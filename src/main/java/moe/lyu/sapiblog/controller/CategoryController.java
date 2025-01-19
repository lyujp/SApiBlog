package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.CategoryTreeDto;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Category;
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
@AuthCheck()
public class CategoryController {

    CategoryService categoryService;
    CategoryPostService categoryPostService;

    @Autowired
    public CategoryController(CategoryService categoryService,
                              CategoryPostService categoryPostService) {
        this.categoryService = categoryService;
        this.categoryPostService = categoryPostService;
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/list")
    public Resp list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "desc", required = false, defaultValue = "true") Boolean desc
    ) {
        List<Category> categories = categoryService.list(page, size, desc);
        return Resp.success(categories);
    }

    @GetMapping("/list/{post_id}")
    @AuthCheck(skipCheck = true)
    public Resp listByPostId(
            @PathVariable(value = "post_id") Integer postId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "desc", required = false, defaultValue = "true") Boolean desc
    ) {
        List<Category> categories = categoryService.listByPostId(postId, page, size, desc);
        return Resp.success(categories);
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/get_by_id/{id}")
    public Resp get(@PathVariable(value = "id") Integer id) throws CategoryNotFoundException {
        Category category = categoryService.getById(id);
        return Resp.success(category);
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/get_by_uniq_name/{uniq_name}")
    public Resp getByUniqName(@PathVariable(value = "uniq_name") String uniq_name) throws CategoryNotFoundException {
        Category category = categoryService.getByUniqName(uniq_name);
        return Resp.success(category);
    }

    @PostMapping("/add")
    public Resp add(@RequestBody Map<String, String> arg) throws JsonProcessingException, CategoryAlreadyExistException {
        Category category = new Category();
        try {
            category.setName(arg.get("name"));
            category.setUniqName(arg.get("uniq_name"));
            category.setParentId(Integer.parseInt(arg.get("parent_id")));
        } catch (Exception e) {
            return Resp.error(-200, "Category args is invalid", arg);
        }
        Category added = categoryService.add(category);
        return Resp.success(added);

    }

    @PostMapping("/add")
    public Resp addQuick(@RequestBody String name) throws JsonProcessingException, CategoryUnknownException {
        Category added = categoryService.add(name);
        return Resp.success(added);
    }

    @PostMapping("/update")
    public Resp update(@RequestBody Category category) throws CategoryNotFoundException {
        Category update = categoryService.update(category);
        return Resp.success(update);
    }

    @PostMapping("/delete")
    public Resp detete(@RequestBody Integer ids) throws CategoryNotFoundException {
        Boolean delete = categoryService.delete(ids);
        if (delete) {
            return Resp.success();
        } else {
            return Resp.error(-200, "Category delete failed");
        }
    }

    @GetMapping("/tree/{category_id}")
    @AuthCheck(skipCheck = true)
    public Resp tree(@PathVariable Integer category_id) throws CategoryNotFoundException {
        CategoryTreeDto tree = categoryService.tree(category_id);
        return Resp.success(tree);
    }
}
