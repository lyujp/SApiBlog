package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import moe.lyu.sapiblog.dto.PostWithoutContentDto;
import moe.lyu.sapiblog.entity.Post;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.exception.PostSaveFailedException;
import moe.lyu.sapiblog.mapper.PostMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        LambdaUpdateChainWrapper<Post> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(postMapper);
        tagLambdaUpdateChainWrapper.likeRight(Post::getTitle, "__Test").remove();
    }

    @Test
    void update() {
        Post postNull = null;
        assertThrowsExactly(PostNotExistException.class, () -> postService.update(postNull));
        Post postInvalidId = new Post();
        postInvalidId.setId(0);
        assertThrowsExactly(PostSaveFailedException.class, () -> postService.update(postInvalidId));
        Post post = new Post();
        post.setPostType(false);
        post.setContent("test");
        post.setTitle("__Test Update");
        post.setStatus(true);
        assertNotNull(postService.update(post));

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        Post postTestUpdateDb = postLambdaQueryChainWrapper.likeRight(Post::getTitle, "__Test Update").one();
        assertEquals("__Test Update", postTestUpdateDb.getTitle());

        postTestUpdateDb.setContent("test2");
        postService.update(postTestUpdateDb);
        assertEquals("test2", postTestUpdateDb.getContent());
    }

    @Test
    void delete() {
        Post post = new Post();
        post.setTitle("__Test Delete");
        post.setContent("test");
        postService.update(post);

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        Post post1 = postLambdaQueryChainWrapper.eq(Post::getTitle, "__Test Delete").one();

        postService.delete(post1.getId());
        assertThrowsExactly(PostNotExistException.class, () -> postService.getById(post1.getId()));
    }

    @Test
    void list() {
        Post post = new Post();
        post.setTitle("__Test List");
        post.setContent("test");
        postService.update(post);

        List<PostWithoutContentDto> list = postService.list(1, 100, true);
        List<String> titles = list.stream().map(PostWithoutContentDto::getTitle).toList();
        assertTrue(titles.contains("__Test List"));
    }

    @Test
    void getById() {
        Post post = new Post();
        post.setTitle("__Test Get by id");
        post.setContent("test");
        postService.update(post);

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        Post post1 = postLambdaQueryChainWrapper.eq(Post::getTitle, "__Test Get by id").one();

        assertEquals("__Test Get by id", postService.getById(post1.getId()).getTitle());
        assertThrowsExactly(PostNotExistException.class, () -> postService.getById(-1));
    }
}