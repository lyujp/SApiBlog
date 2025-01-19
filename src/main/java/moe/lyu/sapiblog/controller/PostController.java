package moe.lyu.sapiblog.controller;

import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.PostWithoutContentDto;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.*;
import moe.lyu.sapiblog.exception.*;
import moe.lyu.sapiblog.service.*;
import moe.lyu.sapiblog.vo.CategoryPostVo;
import moe.lyu.sapiblog.vo.TagPostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final CategoryService categoryService;
    private final TagService tagService;
    PostService postService;
    CategoryPostService categoryPostService;
    TagPostService tagPostService;

    @Autowired
    public PostController(PostService postService,
                          CategoryPostService categoryPostService,
                          TagPostService tagPostService, CategoryService categoryService, TagService tagService) {
        this.postService = postService;
        this.categoryPostService = categoryPostService;
        this.tagPostService = tagPostService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @AuthCheck
    @PostMapping("/update")
    public Resp update(@RequestParam Post post) throws PostNotExistException {
        Post update = postService.update(post);
        return Resp.success(update);
    }

    @AuthCheck
    @PostMapping("/delete")
    public Resp delete(@RequestBody Integer postId) {
        postService.delete(postId);
        return Resp.success();
    }

    @GetMapping("/id/{postId}")
    public Resp get(@PathVariable Integer postId) {
        Post post = postService.getById(postId);
        return Resp.success(post);
    }

    @GetMapping("/list")
    public Resp list( @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
                      @RequestParam(value = "desc", required = false, defaultValue = "true") Boolean desc) {
        List<PostWithoutContentDto> list = postService.list(currentPage, pageSize, desc);
        return Resp.success(list);
    }

    @GetMapping("/list/category_id/{category_id}")
    public Resp listByCategoryId(@PathVariable Integer category_id) {
        List<CategoryPostVo> list = categoryPostService.list(category_id);
        return Resp.success(list);
    }

    @GetMapping("/list/tag_id/{tag_id}")
    public Resp listByTagId(@PathVariable Integer tag_id) {
        List<TagPostVo> list = tagPostService.list(tag_id);
        return Resp.success(list);
    }

    @GetMapping("/list/category/{post_id}")
    public Resp listCategory(@PathVariable Integer post_id) {
        List<CategoryPost> list = categoryPostService.listByPostId(post_id);
        return Resp.success(list);
    }

    @GetMapping("/list/tag/{post_id}")
    public Resp listTag(@PathVariable Integer post_id) {
        List<TagPost> list = tagPostService.listByPostId(post_id);
        return Resp.success(list);
    }

    @PostMapping("/add/category/{post_id}/{category_id}")
    @AuthCheck
    public Resp addCategory(@PathVariable Integer post_id,@PathVariable Integer category_id)
            throws CategoryAddFailedException,
            PostNotExistException,
            CategoryNotFoundException {
        categoryPostService.add(post_id,category_id);
        return Resp.success();
    }

    @PostMapping("/add/category_uniq_name/{post_id}/{uniq_name}")
    @AuthCheck
    public Resp addCategoryByUniqName(@PathVariable Integer post_id,@PathVariable String uniq_name)
            throws CategoryAddFailedException,
            PostNotExistException,
            CategoryNotFoundException{
        Category category = categoryService.getByUniqName(uniq_name);
        categoryPostService.add(post_id,category.getId());
        return Resp.success();
    }

    @PostMapping("/add/tag/{post_id}/{tag_id}")
    @AuthCheck
    public Resp addTag(@PathVariable Integer post_id,@PathVariable Integer tag_id)
            throws TagAddFailedException,
            PostNotExistException,
            TagNotFoundException {
        tagPostService.add(post_id,tag_id);
        return Resp.success();
    }

    @PostMapping("/add/tag_uniq_name/{post_id}/{uniq_name}")
    @AuthCheck
    public Resp addTagByUniqName(@PathVariable Integer post_id,@PathVariable String uniq_name)
            throws TagAddFailedException,
            PostNotExistException,
            TagNotFoundException {
        Tag tag = tagService.getByUniqName(uniq_name);
        tagPostService.add(post_id,tag.getId());
        return Resp.success();
    }
}
