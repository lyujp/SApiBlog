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

import java.util.List;

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
        Setting setting = new Setting();
        setting.setK("__Test List");
        setting.setV("list");
        settingService.update(List.of(setting));

        List<String> list = settingService.list(false).stream().map(Setting::getK).toList();
        assertTrue(list.contains("__Test List"));
    }

    @Test
    void update() {
        Setting setting = new Setting();
        setting.setK("__Test Update");
        setting.setV("update");
        settingService.update(List.of(setting));


        String testUpdate = settingService.getValue("__Test Update");
        assertEquals("update", testUpdate);

        setting.setV("update2");
        settingService.update(List.of(setting));
        testUpdate = settingService.getValue("__Test Update");
        assertEquals("update2", testUpdate);
    }

    @Test
    void getValue() {
        Setting setting = new Setting();
        setting.setK("__Test Get value");
        setting.setV("get value");
        settingService.update(List.of(setting));

        assertEquals("get value", settingService.getValue("__Test Get value"));
        assertThrowsExactly(SettingNotExistException.class, () -> settingService.getValue("__Test Get value null"));
    }

    @Test
    void get() {
        Setting setting = new Setting();
        setting.setK("__Test Get");
        setting.setV("get");
        settingService.update(List.of(setting));

        assertEquals("get", settingService.getValue("__Test Get"));
        assertThrowsExactly(SettingNotExistException.class, () -> settingService.getValue("__Test Get null"));
    }
}