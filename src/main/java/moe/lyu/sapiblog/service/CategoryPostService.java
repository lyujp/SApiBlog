package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.CategoryPost;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.CategoryAddFailedException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
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

    public List<CategoryPost> listByPostId(Integer postId, Integer currentPage, Integer pageSize, Boolean desc) {
        Page<CategoryPost> page = new Page<>(currentPage, pageSize);
        QueryWrapper<CategoryPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true, !desc, "id");
        queryWrapper.eq("post_id", postId);
        page = categoryPostMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    public void add(Integer postId, Integer categoryId) throws CategoryAddFailedException,
            PostNotExistException,
            CategoryNotFoundException {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new PostNotExistException("Post id " + postId + " not exist");
        }

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException("Category id " + categoryId + " not exist");
        }

        CategoryPost categoryPost = new CategoryPost();
        categoryPost.setCategoryId(categoryId);
        categoryPost.setPostId(postId);
        int insert = categoryPostMapper.insert(categoryPost);
        if (insert == 0) {
            LambdaQueryWrapper<CategoryPost> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CategoryPost::getCategoryId, categoryId);
            queryWrapper.eq(CategoryPost::getPostId, postId);
            List<CategoryPost> categoryPosts = categoryPostMapper.selectList(queryWrapper);
            if (categoryPosts.isEmpty()) {
                throw new CategoryAddFailedException("Unknown reason");
            }
            throw new CategoryAddFailedException("Post already add to category");
        }
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
}
