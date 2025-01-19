package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.lyu.sapiblog.entity.CategoryPost;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import moe.lyu.sapiblog.mapper.CategoryPostDtoMapper;
import moe.lyu.sapiblog.mapper.CategoryPostMapper;
import moe.lyu.sapiblog.mapper.PostMapper;
import moe.lyu.sapiblog.vo.CategoryPostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryPostService {

    CategoryPostDtoMapper categoryPostDtoMapper;
    CategoryPostMapper categoryPostMapper;
    PostMapper postMapper;
    CategoryMapper categoryMapper;
    ICategoryPostService iCategoryPostService;

    @Autowired
    public CategoryPostService(CategoryPostDtoMapper categoryPostDtoMapper,
                               CategoryPostMapper categoryPostMapper,
                               PostMapper postMapper,
                               CategoryMapper categoryMapper,
                               ICategoryPostService iCategoryPostService) {
        this.categoryPostDtoMapper = categoryPostDtoMapper;
        this.categoryPostMapper = categoryPostMapper;
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
        this.iCategoryPostService = iCategoryPostService;
    }

    public List<CategoryPostVo> list(Integer categoryId) {
        return categoryPostDtoMapper.get(categoryId);
    }

    public Boolean add(Integer postId, Integer categoryId) {
        CategoryPost categoryPost = new CategoryPost();
        categoryPost.setCategoryId(categoryId);
        categoryPost.setPostId(postId);
        return addCategoryPost(List.of(categoryPost));
    }

    public Boolean add(Integer postId, List<Integer> categoryIds) {
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

    public Boolean add(CategoryPost categoryPost) {
        return addCategoryPost(List.of(categoryPost));
    }

    private Boolean addCategoryPost(List<CategoryPost> categoryPosts) throws PostNotExistException {
        if (categoryPosts == null || categoryPosts.isEmpty()) {
            return false;
        }

        Integer postId = categoryPosts.get(0).getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new PostNotExistException(postId.toString());
        }

        return iCategoryPostService.saveOrUpdateBatch(categoryPosts);
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

    public List<CategoryPost> getByPostId(Integer postId) {
        return categoryPostMapper.selectList(new LambdaQueryWrapper<CategoryPost>().eq(CategoryPost::getPostId, postId));
    }
}
