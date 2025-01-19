package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.entity.TagPost;
import moe.lyu.sapiblog.exception.PostNotExistException;
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

    public Boolean add(Integer postId, Integer tagId) {
        TagPost tagPost = new TagPost();
        tagPost.setTagId(tagId);
        tagPost.setPostId(postId);
        return addTagPost(List.of(tagPost));
    }

    public Boolean add(Integer postId, List<Integer> tagIds) {
        List<TagPost> tagPosts = tagIds.stream()
                .map(tagId -> {
                    TagPost tagPost = new TagPost();
                    tagPost.setTagId(tagId);
                    tagPost.setPostId(postId);
                    return tagPost;
                })
                .toList();
        return addTagPost(tagPosts);
    }

    public Boolean add(TagPost tagPost) {
        return addTagPost(List.of(tagPost));
    }

    private Boolean addTagPost(List<TagPost> tagPosts) throws PostNotExistException {
        if (tagPosts == null || tagPosts.isEmpty()) {
            return false;
        }
        Integer postId = tagPosts.get(0).getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new PostNotExistException(postId.toString());
        }

        return iTagPostService.saveOrUpdateBatch(tagPosts);
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

    public Integer deleteByTagPostId(Integer tagPostId) {
        return tagPostMapper.deleteById(tagPostId);
    }


    public List<TagPost> getByPostId(Integer postId) {
        return tagPostMapper.selectList(new LambdaQueryWrapper<TagPost>().eq(TagPost::getPostId, postId));
    }
}
