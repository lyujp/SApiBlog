package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.entity.TagPost;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.exception.*;
import moe.lyu.sapiblog.mapper.TagMapper;
import moe.lyu.sapiblog.mapper.TagPostMapper;
import moe.lyu.sapiblog.mapper.TagMapper;
import org.springframework.beans.BeanUtils;
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
                .orderBy(true, !desc,Tag::getId).page(page).getRecords();
    }

    public List<Tag> listByPostId(Integer postId) {
        if (postId == null) postId = 0;
        LambdaQueryChainWrapper<TagPost> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagPostMapper);
        List<Integer> tagIds = tagLambdaQueryChainWrapper.eq(TagPost::getPostId, postId).list()
                .stream().map(TagPost::getTagId).toList();
        LambdaQueryChainWrapper<Tag> tagLambdaWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        return tagLambdaWrapper.in(Tag::getId, tagIds).list();
    }

    public Tag getByUniqName(String uniqName) throws TagNotFoundException {
        if(uniqName == null || uniqName.isEmpty()) throw new TagNotFoundException("Uniq name can't be empty");
        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tag = tagLambdaQueryChainWrapper.eq(Tag::getUniqName, uniqName).one();
        if(tag == null) {
            throw new TagNotFoundException(uniqName);
        }
        return tag;
    }

    public Tag getByName(String name) throws TagNotFoundException {
        if(name == null || name.isEmpty()) throw new TagNotFoundException("Name can't be empty");
        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tag = tagLambdaQueryChainWrapper.eq(Tag::getName, name).one();
        if(tag == null) {
            throw new TagNotFoundException(name);
        }
        return tag;
    }

    public Tag add(Tag tag) throws JsonProcessingException, TagAlreadyExistException, TagAddFailedException, TagUnknownException {
        if(tag == null) throw new TagAddFailedException("Tag can't be null");
        if (tag.getName() == null || tag.getName().isEmpty()) throw new TagAddFailedException("Tag name can't be empty");
        if (tag.getUniqName() == null || tag.getUniqName().isEmpty()) throw new TagAddFailedException("Tag uniqName can't be empty");

        LambdaQueryChainWrapper<Tag> tagByNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tagByName = tagByNameLambdaQueryWrapper.eq(Tag::getName, tag.getName()).one();
        if(tagByName != null) {
            return tagByName;
        }

        LambdaQueryChainWrapper<Tag> tagByUniqNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tagByUniqName = tagByUniqNameLambdaQueryWrapper.eq(Tag::getUniqName, tag.getUniqName()).one();
        if(tagByUniqName != null) {
            return tagByUniqName;
        }

        int insert = tagMapper.insert(tag);
        if(insert == 0) {
            throw new TagAddFailedException("Unknown Exception");
        }

        tagByUniqNameLambdaQueryWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag one = tagByUniqNameLambdaQueryWrapper.eq(Tag::getUniqName, tag.getUniqName()).one();
        if(one == null) {
            throw new TagUnknownException("Unknown Exception");
        }
        return one;
    }

    public Tag add(String name) throws JsonProcessingException, TagUnknownException, TagAddFailedException {
        if (name == null || name.isEmpty()) throw new TagAddFailedException("Tag name can't be empty");

        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tag = tagLambdaQueryChainWrapper.eq(Tag::getName, name).one();
        if(tag != null) {
            return tag;
        }
        String uniqName = name.toLowerCase().trim();

        tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        List<String> tagUniqNames = tagLambdaQueryChainWrapper.likeRight(Tag::getUniqName, uniqName).list()
                .stream().map(Tag::getUniqName).toList();


        while (tagUniqNames.contains(uniqName)) {
            uniqName += "_";
        }

        LambdaUpdateChainWrapper<Tag> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagMapper);
        tagLambdaUpdateChainWrapper.set(Tag::getUniqName, uniqName)
                .set(Tag::getName, name).update();

        tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag one = tagLambdaQueryChainWrapper.eq(Tag::getName, name).one();
        if(one == null) {
            throw new TagUnknownException("Unknown Exception");
        }
        return one;
    }

    public Tag update(Tag tag) throws TagNotFoundException,TagUnknownException {
        if(tag == null) throw new TagNotFoundException("Tag can't be null");
        if(tag.getId() == null) throw new TagNotFoundException("Tag id can't be null");

        LambdaUpdateChainWrapper<Tag> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagMapper);
        boolean update = tagLambdaUpdateChainWrapper.setEntity(tag).update();
        if(!update) {
            throw new TagNotFoundException(tag.getId().toString());
        }

        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag one = tagLambdaQueryChainWrapper.eq(Tag::getUniqName, tag.getUniqName()).one();
        if(one == null) {
            throw new TagUnknownException("Unknown Exception");
        }
        return one;
    }

    public void delete(Integer id) throws TagNotFoundException {
        if (id == null) return;

        LambdaUpdateChainWrapper<Tag> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagMapper);
        boolean remove = tagLambdaUpdateChainWrapper.eq(Tag::getId, id).remove();
        if(!remove){
            throw new TagNotFoundException(id.toString() + " does not exist");
        }
    }
}
