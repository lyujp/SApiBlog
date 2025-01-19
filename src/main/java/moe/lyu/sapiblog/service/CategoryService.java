package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.dto.CategoryTreeDto;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.CategoryUnknownException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> list(Integer currentPage, Integer pageSize, Boolean desc) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.orderBy(true, !desc, "id");
        page = categoryMapper.selectPage(page, categoryQueryWrapper);
        return page.getRecords();
    }

    public List<Category> listByPostId(Integer postId, Integer currentPage, Integer pageSize, Boolean desc) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("post_id", postId);
        categoryQueryWrapper.orderBy(true, !desc, "id");
        page = categoryMapper.selectPage(page, categoryQueryWrapper);
        return page.getRecords();
    }

    public Category getById(Integer id) throws CategoryNotFoundException {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId, id);
        Category category = categoryMapper.selectOne(categoryLambdaQueryWrapper);
        if (category == null) {
            throw new CategoryNotFoundException(id.toString() + " not found");
        } else {
            return category;
        }
    }

    public Category getByUniqName(String name) throws CategoryNotFoundException {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getUniqName, name);
        Category category = categoryMapper.selectOne(categoryLambdaQueryWrapper);
        if (category == null) {
            throw new CategoryNotFoundException(name + "not found");
        } else {
            return category;
        }
    }

    public Category add(Category category) throws JsonProcessingException, CategoryAlreadyExistException {
        if (categoryMapper.selectById(category.getId()) == null) {
            throw new CategoryAlreadyExistException(category.getId().toString() + " already exist");
        }
        try {
            int effectRows = categoryMapper.insert(category);
            if (effectRows == 1) {
                return category;
            }
        } catch (Exception ignored) {
        }
        throw new CategoryUnknownException(new ObjectMapper().writeValueAsString(category));
    }

    public Category add(String name) throws JsonProcessingException, CategoryUnknownException {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.likeRight(Category::getName, name);
        List<String> categoryUniqNames = categoryMapper.selectList(categoryLambdaQueryWrapper).stream().map(Category::getUniqName).toList();
        String uniqName = name.toLowerCase().trim();
        while (categoryUniqNames.contains(uniqName)) {
            uniqName += "_";
        }

        Category category = new Category();
        category.setName(name);
        category.setUniqName(uniqName);
        category.setParentId(0);
        try {
            int effectRows = categoryMapper.insert(category);
            if (effectRows == 1) {
                return category;
            }
        } catch (Exception ignored) {
        }
        throw new CategoryUnknownException(new ObjectMapper().writeValueAsString(category));
    }

    public Category update(Category category) throws CategoryNotFoundException {
        int effectRows = categoryMapper.updateById(category);
        if (effectRows == 1) {
            return category;
        } else {
            throw new CategoryNotFoundException(category.getId().toString());
        }
    }

    public Boolean delete(Integer id) throws CategoryNotFoundException {
        int effectRows = categoryMapper.deleteById(id);
        return effectRows == 1;
    }

    public CategoryTreeDto tree(Integer categoryId) throws CategoryNotFoundException {
        CategoryTreeDto treeDto = new CategoryTreeDto();
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new CategoryNotFoundException(categoryId.toString() + " not found");
        }

        BeanUtils.copyProperties(category, treeDto);
        if (category.getParentId() != 0) {
            treeDto.setParentCategory(tree(category.getParentId()));
        }
        return treeDto;
    }
}
