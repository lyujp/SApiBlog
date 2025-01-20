package moe.lyu.sapiblog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void list() {
        List<Category> categories = categoryService.list(1, 10, true);
    }

    @Test
    void listByPostId() {
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

        categoryService.delete("__Test Add");
        categoryService.delete("__Test Add 2");

    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void tree() {
    }
}