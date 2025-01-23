package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.*;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.mapper.*;
import moe.lyu.sapiblog.vo.CategoryPostVo;
import moe.lyu.sapiblog.vo.TagPostVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CategoryTagPostServiceTest {
    @Autowired
    private CategoryPostService categoryPostService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private TagPostService tagPostService;

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagPostMapper tagPostMapper;
    @Autowired
    private CategoryPostMapper categoryPostMapper;
    @Autowired
    private PostService postService;

    private Post postAdd;
    private Category categoryAdd;
    private Tag tagAdd;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        Post post = new Post();
        post.setTitle("__Test Tag category post add");
        post.setContent("test");
        this.postAdd = postService.update(post);

        Category category = new Category();
        category.setName("__Test Tag category post add");
        this.categoryAdd = categoryService.add(category);

        Tag tag = new Tag();
        tag.setName("__Test Tag category post add");
        this.tagAdd = tagService.add(tag);

        categoryPostService.add(this.postAdd.getId(), this.categoryAdd.getId());
        tagPostService.add(this.postAdd.getId(), this.tagAdd.getId());
    }

    @AfterEach
    void tearDown() {
        LambdaQueryChainWrapper<Category> categoryLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        List<Integer> categoryIds = categoryLambdaQueryChainWrapper.likeRight(Category::getName, "__Test").list().stream().map(Category::getId).toList();

        LambdaQueryChainWrapper<Tag> tagLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        List<Integer> tagIds = tagLambdaQueryChainWrapper.likeRight(Tag::getName, "__Test").list().stream().map(Tag::getId).toList();

        LambdaQueryChainWrapper<Post> postLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(postMapper);
        List<Integer> postIds = postLambdaQueryChainWrapper.likeRight(Post::getTitle, "__Test").list().stream().map(Post::getId).toList();

        LambdaUpdateChainWrapper<Category> categoryLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryMapper);
        categoryLambdaUpdateChainWrapper.likeRight(Category::getName, "__Test").remove();

        LambdaUpdateChainWrapper<Tag> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagMapper);
        tagLambdaUpdateChainWrapper.likeRight(Tag::getName, "__Test").remove();

        LambdaUpdateChainWrapper<Post> postLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(postMapper);
        postLambdaUpdateChainWrapper.likeRight(Post::getTitle, "__Test").remove();

        LambdaUpdateChainWrapper<TagPost> tagPostLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagPostMapper);
        tagPostLambdaUpdateChainWrapper.in(TagPost::getTagId, tagIds).remove();
        LambdaUpdateChainWrapper<TagPost> tagPostLambdaUpdateChainWrapper2 = new LambdaUpdateChainWrapper<>(tagPostMapper);
        tagPostLambdaUpdateChainWrapper2.in(TagPost::getPostId, postIds).remove();

        LambdaUpdateChainWrapper<CategoryPost> categoryPostLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(categoryPostMapper);
        categoryPostLambdaUpdateChainWrapper.in(CategoryPost::getCategoryId, categoryIds).remove();
        LambdaUpdateChainWrapper<CategoryPost> categoryPostLambdaUpdateChainWrapper2 = new LambdaUpdateChainWrapper<>(categoryPostMapper);
        categoryPostLambdaUpdateChainWrapper2.in(CategoryPost::getPostId, postIds).remove();
    }

    @Test
    void add() {
        assertThrowsExactly(PostNotExistException.class, () -> categoryPostService.add(-1, this.categoryAdd.getId()));
        assertThrowsExactly(CategoryNotFoundException.class, () -> categoryPostService.add(this.postAdd.getId(), -1));

        assertThrowsExactly(PostNotExistException.class, () -> tagPostService.add(-1, this.tagAdd.getId()));
        assertThrowsExactly(TagNotFoundException.class, () -> tagPostService.add(this.postAdd.getId(), -1));
    }

    @Test
    void list() {

        assertTrue(categoryPostService.list(this.categoryAdd.getId()).stream().map(CategoryPostVo::getPostId).toList()
                .contains(this.postAdd.getId()));
        assertTrue(tagPostService.list(this.tagAdd.getId()).stream().map(TagPostVo::getPostId).toList()
                .contains(this.postAdd.getId()));
    }

    @Test
    void listByPostId() {
        assertTrue(categoryPostService.listByPostId(postAdd.getId()).stream().map(CategoryPost::getPostId).toList()
                .contains(this.postAdd.getId()));
        assertTrue(tagPostService.listByPostId(postAdd.getId()).stream().map(TagPost::getPostId).toList()
                .contains(this.postAdd.getId()));
    }

    @Test
    void delete() throws JsonProcessingException {
        Post post = new Post();
        post.setTitle("__Test Tag category post delete");
        post.setContent("test");
        Post postAdd = postService.update(post);

        Category category = new Category();
        category.setName("__Test Tag category post delete");
        Category categoryAdd = categoryService.add(category);

        Tag tag = new Tag();
        tag.setName("__Test Tag category post delete");
        Tag tagAdd = tagService.add(tag);

        categoryPostService.add(postAdd.getId(), categoryAdd.getId());
        tagPostService.add(postAdd.getId(), tagAdd.getId());

        categoryPostService.deleteByCategoryId(categoryAdd.getId());
        tagPostService.deleteByTagId(tagAdd.getId());

        assertEquals(0, categoryPostService.list(categoryAdd.getId()).stream().map(CategoryPostVo::getCategoryId).toList().size());
        assertEquals(0, tagPostService.list(categoryAdd.getId()).stream().map(TagPostVo::getTagId).toList().size());
    }

    @Test
    void deleteByPostId() throws JsonProcessingException {
        Post post = new Post();
        post.setTitle("__Test Tag category post delete by post id");
        post.setContent("test");
        Post postAdd = postService.update(post);

        Category category = new Category();
        category.setName("__Test Tag category post delete by post id");
        Category categoryAdd = categoryService.add(category);

        Tag tag = new Tag();
        tag.setName("__Test Tag category post delete by post id");
        Tag tagAdd = tagService.add(tag);

        categoryPostService.add(postAdd.getId(), categoryAdd.getId());
        tagPostService.add(postAdd.getId(), tagAdd.getId());

        categoryPostService.deleteByPostId(postAdd.getId());
        tagPostService.deleteByPostId(postAdd.getId());

        assertEquals(0, categoryPostService.list(categoryAdd.getId()).stream().map(CategoryPostVo::getCategoryId).toList().size());
        assertEquals(0, tagPostService.list(tagAdd.getId()).stream().map(TagPostVo::getTagId).toList().size());
    }
}
