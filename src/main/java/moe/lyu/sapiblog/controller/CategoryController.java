package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.exception.CategoryAddFailedException;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.CategoryUnknownException;
import moe.lyu.sapiblog.service.CategoryPostService;
import moe.lyu.sapiblog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    CategoryService categoryService;
    CategoryPostService categoryPostService;

    @Autowired
    public CategoryController(CategoryService categoryService,
                              CategoryPostService categoryPostService) {
        this.categoryService = categoryService;
        this.categoryPostService = categoryPostService;
    }

    @GetMapping("/list")
    public Resp listAllCategories(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "desc", required = false, defaultValue = "true") Boolean desc
    ) {
        List<Category> categories = categoryService.list(currentPage, pageSize, desc);
        return Resp.success(categories);
    }

    @GetMapping("/list/{post_id}")
    public Resp listByPostId(@PathVariable(value = "post_id") Integer postId) {
        List<Category> categories = categoryService.listByPostId(postId);
        return Resp.success(categories);
    }

    @GetMapping("/get/name/{name}")
    public Resp getByName(@PathVariable String name) throws CategoryNotFoundException {
        Category category = categoryService.getByName(name);
        return Resp.success(category);
    }

    @AuthCheck
    @PostMapping("/add")
    public Resp add(@RequestBody Map<String, String> arg)
            throws JsonProcessingException, CategoryAlreadyExistException, CategoryAddFailedException, CategoryUnknownException {
        Category category = new Category();
        try {
            category.setName(arg.get("name"));
            if(arg.get("parent_id") != null) {
                category.setParentId(Integer.valueOf(arg.get("parent_id")));
            }

        } catch (Exception e) {
            return Resp.error(-200, "Category args is invalid", arg);
        }
        Category added = categoryService.add(category);
        return Resp.success(added);

    }

    @AuthCheck
    @PostMapping("/add/name/{name}")
    public Resp addQuick(@PathVariable String name) throws JsonProcessingException, CategoryUnknownException, CategoryAddFailedException {
        Category added = categoryService.add(name);
        return Resp.success(added);
    }

    @AuthCheck
    @PostMapping("/update")
    public Resp update(@RequestBody Category category) throws CategoryNotFoundException, CategoryUnknownException, JsonProcessingException {
        Category update = categoryService.update(category);
        return Resp.success(update);
    }

    @AuthCheck(jwtDbCheck = true)
    @PostMapping("/delete/id/{category_id}")
    public Resp deleteById(@PathVariable Integer category_id) throws CategoryNotFoundException {
        categoryService.delete(category_id);
        return Resp.success();
    }

    @AuthCheck(jwtDbCheck = true)
    @PostMapping("/delete/name/{name}")
    public Resp deleteByName(@PathVariable String name) throws CategoryNotFoundException {
        categoryService.delete(name);
        return Resp.success();
    }
}
