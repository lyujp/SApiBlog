package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryFieldNotFoundException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.CategoryUnknownException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> list(Boolean orderByDesc, String orderField) throws CategoryFieldNotFoundException {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        Class<Category> categoryClass = Category.class;
        queryWrapper.ge("id", 0);
        try {
            categoryClass.getDeclaredField(orderField);
            if (orderByDesc) {
                queryWrapper.orderByDesc(orderField);
            } else {
                queryWrapper.orderByAsc(orderField);
            }
        } catch (NoSuchFieldException ignored) {
            throw new CategoryFieldNotFoundException(orderField);
        }
        return categoryMapper.selectList(queryWrapper);
    }

    public Category getById(Integer id) throws CategoryNotFoundException {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Category category = categoryMapper.selectOne(queryWrapper);
        if (category == null) {
            throw new CategoryNotFoundException(id.toString());
        } else {
            return category;
        }
    }

    public Category getByUniqName(String name) throws CategoryNotFoundException {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uniq_name", name);
        Category category = categoryMapper.selectOne(queryWrapper);
        if (category == null) {
            throw new CategoryNotFoundException(name);
        } else {
            return category;
        }
    }

    public Category add(Category category) throws CategoryAlreadyExistException, JsonProcessingException {
        try {
            int effectRows = categoryMapper.insert(category);
            if (effectRows == 1) {
                return category;
            } else {
                throw new CategoryUnknownException(new ObjectMapper().writeValueAsString(category));
            }
        } catch (DuplicateKeyException e) {
            throw new CategoryAlreadyExistException(category.getUniqName());
        }

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
}
