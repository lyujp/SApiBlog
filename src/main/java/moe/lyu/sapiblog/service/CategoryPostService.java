package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
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

    public List<CategoryPost> listByPostId(Integer postId) {
        LambdaQueryChainWrapper<CategoryPost> categoryPostLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryPostMapper);
        return categoryPostLambdaQueryChainWrapper.eq(CategoryPost::getPostId, postId).orderByDesc(CategoryPost::getId).list();
    }

    public void add(Integer postId, Integer categoryId) throws CategoryAddFailedException,
            PostNotExistException,
            CategoryNotFoundException {
        LambdaQueryChainWrapper<CategoryPost> categoryPostLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryPostMapper);
        CategoryPost one = categoryPostLambdaQueryChainWrapper.eq(CategoryPost::getPostId, postId).eq(CategoryPost::getCategoryId, categoryId).one();
        if (one != null) {
            return;
        }

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        Post post = postLambdaQueryChainWrapper.eq(Post::getId, postId).one();
        if (post == null) {
            throw new PostNotExistException("Post id " + postId + " not exist");
        }

        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category category = categoryLambdaQueryChainWrapper.eq(Category::getId, categoryId).one();
        if (category == null) {
            throw new CategoryNotFoundException("Category id " + categoryId + " not exist");
        }

        CategoryPost categoryPost = new CategoryPost();
        categoryPost.setCategoryId(categoryId);
        categoryPost.setPostId(postId);
        int insert = categoryPostMapper.insert(categoryPost);
        if (insert == 0) {
            throw new CategoryAddFailedException("Unknown reason");
        }
    }

    public void deleteByPostId(Integer postId) {
        if (postId == null) return;
        LambdaUpdateChainWrapper<CategoryPost> categoryPostLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryPostMapper);
        categoryPostLambdaUpdateChainWrapper.eq(CategoryPost::getPostId, postId).remove();
    }

    public void deleteByCategoryId(Integer categoryId) {
        if (categoryId == null) return;
        LambdaUpdateChainWrapper<CategoryPost> categoryPostLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryPostMapper);
        categoryPostLambdaUpdateChainWrapper.eq(CategoryPost::getCategoryId, categoryId).remove();
    }
}
