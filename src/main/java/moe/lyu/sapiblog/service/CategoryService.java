package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.dto.CategoryTreeDto;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.CategoryPost;
import moe.lyu.sapiblog.exception.CategoryAddFailedException;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.CategoryUnknownException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import moe.lyu.sapiblog.mapper.CategoryPostMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .orderBy(true, !desc,Category::getId).page(page).getRecords();
    }

    public List<Category> listByPostId(Integer postId) {
        if (postId == null) postId = 0;
        LambdaQueryChainWrapper<CategoryPost> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryPostMapper);
        List<Integer> categoryIds = categoryLambdaQueryChainWrapper.eq(CategoryPost::getPostId, postId).list()
                                       .stream().map(CategoryPost::getCategoryId).toList();
        LambdaQueryChainWrapper<Category> categoryLambdaWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        return categoryLambdaWrapper.in(Category::getId, categoryIds).list();
    }

    public Category getByUniqName(String uniqName) throws CategoryNotFoundException {
        if(uniqName == null || uniqName.isEmpty()) throw new CategoryNotFoundException("Uniq name can't be empty");
        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category category = categoryLambdaQueryChainWrapper.eq(Category::getUniqName, uniqName).one();
        if(category == null) {
            throw new CategoryNotFoundException(uniqName);
        }
        return category;
    }

    public Category getByName(String name) throws CategoryNotFoundException {
        if(name == null || name.isEmpty()) throw new CategoryNotFoundException("Name can't be empty");
        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category category = categoryLambdaQueryChainWrapper.eq(Category::getName, name).one();
        if(category == null) {
            throw new CategoryNotFoundException(name);
        }
        return category;
    }

    public Category add(Category category) throws JsonProcessingException, CategoryAlreadyExistException, CategoryAddFailedException, CategoryUnknownException {
        if(category == null) throw new CategoryAddFailedException("Category can't be null");
        if (category.getName() == null || category.getName().isEmpty()) throw new CategoryAddFailedException("Category name can't be empty");
        if (category.getUniqName() == null || category.getUniqName().isEmpty()) throw new CategoryAddFailedException("Category uniqName can't be empty");

        LambdaQueryChainWrapper<Category> categoryByNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category categoryByName = categoryByNameLambdaQueryWrapper.eq(Category::getName, category.getName()).one();
        if(categoryByName != null) {
            return category;
        }

        LambdaQueryChainWrapper<Category> categoryByUniqNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category categoryByUniqName = categoryByUniqNameLambdaQueryWrapper.eq(Category::getUniqName, category.getUniqName()).one();
        if(categoryByUniqName != null) {
            return category;
        }

        int insert = categoryMapper.insert(category);
        if(insert == 0) {
            throw new CategoryAddFailedException("Unknown Exception");
        }

        categoryByUniqNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category one = categoryByUniqNameLambdaQueryWrapper.eq(Category::getUniqName, category.getUniqName()).one();
        if(one == null) {
            throw new CategoryUnknownException("Unknown Exception");
        }
        return one;
    }

    public Category add(String name) throws JsonProcessingException, CategoryUnknownException, CategoryAddFailedException {
        if (name == null || name.isEmpty()) throw new CategoryAddFailedException("Category name can't be empty");

        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category category = categoryLambdaQueryChainWrapper.eq(Category::getName, name).one();
        if(category != null) {
            return category;
        }
        String uniqName = name.toLowerCase().trim();

        categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        List<String> categoryUniqNames = categoryLambdaQueryChainWrapper.likeRight(Category::getUniqName, uniqName).list()
                                    .stream().map(Category::getUniqName).toList();


        while (categoryUniqNames.contains(uniqName)) {
            uniqName += "_";
        }

        LambdaUpdateChainWrapper<Category> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryMapper);
        categoryLambdaUpdateChainWrapper.set(Category::getUniqName, uniqName)
                .set(Category::getName, name)
                .set(Category::getParentId, 0).update();

        categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category one = categoryLambdaQueryChainWrapper.eq(Category::getName, name).one();
        if(one == null) {
            throw new CategoryUnknownException("Unknown Exception");
        }
       return one;
    }

    public Category update(Category category) throws CategoryNotFoundException,CategoryUnknownException {
        if(category == null) throw new CategoryNotFoundException("Category can't be null");
        if(category.getId() == null) throw new CategoryNotFoundException("Category id can't be null");

        LambdaUpdateChainWrapper<Category> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryMapper);
        boolean update = categoryLambdaUpdateChainWrapper.setEntity(category).update();
        if(!update) {
            throw new CategoryNotFoundException(category.getId().toString());
        }

        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category one = categoryLambdaQueryChainWrapper.eq(Category::getUniqName, category.getUniqName()).one();
        if(one == null) {
            throw new CategoryUnknownException("Unknown Exception");
        }
        return one;
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        if (id == null) return;

        LambdaUpdateChainWrapper<Category> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryMapper);
        boolean remove = categoryLambdaUpdateChainWrapper.eq(Category::getId, id).remove();
        if(!remove){
            throw new CategoryNotFoundException(id.toString() + " does not exist");
        }
    }

    public CategoryTreeDto tree(Integer categoryId) throws CategoryNotFoundException {
        if (categoryId == null) categoryId = 0;
        CategoryTreeDto treeDto = new CategoryTreeDto();
        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Category category = categoryLambdaQueryChainWrapper.eq(Category::getId, categoryId).one();
        if (category == null) {
            throw new CategoryNotFoundException(categoryId + " not found");
        }

        BeanUtils.copyProperties(category, treeDto);
        if (category.getParentId() != 0) {
            treeDto.setParentCategory(tree(category.getParentId()));
        }
        return treeDto;
    }
}
