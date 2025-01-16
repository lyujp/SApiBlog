package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.CategoryPost;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import moe.lyu.sapiblog.mapper.CategoryPostDtoMapper;
import moe.lyu.sapiblog.mapper.CategoryPostMapper;
import moe.lyu.sapiblog.mapper.PostMapper;
import moe.lyu.sapiblog.vo.CategoryPostVo;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryPostService {

    private final SqlSessionFactory sqlSessionFactory;
    CategoryPostDtoMapper categoryPostDtoMapper;
    CategoryPostMapper categoryPostMapper;
    PostMapper postMapper;
    CategoryMapper categoryMapper;

    @Autowired
    public CategoryPostService(CategoryPostDtoMapper categoryPostDtoMapper,
                               CategoryPostMapper categoryPostMapper,
                               SqlSessionFactory sqlSessionFactory,
                               PostMapper postMapper,
                               CategoryMapper categoryMapper) {
        this.categoryPostDtoMapper = categoryPostDtoMapper;
        this.categoryPostMapper = categoryPostMapper;
        this.sqlSessionFactory = sqlSessionFactory;
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryPostVo> list(Integer categoryId) {
        return categoryPostDtoMapper.get(categoryId);
    }

    public Integer add(Integer postId, Integer categoryId) {
        CategoryPost categoryPost = new CategoryPost();
        categoryPost.setCategoryId(categoryId);
        categoryPost.setPostId(postId);
        return addCategoryPost(List.of(categoryPost));
    }

    public Integer add(Integer postId, List<Integer> categoryIds) {
        List<CategoryPost> categoryPosts = categoryIds.stream()
                .map(categoryId -> {
                    CategoryPost categoryPost = new CategoryPost();
                    categoryPost.setCategoryId(categoryId);
                    categoryPost.setPostId(postId);
                    return categoryPost;
                })
                .toList();
        return addCategoryPost(categoryPosts);
    }

    public Integer add(CategoryPost categoryPost) {
        return addCategoryPost(List.of(categoryPost));
    }

    private Integer addCategoryPost(List<CategoryPost> categoryPosts) throws PostNotExistException {
        if (categoryPosts == null || categoryPosts.isEmpty()) {
            return 0;
        }

        Integer postId = categoryPosts.get(0).getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new PostNotExistException(postId.toString());
        }

        List<Integer> categoryIds = categoryPosts.stream().map(CategoryPost::getCategoryId).toList();
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.in(Category::getId, categoryIds);

        List<Integer> cids = categoryMapper.selectList(categoryLambdaQueryWrapper).stream().map(Category::getId).toList();

        List<CategoryPost> list = categoryPosts.stream().filter(cp -> cids.contains(cp.getCategoryId())).toList();

        MybatisBatch<CategoryPost> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, list);
        MybatisBatch.Method<CategoryPost> method = new MybatisBatch.Method<>(CategoryPost.class);
        List<BatchResult> batchResults = mybatisBatch.execute(method.insert());
        return batchResults.stream().flatMapToInt(br -> Arrays.stream(br.getUpdateCounts())).sum();
    }

    public Integer deleteByPostId(Integer postId) {
        LambdaQueryWrapper<CategoryPost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CategoryPost::getPostId, postId);
        return categoryPostMapper.delete(lambdaQueryWrapper);
    }

    public Integer deleteByCategoryId(Integer categoryId) {
        LambdaQueryWrapper<CategoryPost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CategoryPost::getCategoryId, categoryId);
        return categoryPostMapper.delete(lambdaQueryWrapper);
    }

    public Integer deleteByCategoryPostId(Integer categoryPostId) {
        return categoryPostMapper.deleteById(categoryPostId);
    }
}
