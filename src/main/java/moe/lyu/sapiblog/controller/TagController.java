package moe.lyu.sapiblog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.exception.TagAlreadyExistException;
import moe.lyu.sapiblog.exception.TagFieldNotFoundException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    TagService TagService;

    @Autowired
    public TagController(TagService TagService) {
        this.TagService = TagService;
    }

    @GetMapping("/list")
    public Resp list(@RequestParam(value = "desc", required = false, defaultValue = "true") Boolean orderByDesc,
                     @RequestParam(value = "field", required = false, defaultValue = "id") String orderByField
    ) throws TagFieldNotFoundException {
        List<Tag> TagList = TagService.list(orderByDesc, orderByField);
        return Resp.success(TagList);
    }

    @PostMapping("/add")
    public Resp add(@RequestBody Tag Tag) throws TagAlreadyExistException, JsonProcessingException {
        Tag addedTag = TagService.add(Tag);
        return Resp.success(addedTag);
    }

    @PostMapping("/update")
    public Resp update(@RequestBody Tag Tag) throws TagNotFoundException {
        Tag update = TagService.update(Tag);
        return Resp.success(update);
    }

    @PostMapping("/delete/{id}")
    public Resp detete(@PathVariable String id) throws TagNotFoundException {
        try{
            TagService.delete(Integer.parseInt(id));
        }catch (NumberFormatException e){
            throw new TagNotFoundException(id);
        }
        return Resp.success();
    }
}
