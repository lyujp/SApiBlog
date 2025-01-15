package moe.lyu.sapiblog;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.service.CategoryPostService;
import moe.lyu.sapiblog.service.CategoryService;
import moe.lyu.sapiblog.vo.CategoryPostVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CategoryPostServiceTest {

    CategoryPostService categoryPostService;

    CategoryService categoryService;

    Integer testCategoryId;

    @Autowired
    public CategoryPostServiceTest(CategoryPostService categoryPostService,
                                   CategoryService categoryService
    ) {
        this.categoryPostService = categoryPostService;
        this.categoryService = categoryService;
    }

    @Test
    void main() throws JsonProcessingException {
        testSetup();

        this.testCategoryId = 10;

        list();

        testCleanUp();
    }

    void testSetup() {
        try {
            Category test = categoryService.getByUniqName("__test");
            categoryService.delete(test.getId());
        } catch (CategoryNotFoundException ignored) {}

    }


    void testCleanUp() {
        try {
            Category test = categoryService.getByUniqName("__test");
            categoryService.delete(test.getId());

        } catch (CategoryNotFoundException ignored) {
        }
    }

    void list(){
        List<CategoryPostVo> list = categoryPostService.list(this.testCategoryId);
        System.out.println(list);
    }
}
