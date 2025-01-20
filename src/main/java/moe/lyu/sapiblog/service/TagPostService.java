package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
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

    public List<TagPost> listByPostId(Integer postId) {
        LambdaQueryChainWrapper<TagPost> tagPostLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagPostMapper);
        return tagPostLambdaQueryChainWrapper.eq(TagPost::getPostId, postId).orderByDesc(TagPost::getId).list();
    }

    public void add(Integer postId, Integer tagId) throws TagAddFailedException,
            PostNotExistException,
            TagNotFoundException {
        LambdaQueryChainWrapper<TagPost> tagPostLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagPostMapper);
        TagPost one = tagPostLambdaQueryChainWrapper.eq(TagPost::getPostId, postId).eq(TagPost::getTagId, tagId).one();
        if (one != null) {
            return;
        }

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        Post post = postLambdaQueryChainWrapper.eq(Post::getId, postId).one();
        if (post == null) {
            throw new PostNotExistException("Post id " + postId + " not exist");
        }

        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        Tag tag = tagLambdaQueryChainWrapper.eq(Tag::getId, tagId).one();
        if (tag == null) {
            throw new TagNotFoundException("Tag id " + tagId + " not exist");
        }

        TagPost tagPost = new TagPost();
        tagPost.setPostId(postId);
        tagPost.setTagId(tagId);
        int insert = tagPostMapper.insert(tagPost);
        if (insert == 0) {
            throw new TagAddFailedException("Unknown reason");
        }
    }

    public void deleteByPostId(Integer postId) {
        LambdaUpdateChainWrapper<TagPost> tagPostLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagPostMapper);
        tagPostLambdaUpdateChainWrapper.eq(TagPost::getPostId, postId).remove();
    }

    public void deleteByTagId(Integer tagId) {
        LambdaUpdateChainWrapper<TagPost> tagPostLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagPostMapper);
        tagPostLambdaUpdateChainWrapper.eq(TagPost::getTagId, tagId).remove();
    }
}
