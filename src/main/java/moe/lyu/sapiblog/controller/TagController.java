package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.exception.*;
import moe.lyu.sapiblog.service.TagPostService;
import moe.lyu.sapiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tag")
@AuthCheck()
public class TagController {

    TagService tagService;
    TagPostService tagPostService;

    @Autowired
    public TagController(TagService tagService,
                         TagPostService tagPostService) {
        this.tagService = tagService;
        this.tagPostService = tagPostService;
    }

    @AuthCheck(skipCheck = true)
    @GetMapping("/list")
    public Resp listAllTags(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "desc", required = false, defaultValue = "true") Boolean desc
    ) {
        List<Tag> categories = tagService.list(currentPage, pageSize, desc);
        return Resp.success(categories);
    }

    @GetMapping("/list/post_id/{post_id}")
    @AuthCheck(skipCheck = true)
    public Resp listByPostId(@PathVariable(value = "post_id") Integer postId) {
        List<Tag> categories = tagService.listByPostId(postId);
        return Resp.success(categories);
    }

    @GetMapping("/get/{uniq_name}")
    @AuthCheck(skipCheck = true)
    public Resp getByUniqName(@PathVariable String uniq_name) throws TagNotFoundException {
        Tag tag = tagService.getByUniqName(uniq_name);
        return Resp.success(tag);
    }

    @PostMapping("/add")
    public Resp add(@RequestBody Map<String, String> arg) throws JsonProcessingException, TagAlreadyExistException, TagAddFailedException {
        Tag tag = new Tag();
        try {
            tag.setName(arg.get("name"));
            tag.setUniqName(arg.get("uniq_name"));
        } catch (Exception e) {
            return Resp.error(-200, "Tag args is invalid", arg);
        }
        Tag added = tagService.add(tag);
        return Resp.success(added);

    }

    @PostMapping("/add/name/{name}")
    public Resp addQuick(@PathVariable String name) throws JsonProcessingException, TagUnknownException {
        Tag added = tagService.add(name);
        return Resp.success(added);
    }

    @PostMapping("/update")
    public Resp update(@RequestBody Tag tag) throws TagNotFoundException, TagUnknownException {
        Tag update = tagService.update(tag);
        return Resp.success(update);
    }

    @PostMapping("/delete/id/{tag_id}")
    public Resp deleteById(@PathVariable Integer tag_id) throws TagNotFoundException {
        tagService.delete(tag_id);
        return Resp.success();
    }

    @PostMapping("/delete/uniq_name/{uniq_name}")
    public Resp deleteByUniqName(@PathVariable String uniq_name) throws TagNotFoundException {
        Tag tag = tagService.getByUniqName(uniq_name);
        tagService.delete(tag.getId());
        return Resp.success();
    }

    @PostMapping("/delete/name/{name}")
    public Resp deleteByName(@PathVariable String name) throws TagNotFoundException {
        Tag tag = tagService.getByName(name);
        tagService.delete(tag.getId());
        return Resp.success();
    }
}
