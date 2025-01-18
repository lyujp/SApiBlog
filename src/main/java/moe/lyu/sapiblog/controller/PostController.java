package moe.lyu.sapiblog.controller;

import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.PostWithoutContentDto;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@AuthCheck
public class PostController {

    PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/update")
    public Resp update(@RequestParam Post post) {
        Post update = postService.update(post);
        return Resp.success(update);
    }

    @PostMapping("/delete")
    public Resp delete(@RequestBody long postId) {
        postService.delete(postId);
        return Resp.success();
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/id/{postId}")
    public Resp get(@PathVariable long postId) {
        Post post = postService.getById(postId);
        return Resp.success(post);
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/list")
    public Resp list(@RequestParam(value = "true", required = false) boolean desc,
                     @RequestParam(value = "1", required = false) int page,
                     @RequestParam(value = "10", required = false) int size) {
        List<PostWithoutContentDto> list = postService.list(desc, page, size);
        return Resp.success(list);
    }
}
