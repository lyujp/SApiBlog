package moe.lyu.sapiblog;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.service.CategoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    Integer testCategoryId;

    @Test
    void testMain() throws JsonProcessingException {
        testSetup();

        testAdd();
        testList();
        testUpdate();
        testDelete();

        testCleanUp();
    }

    void testSetup() {
        try{
            Category test = categoryService.getByUniqName("__test");
            categoryService.delete(test.getId());

        }catch (CategoryNotFoundException e){}
        try{
            Category test = categoryService.getByUniqName("__test2");
            categoryService.delete(test.getId());
        }catch (CategoryNotFoundException e){}
    }


    void testCleanUp(){
        try{
            Category test = categoryService.getByUniqName("__test");
            categoryService.delete(test.getId());

        }catch (CategoryNotFoundException e){}
        try{
            Category test = categoryService.getByUniqName("__test2");
            categoryService.delete(test.getId());
        }catch (CategoryNotFoundException e){}
    }

    void testAdd() throws JsonProcessingException {
        Category category = new Category();
        category.setName("__test");
        category.setUniqName("__test");
        Category added = categoryService.add(category);
        this.testCategoryId = added.getId();
        assertEquals("__test", added.getUniqName());
        assertEquals("__test", added.getName());
        assertThrows(CategoryAlreadyExistException.class, () -> categoryService.add(category));
    }


    void testList(){
        AtomicReference<Boolean> findTestCategory = new AtomicReference<>(false);
        List<Category> result = categoryService.list(true, "id");
        result.forEach(category -> {
            if(Objects.equals(category.getId(), testCategoryId)){
                findTestCategory.set(true);
            }
        });
        assertTrue(findTestCategory.get());
        assertDoesNotThrow(() -> categoryService.getById(this.testCategoryId));
        assertDoesNotThrow(() -> categoryService.getByUniqName("__test"));
    }


    void testUpdate(){
        Category category = new Category();
        category.setId(this.testCategoryId);
        category.setName("__test2");
        category.setUniqName("__test2");
        category.setParentId(1);
        Category update = categoryService.update(category);
        assertNotNull(update);
        assertEquals(this.testCategoryId, update.getId());
        Category updated = categoryService.getById(this.testCategoryId);
        assertEquals("__test2", updated.getName());
        assertEquals("__test2", updated.getUniqName());
        assertEquals(1,updated.getParentId());
        category.setName("__test");
        category.setUniqName("__test");
        category.setId(this.testCategoryId+1);
        assertThrows(CategoryNotFoundException.class, () -> categoryService.update(category));
    }

    void testDelete(){
        Boolean delete = categoryService.delete(this.testCategoryId);
        assertTrue(delete);
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getById(this.testCategoryId));
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getByUniqName("__test2"));
    }

}
