package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.CategoryPost;
import moe.lyu.sapiblog.exception.CategoryAddFailedException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.CategoryUnknownException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import moe.lyu.sapiblog.mapper.CategoryPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryPostMapper categoryPostMapper;
    CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper, CategoryPostMapper categoryPostMapper) {
        this.categoryMapper = categoryMapper;
        this.categoryPostMapper = categoryPostMapper;
    }

    public List<Category> list(Integer currentPage, Integer pageSize, Boolean desc) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        return categoryLambdaQueryChainWrapper
                .orderBy(true, !desc, Category::getId).page(page).getRecords();
    }

    public List<Category> listByPostId(Integer postId) {
        if (postId == null) postId = 0;
        LambdaQueryChainWrapper<CategoryPost> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryPostMapper);
        List<Integer> categoryIds = categoryLambdaQueryChainWrapper.eq(CategoryPost::getPostId, postId).list()
                .stream().map(CategoryPost::getCategoryId).toList();
        LambdaQueryChainWrapper<Category> categoryLambdaWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        return categoryLambdaWrapper.in(Category::getId, categoryIds).list();
    }

    public Category getByName(String name) throws CategoryNotFoundException {
        if (name == null || name.isEmpty()) throw new CategoryNotFoundException("Name can't be empty");
        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category category = categoryLambdaQueryChainWrapper.eq(Category::getName, name).one();
        if (category == null) {
            throw new CategoryNotFoundException(name);
        }
        return category;
    }

    public Category add(Category category) throws JsonProcessingException, CategoryAddFailedException, CategoryUnknownException {
        if (category == null) throw new CategoryAddFailedException("Category can't be null");
        if (category.getName() == null || category.getName().isEmpty())
            throw new CategoryAddFailedException("Category name can't be empty");

        LambdaQueryChainWrapper<Category> categoryByNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category categoryByName = categoryByNameLambdaQueryWrapper.eq(Category::getName, category.getName()).one();
        if (categoryByName != null) {
            return categoryByName;
        }

        int insert = categoryMapper.insert(category);
        if (insert == 0) {
            throw new CategoryAddFailedException("Unknown Exception");
        }

        categoryByNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        categoryByName = categoryByNameLambdaQueryWrapper.eq(Category::getName, category.getName()).one();
        if (categoryByName == null) {
            throw new CategoryUnknownException("Unknown Exception");
        }
        return categoryByName;
    }

    public Category add(String name) throws JsonProcessingException, CategoryUnknownException, CategoryAddFailedException {
        if (name == null || name.isEmpty()) throw new CategoryAddFailedException("Category name can't be empty");

        Category category = new Category();
        category.setName(name);

        int insert = categoryMapper.insert(category);
        if (insert == 0) {
            throw new CategoryUnknownException("Unknown Exception");
        }
        return categoryMapper.selectById(category.getId());
    }

    public Category update(Category category) throws CategoryNotFoundException, JsonProcessingException {
        if (category == null) throw new CategoryNotFoundException("Category can't be null");
        if (category.getId() == null) throw new CategoryNotFoundException("Category id can't be null");
        if (category.getName() == null) throw new CategoryNotFoundException("Category name can't be null");

        int i = categoryMapper.updateById(category);
        if (i == 0) {
            throw new CategoryNotFoundException(new ObjectMapper().writeValueAsString(category));
        }
        return categoryMapper.selectById(category.getId());
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        if (id == null) return;

        int i = categoryMapper.deleteById(id);
        if (i == 0) {
            throw new CategoryNotFoundException(id + " does not exist");
        }
    }

    public void delete(String name) throws CategoryNotFoundException {
        if (name == null || name.isEmpty()) return;

        LambdaUpdateChainWrapper<Category> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryMapper);
        boolean remove = categoryLambdaUpdateChainWrapper.eq(Category::getName, name).remove();
        if (!remove) {
            throw new CategoryNotFoundException(name + " does not exist");
        }
    }
}
