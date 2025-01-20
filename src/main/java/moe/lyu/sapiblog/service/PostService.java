package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import moe.lyu.sapiblog.dto.PostWithoutContentDto;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.PostAddFailedException;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.exception.PostSaveFailedException;
import moe.lyu.sapiblog.mapper.PostMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    PostMapper postMapper;

    @Autowired
    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public Post update(Post post) throws PostNotExistException {
        if (post == null) throw new PostNotExistException("Post can't be null");
        if (post.getId() == null) {
            int effectRows = postMapper.insert(post);
            if (effectRows == 1) {
                return post;
            } else {
                throw new PostAddFailedException("Unknown error");
            }
        } else {
            int effectRows = postMapper.updateById(post);
            if (effectRows == 1) {
                return post;
            } else {
                throw new PostSaveFailedException("Unknown error");
            }
        }
    }

    public void delete(Integer id) {
        if (id == null) return;
        if (postMapper.deleteById(id) != 1) {
            throw new PostNotExistException("Post with id " + id + " not exist");
        }
    }

    public List<PostWithoutContentDto> list(Integer currentPage, Integer pageSize, Boolean desc) {

        Page<Post> page = new Page<>(currentPage, pageSize);

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        List<Post> posts = postLambdaQueryChainWrapper.orderBy(true, !desc, Post::getId).page(page).getRecords();


        return posts.stream().map(post -> {
            PostWithoutContentDto postWithoutContentDto = new PostWithoutContentDto();
            BeanUtils.copyProperties(post, postWithoutContentDto);
            return postWithoutContentDto;
        }).toList();
    }

    public Post getById(Integer id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new PostNotExistException("Post with id " + id + " not exist");
        }
        return post;
    }
}
