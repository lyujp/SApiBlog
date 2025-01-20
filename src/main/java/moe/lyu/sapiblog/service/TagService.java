package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.entity.TagPost;
import moe.lyu.sapiblog.exception.TagAddFailedException;
import moe.lyu.sapiblog.exception.TagAlreadyExistException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.exception.TagUnknownException;
import moe.lyu.sapiblog.mapper.TagMapper;
import moe.lyu.sapiblog.mapper.TagPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagPostMapper tagPostMapper;
    TagMapper tagMapper;

    @Autowired
    public TagService(TagMapper tagMapper, TagPostMapper tagPostMapper) {
        this.tagMapper = tagMapper;
        this.tagPostMapper = tagPostMapper;
    }

    public List<Tag> list(Integer currentPage, Integer pageSize, Boolean desc) {
        Page<Tag> page = new Page<>(currentPage, pageSize);
        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        return tagLambdaQueryChainWrapper
                .orderBy(true, !desc, Tag::getId).page(page).getRecords();
    }

    public List<Tag> listByPostId(Integer postId) {
        if (postId == null) postId = 0;
        LambdaQueryChainWrapper<TagPost> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagPostMapper);
        List<Integer> tagIds = tagLambdaQueryChainWrapper.eq(TagPost::getPostId, postId).list()
                .stream().map(TagPost::getTagId).toList();
        LambdaQueryChainWrapper<Tag> tagLambdaWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        return tagLambdaWrapper.in(Tag::getId, tagIds).list();
    }

    public Tag getByName(String name) throws TagNotFoundException {
        if (name == null || name.isEmpty()) throw new TagNotFoundException("Name can't be empty");
        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tag = tagLambdaQueryChainWrapper.eq(Tag::getName, name).one();
        if (tag == null) {
            throw new TagNotFoundException(name);
        }
        return tag;
    }

    public Tag add(Tag tag) throws JsonProcessingException, TagAlreadyExistException, TagAddFailedException, TagUnknownException {
        if (tag == null) throw new TagAddFailedException("Tag can't be null");
        if (tag.getName() == null || tag.getName().isEmpty())
            throw new TagAddFailedException("Tag name can't be empty");

        LambdaQueryChainWrapper<Tag> tagByNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tagByName = tagByNameLambdaQueryWrapper.eq(Tag::getName, tag.getName()).one();
        if (tagByName != null) {
            return tagByName;
        }

        int insert = tagMapper.insert(tag);
        if (insert == 0) {
            throw new TagAddFailedException("Unknown Exception");
        }

        tagByNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        tagByName = tagByNameLambdaQueryWrapper.eq(Tag::getName, tag.getName()).one();
        if (tagByName == null) {
            throw new TagAddFailedException("Unknown Exception");
        }
        return tagByName;
    }

    public Tag add(String name) throws JsonProcessingException, TagUnknownException, TagAddFailedException {
        if (name == null || name.isEmpty()) throw new TagAddFailedException("Tag name can't be empty");

        Tag tag = new Tag();
        tag.setName(name);
        return add(tag);
    }

    public Tag update(Tag tag) throws TagNotFoundException, TagUnknownException, JsonProcessingException {
        if (tag == null) throw new TagNotFoundException("Tag can't be null");
        if (tag.getId() == null) throw new TagNotFoundException("Tag id can't be null");
        if (tag.getName() == null || tag.getName().isEmpty()) throw new TagNotFoundException("Tag name can't be null");

        int i = tagMapper.updateById(tag);
        if (i == 0) {
            throw new TagNotFoundException(new ObjectMapper().writeValueAsString(tag));
        }
        return tagMapper.selectById(tag.getId());
    }

    public void delete(Integer id) throws TagNotFoundException {
        if (id == null) return;

        int i = tagMapper.deleteById(id);
        if (i == 0) {
            throw new TagNotFoundException(id + " does not exist");
        }
    }

    public void delete(String name) throws TagNotFoundException {
        if (name == null || name.isEmpty()) return;

        LambdaUpdateChainWrapper<Tag> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagMapper);
        boolean remove = tagLambdaUpdateChainWrapper.eq(Tag::getName, name).remove();
        if (!remove) {
            throw new TagNotFoundException(name + " does not exist");
        }
    }
}
