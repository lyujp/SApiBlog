package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.exception.TagAlreadyExistException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.exception.TagUnknownException;
import moe.lyu.sapiblog.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    TagMapper tagMapper;

    @Autowired
    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public List<Tag> list(Integer currentPage, Integer pageSize, Boolean desc) {
        Page<Tag> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.orderBy(true, !desc, "id");
        page = tagMapper.selectPage(page, tagQueryWrapper);
        return page.getRecords();
    }

    public List<Tag> listByPostId(Integer postId, Integer currentPage, Integer pageSize, Boolean desc) {
        Page<Tag> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("post_id", postId);
        tagQueryWrapper.orderBy(true, !desc, "id");
        page = tagMapper.selectPage(page, tagQueryWrapper);
        return page.getRecords();
    }

    public Tag getById(Integer id) throws TagNotFoundException {
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getId, id);
        Tag tag = tagMapper.selectOne(tagLambdaQueryWrapper);
        if (tag == null) {
            throw new TagNotFoundException(id.toString() + " not found");
        } else {
            return tag;
        }
    }

    public Tag getByUniqName(String name) throws TagNotFoundException {
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getUniqName, name);
        Tag tag = tagMapper.selectOne(tagLambdaQueryWrapper);
        if (tag == null) {
            throw new TagNotFoundException(name + "not found");
        } else {
            return tag;
        }
    }

    public Tag add(Tag tag) throws JsonProcessingException, TagAlreadyExistException {
        if (tagMapper.selectById(tag.getId()) == null) {
            throw new TagAlreadyExistException(tag.getId().toString() + " already exist");
        }
        try {
            int effectRows = tagMapper.insert(tag);
            if (effectRows == 1) {
                return tag;
            }
        } catch (Exception ignored) {
        }
        throw new TagUnknownException(new ObjectMapper().writeValueAsString(tag));
    }

    public Tag add(String name) throws JsonProcessingException, TagUnknownException {
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.likeRight(Tag::getName, name);
        List<String> tagUniqNames = tagMapper.selectList(tagLambdaQueryWrapper).stream().map(Tag::getUniqName).toList();
        String uniqName = name.toLowerCase().trim();
        while (tagUniqNames.contains(uniqName)) {
            uniqName += "_";
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setUniqName(uniqName);
        try {
            int effectRows = tagMapper.insert(tag);
            if (effectRows == 1) {
                return tag;
            }
        } catch (Exception ignored) {
        }
        throw new TagUnknownException(new ObjectMapper().writeValueAsString(tag));
    }

    public Tag update(Tag tag) throws TagNotFoundException {
        int effectRows = tagMapper.updateById(tag);
        if (effectRows == 1) {
            return tag;
        } else {
            throw new TagNotFoundException(tag.getId().toString());
        }
    }

    public Boolean delete(Integer id) throws TagNotFoundException {
        int effectRows = tagMapper.deleteById(id);
        return effectRows == 1;
    }
}
