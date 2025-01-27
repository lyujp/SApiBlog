package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.exception.SettingNotExistException;
import moe.lyu.sapiblog.mapper.SettingMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SettingServiceTest {

    @Autowired
    private SettingService settingService;

    @Autowired
    private SettingMapper settingMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        LambdaUpdateChainWrapper<Setting> tagLambdaUpdateChainWrapper = new LambdaUpdateChainWrapper<>(settingMapper);
        tagLambdaUpdateChainWrapper.likeRight(Setting::getK, "__Test").remove();
    }

    @Test
    void list() {
        settingService.update(Map.of("__Test List","list"));

        List<String> list = settingService.list(true).stream().map(Setting::getK).toList();
        assertTrue(list.contains("__Test List"));
    }

    @Test
    void update() {
        settingService.update(Map.of("__Test Update","update"));
        String testUpdate = settingService.getValue("__Test Update");
        assertEquals("update", testUpdate);

        settingService.update(Map.of("__Test Update","update2"));
        testUpdate = settingService.getValue("__Test Update");
        assertEquals("update2", testUpdate);
    }

    @Test
    void getValue() {
        settingService.update(Map.of("__Test Get value","get value"));

        assertEquals("get value", settingService.getValue("__Test Get value"));
        assertThrowsExactly(SettingNotExistException.class, () -> settingService.getValue("__Test Get value null"));
    }

    @Test
    void get() {
        settingService.update(Map.of("__Test Get","get"));

        assertEquals("get", settingService.getValue("__Test Get"));
        assertThrowsExactly(SettingNotExistException.class, () -> settingService.getValue("__Test Get null"));
    }
}