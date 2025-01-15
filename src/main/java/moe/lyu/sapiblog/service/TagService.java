package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.entity.mapper.TagMapper;
import moe.lyu.sapiblog.entity.mapper.TagMapper;
import moe.lyu.sapiblog.exception.TagAlreadyExistException;
import moe.lyu.sapiblog.exception.TagFieldNotFoundException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.exception.TagUnknownException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    TagMapper tagMapper;

    @Autowired
    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public List<Tag> list(Boolean orderByDesc, String orderField) throws TagFieldNotFoundException {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        Class<Tag> TagClass = Tag.class;
        queryWrapper.ge("id", 0);
        try{
            TagClass.getDeclaredField(orderField);
            if(orderByDesc){
                queryWrapper.orderByDesc(orderField);
            }else{
                queryWrapper.orderByAsc(orderField);
            }
        } catch (NoSuchFieldException ignored) {
            throw new TagFieldNotFoundException(orderField);
        }
        return tagMapper.selectList(queryWrapper);
    }

    public Tag getById(Integer id) throws TagNotFoundException {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Tag Tag = tagMapper.selectOne(queryWrapper);
        if(Tag == null){
            throw new TagNotFoundException(id.toString());
        }else {
            return Tag;
        }
    }

    public Tag getByUniqName(String name) throws TagNotFoundException {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uniq_name", name);
        Tag Tag = tagMapper.selectOne(queryWrapper);
        if(Tag == null){
            throw new TagNotFoundException(name);
        }else{
            return Tag;
        }
    }

    public Tag add(Tag Tag) throws TagAlreadyExistException,JsonProcessingException {
        try{
            int effectRows = tagMapper.insert(Tag);
            if(effectRows == 1){
                return Tag;
            }else{
                throw new TagUnknownException(new ObjectMapper().writeValueAsString(Tag));
            }
        }catch (DuplicateKeyException e){
            throw new TagAlreadyExistException(Tag.getUniqName());
        }

    }

    public Tag update(Tag Tag) throws TagNotFoundException {
        int effectRows = tagMapper.updateById(Tag);
        if(effectRows == 1){
            return Tag;
        }else{
            throw new TagNotFoundException(Tag.getId().toString());
        }
    }

    public Boolean delete(Integer id) throws TagNotFoundException {
        int effectRows = tagMapper.deleteById(id);
        return effectRows == 1;
    }
}
