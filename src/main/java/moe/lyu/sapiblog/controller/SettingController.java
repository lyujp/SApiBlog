package moe.lyu.sapiblog.controller;

import moe.lyu.sapiblog.annotation.AuthCheck;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.exception.SettingNotExistException;
import moe.lyu.sapiblog.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/setting")
public class SettingController {

    SettingService settingService;

    @Autowired
    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/system")
    public Resp getSystemSetting() {
        List<Setting> settings = settingService.list(false);
        Map<String,String> map = new HashMap<>();
        settings.forEach(setting -> map.put(setting.getK(), setting.getV()));
        return Resp.success(map);
    }

    @GetMapping("/get/{k}")
    public Resp getValue(@PathVariable String k) throws SettingNotExistException {
        return Resp.success(settingService.get(k));
    }

    @GetMapping("/custom")
    public Resp getCustomSetting() {
        List<Setting> settings = settingService.list(true);
        Map<String,String> map = new HashMap<>();
        settings.forEach(setting -> map.put(setting.getK(), setting.getV()));
        return Resp.success(map);
    }

    @PostMapping("/update")
    @AuthCheck
    public Resp updateSetting(@RequestBody Map<String,String> settings) {
        settingService.update(settings);
        return Resp.success();
    }
}
