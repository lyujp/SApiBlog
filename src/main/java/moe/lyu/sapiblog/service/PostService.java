package moe.lyu.sapiblog.service;

import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.PostAddFailedException;
import moe.lyu.sapiblog.exception.PostSaveFailedException;
import moe.lyu.sapiblog.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    PostMapper postMapper;

    @Autowired
    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public Post save(Post post) {
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

    public Boolean delete(Long id) {
        return postMapper.deleteById(id) == 1;
    }
}
