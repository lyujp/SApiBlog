package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.mapper.TagMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagServiceTest {

    @Autowired
    TagService tagService;
    @Autowired
    private TagMapper tagMapper;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        LambdaUpdateChainWrapper<Tag> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(tagMapper);
        tagLambdaUpdateChainWrapper.likeRight(Tag::getName, "__Test").remove();
    }

    @Test
    void list() throws JsonProcessingException {
        tagService.add("__Test List");
        List<Tag> categories = tagService.list(1, 10, true);
        List<String> tagNames = categories.stream().map(Tag::getName).toList();
        assertTrue(tagNames.contains("__Test List"));
    }

    @Test
    void add() throws JsonProcessingException {
        Tag tag = new Tag();
        tag.setName("__Test Add");
        Tag added = tagService.add(tag);
        tagService.add(tag);

        assertEquals("__Test Add", added.getName());

        Tag added2 = tagService.add("__Test Add 2");
        assertEquals("__Test Add 2", added2.getName());
    }

    @Test
    void update() throws JsonProcessingException {
        Tag tagNull = null;
        Tag tagIdNull = new Tag();
        tagIdNull.setName("__Test Update Null");
        Tag tagNameNull = new Tag();
        tagNameNull.setId(9999);
        Tag tag = new Tag();
        tag.setName("__Test Update Name with id");
        tag.setId(9999);

        assertThrowsExactly(TagNotFoundException.class, () -> tagService.update(tagNull));
        assertThrowsExactly(TagNotFoundException.class, () -> tagService.update(tagIdNull));
        assertThrowsExactly(TagNotFoundException.class, () -> tagService.update(tagNameNull));
        assertThrowsExactly(TagNotFoundException.class, () -> tagService.update(tag));

        tagService.add("__Test Update");
        Tag testUpdate = tagService.getByName("__Test Update");
        testUpdate.setName("__Test Update2");
        tagService.update(testUpdate);

        assertThrowsExactly(TagNotFoundException.class, () -> tagService.getByName("__Test Update"));
        Tag testUpdate2 = tagService.getByName("__Test Update2");
        assertEquals("__Test Update2", testUpdate2.getName());

    }

    @Test
    void delete() throws JsonProcessingException {

        assertThrowsExactly(TagNotFoundException.class, () -> tagService.delete(9999));
        assertThrowsExactly(TagNotFoundException.class, () -> tagService.delete("__Test Delete"));
        Tag tag = tagService.add("__Test Delete 1");
        Tag tag2 = tagService.add("__Test Delete 2");

        Tag tagDb1 = tagService.getByName(tag.getName());
        Tag tagDb2 = tagService.getByName(tag2.getName());

        assertEquals("__Test Delete 1", tagDb1.getName());
        assertEquals("__Test Delete 2", tagDb2.getName());
        tagService.delete(tag.getName());
        tagService.delete(tag2.getName());

        assertThrowsExactly(TagNotFoundException.class, () -> tagService.getByName(tag.getName()));
        assertThrowsExactly(TagNotFoundException.class, () -> tagService.getByName(tag2.getName()));
    }

    @Test
    void getByName() throws JsonProcessingException {
        tagService.add("__Test Get");
        assertEquals("__Test Get", tagService.getByName("__Test Get").getName());
        assertThrowsExactly(TagNotFoundException.class, () -> tagService.getByName("__Test Get 2"));
    }
}