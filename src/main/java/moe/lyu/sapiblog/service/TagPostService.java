package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.entity.TagPost;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.mapper.PostMapper;
import moe.lyu.sapiblog.mapper.TagMapper;
import moe.lyu.sapiblog.mapper.TagPostDtoMapper;
import moe.lyu.sapiblog.mapper.TagPostMapper;
import moe.lyu.sapiblog.vo.TagPostVo;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TagPostService {

    private final SqlSessionFactory sqlSessionFactory;
    TagPostDtoMapper tagPostDtoMapper;
    TagPostMapper tagPostMapper;
    PostMapper postMapper;
    TagMapper tagMapper;

    @Autowired
    public TagPostService(TagPostDtoMapper tagPostDtoMapper,
                          TagPostMapper tagPostMapper,
                          SqlSessionFactory sqlSessionFactory,
                          PostMapper postMapper,
                          TagMapper tagMapper) {
        this.tagPostDtoMapper = tagPostDtoMapper;
        this.tagPostMapper = tagPostMapper;
        this.sqlSessionFactory = sqlSessionFactory;
        this.postMapper = postMapper;
        this.tagMapper = tagMapper;
    }

    public List<TagPostVo> list(Integer tagId) {
        return tagPostDtoMapper.get(tagId);
    }

    public Integer add(Integer postId, Integer tagId) {
        TagPost tagPost = new TagPost();
        tagPost.setTagId(tagId);
        tagPost.setPostId(postId);
        return addTagPost(List.of(tagPost));
    }

    public Integer add(Integer postId, List<Integer> tagIds) {
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

    public Integer add(TagPost tagPost) {
        return addTagPost(List.of(tagPost));
    }

    private Integer addTagPost(List<TagPost> tagPosts) throws PostNotExistException {
        if (tagPosts != null) {
            Integer postId = tagPosts.get(0).getPostId();
            Post post = postMapper.selectById(postId);
            if (post == null) {
                throw new PostNotExistException(postId.toString());
            }
        }
        List<Integer> tagIds = tagPosts.stream().map(TagPost::getTagId).toList();
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.in(Tag::getId, tagIds);

        List<Integer> cids = tagMapper.selectList(tagLambdaQueryWrapper).stream().map(Tag::getId).toList();

        List<TagPost> list = tagPosts.stream().filter(cp -> cids.contains(cp.getTagId())).toList();

        MybatisBatch<TagPost> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, list);
        MybatisBatch.Method<TagPost> method = new MybatisBatch.Method<>(TagPost.class);
        List<BatchResult> batchResults = mybatisBatch.execute(method.insert());
        return batchResults.stream().flatMapToInt(br -> Arrays.stream(br.getUpdateCounts())).sum();
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
}
