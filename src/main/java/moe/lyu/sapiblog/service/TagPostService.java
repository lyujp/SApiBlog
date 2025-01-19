package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.entity.TagPost;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.exception.TagAddFailedException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.mapper.PostMapper;
import moe.lyu.sapiblog.mapper.TagMapper;
import moe.lyu.sapiblog.mapper.TagPostDtoMapper;
import moe.lyu.sapiblog.mapper.TagPostMapper;
import moe.lyu.sapiblog.vo.TagPostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagPostService {

    TagPostDtoMapper tagPostDtoMapper;
    TagPostMapper tagPostMapper;
    PostMapper postMapper;
    TagMapper tagMapper;
    ITagPostService iTagPostService;

    @Autowired
    public TagPostService(TagPostDtoMapper tagPostDtoMapper,
                          TagPostMapper tagPostMapper,
                          PostMapper postMapper,
                          TagMapper tagMapper,
                          ITagPostService iTagPostService) {
        this.tagPostDtoMapper = tagPostDtoMapper;
        this.tagPostMapper = tagPostMapper;
        this.postMapper = postMapper;
        this.tagMapper = tagMapper;
        this.iTagPostService = iTagPostService;
    }

    public List<TagPostVo> list(Integer tagId) {
        return tagPostDtoMapper.get(tagId);
    }

    public List<TagPost> listByPostId(Integer postId, Integer currentPage, Integer pageSize, Boolean desc) {
        Page<TagPost> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TagPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true, !desc, "id");
        queryWrapper.eq("post_id", postId);
        page = tagPostMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    public void add(Integer postId, Integer tagId)
            throws TagAddFailedException,
            PostNotExistException,
            TagNotFoundException {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new PostNotExistException("Post id " + postId + " not exist");
        }

        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new TagNotFoundException("Tag id " + tagId + " not exist");
        }

        TagPost tagPost = new TagPost();
        tagPost.setTagId(tagId);
        tagPost.setPostId(postId);
        int insert = tagPostMapper.insert(tagPost);
        if (insert == 0) {
            LambdaQueryWrapper<TagPost> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TagPost::getTagId, tagId);
            queryWrapper.eq(TagPost::getPostId, postId);
            List<TagPost> tagPosts = tagPostMapper.selectList(queryWrapper);
            if (tagPosts.isEmpty()) {
                throw new TagAddFailedException("Unknown reason");
            }
            throw new TagAddFailedException("Post already add to tag");
        }
    }

    public Integer deleteByPostId(Integer postId) {
        LambdaQueryWrapper<TagPost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TagPost::getPostId, postId);
        return tagPostMapper.delete(lambdaQueryWrapper);
    }

    public Integer deleteByTagId(Integer tagId) {
        LambdaQueryWrapper<TagPost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TagPost::getTagId, tagId);
        return tagPostMapper.delete(lambdaQueryWrapper);
    }
}
