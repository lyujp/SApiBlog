package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryFieldNotFoundException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@AuthCheck()
public class CategoryController {

    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/list")
    public Resp list(@RequestParam(value = "desc", required = false, defaultValue = "true") Boolean orderByDesc,
                     @RequestParam(value = "field", required = false, defaultValue = "id") String orderByField
    ) throws CategoryFieldNotFoundException {
        List<Category> categoryList = categoryService.list(orderByDesc, orderByField);
        return Resp.success(categoryList);
    }

    @PostMapping("/add")
    public Resp add(@RequestBody Category category) throws CategoryAlreadyExistException, JsonProcessingException {
        Category addedCategory = categoryService.add(category);
        return Resp.success(addedCategory);
    }

    @PostMapping("/update")
    public Resp update(@RequestBody Category category) throws CategoryNotFoundException {
        Category update = categoryService.update(category);
        return Resp.success(update);
    }

    @PostMapping("/delete/{id}")
    public Resp detete(@PathVariable String id) throws CategoryNotFoundException {
        try {
            categoryService.delete(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            throw new CategoryNotFoundException(id);
        }
        return Resp.success();
    }
}
