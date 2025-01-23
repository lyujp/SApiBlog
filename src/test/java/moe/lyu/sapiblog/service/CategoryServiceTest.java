package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.mapper.CategoryMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        LambdaUpdateChainWrapper<Category> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryMapper);
        categoryLambdaUpdateChainWrapper.likeRight(Category::getName, "__Test").remove();
    }

    @Test
    void list() throws JsonProcessingException {
        categoryService.add("__Test List");
        List<Category> categories = categoryService.list(1, 10, true);
        List<String> categoryNames = categories.stream().map(Category::getName).toList();
        assertTrue(categoryNames.contains("__Test List"));
    }

    @Test
    void add() throws JsonProcessingException {
        Category category = new Category();
        category.setName("__Test Add");
        Category added = categoryService.add(category);
        categoryService.add(category);

        assertEquals("__Test Add", added.getName());

        Category added2 = categoryService.add("__Test Add 2");
        assertEquals("__Test Add 2", added2.getName());
    }

    @Test
    void update() throws JsonProcessingException {
        Category categoryNull = null;
        Category categoryIdNull = new Category();
        categoryIdNull.setName("__Test Update Null");
        Category categoryNameNull = new Category();
        categoryNameNull.setId(9999);
        Category category = new Category();
        category.setName("__Test Update Name with id");
        category.setId(9999);

        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.update(categoryNull));
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.update(categoryIdNull));
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.update(categoryNameNull));
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.update(category));

        categoryService.add("__Test Update");
        Category testUpdate = categoryService.getByName("__Test Update");
        testUpdate.setName("__Test Update2");
        categoryService.update(testUpdate);

        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.getByName("__Test Update"));
        Category testUpdate2 = categoryService.getByName("__Test Update2");
        assertEquals("__Test Update2", testUpdate2.getName());

    }

    @Test
    void delete() throws JsonProcessingException {

        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.delete(9999));
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.delete("__Test Delete"));
        Category category = categoryService.add("__Test Delete 1");
        Category category2 = categoryService.add("__Test Delete 2");

        Category categoryDb1 = categoryService.getByName(category.getName());
        Category categoryDb2 = categoryService.getByName(category2.getName());

        assertEquals("__Test Delete 1", categoryDb1.getName());
        assertEquals("__Test Delete 2", categoryDb2.getName());
        categoryService.delete(category.getName());
        categoryService.delete(category2.getName());

        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.getByName(category.getName()));
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.getByName(category2.getName()));
    }

    @Test
    void getByName() throws JsonProcessingException {
        categoryService.add("__Test Get");
        assertEquals("__Test Get", categoryService.getByName("__Test Get").getName());
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryService.getByName("__Test Get 2"));
    }
}