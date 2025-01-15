package moe.lyu.sapiblog;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.entity.Tag;
import moe.lyu.sapiblog.exception.TagAlreadyExistException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TagServiceTest {

    @Autowired
    TagService TagService;

    Integer testTagId;

    @Test
    void testMain() throws JsonProcessingException {
        testSetup();

        testAdd();
        testList();
        testUpdate();
        testDelete();

        testCleanUp();
    }

    void testSetup() {
        try{
            Tag test = TagService.getByUniqName("__test");
            TagService.delete(test.getId());

        }catch (TagNotFoundException e){}
        try{
            Tag test = TagService.getByUniqName("__test2");
            TagService.delete(test.getId());
        }catch (TagNotFoundException e){}
    }


    void testCleanUp(){
        try{
            Tag test = TagService.getByUniqName("__test");
            TagService.delete(test.getId());

        }catch (TagNotFoundException e){}
        try{
            Tag test = TagService.getByUniqName("__test2");
            TagService.delete(test.getId());
        }catch (TagNotFoundException e){}
    }

    void testAdd() throws JsonProcessingException {
        Tag Tag = new Tag();
        Tag.setName("__test");
        Tag.setUniqName("__test");
        Tag added = TagService.add(Tag);
        this.testTagId = added.getId();
        assertEquals("__test", added.getUniqName());
        assertEquals("__test", added.getName());
        assertThrows(TagAlreadyExistException.class, () -> TagService.add(Tag));
    }


    void testList(){
        AtomicReference<Boolean> findTestTag = new AtomicReference<>(false);
        List<Tag> result = TagService.list(true, "id");
        result.forEach(Tag -> {
            if(Objects.equals(Tag.getId(), testTagId)){
                findTestTag.set(true);
            }
        });
        assertTrue(findTestTag.get());
        assertDoesNotThrow(() -> TagService.getById(this.testTagId));
        assertDoesNotThrow(() -> TagService.getByUniqName("__test"));
    }


    void testUpdate(){
        Tag Tag = new Tag();
        Tag.setId(this.testTagId);
        Tag.setName("__test2");
        Tag.setUniqName("__test2");
        Tag update = TagService.update(Tag);
        assertNotNull(update);
        assertEquals(this.testTagId, update.getId());
        Tag updated = TagService.getById(this.testTagId);
        assertEquals("__test2", updated.getName());
        assertEquals("__test2", updated.getUniqName());
        Tag.setName("__test");
        Tag.setUniqName("__test");
        Tag.setId(this.testTagId+1);
        assertThrows(TagNotFoundException.class, () -> TagService.update(Tag));
    }

    void testDelete(){
        Boolean delete = TagService.delete(this.testTagId);
        assertTrue(delete);
        assertThrows(TagNotFoundException.class, () -> TagService.getById(this.testTagId));
        assertThrows(TagNotFoundException.class, () -> TagService.getByUniqName("__test2"));
    }

}
