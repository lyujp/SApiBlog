package moe.lyu.sapiblog.controller;

import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.dto.SettingDto;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.exception.SettingNotExistException;
import moe.lyu.sapiblog.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<SettingDto> settingDtos = settings.stream().map(setting -> {
            SettingDto settingDto = new SettingDto();
            settingDto.setK(setting.getK());
            settingDto.setV(setting.getV());
            return settingDto;
        }).toList();
        return Resp.success(settingDtos);
    }

    @GetMapping("/get/{k}")
    public Resp getValue(@PathVariable String k) throws SettingNotExistException {
        return Resp.success(settingService.get(k));
    }

    @GetMapping("/custom")
    public Resp getCustomSetting() {
        List<Setting> settings = settingService.list(true);
        List<SettingDto> settingDtos = settings.stream().map(setting -> {
            SettingDto settingDto = new SettingDto();
            settingDto.setK(setting.getK());
            settingDto.setV(setting.getV());
            return settingDto;
        }).toList();
        return Resp.success(settingDtos);
    }

    @PostMapping("/update")
    public Resp updateSetting(@RequestBody List<SettingDto> settingDtos) {
        List<Setting> settings = settingDtos.stream().map(settingDto -> {
            Setting setting = new Setting();
            setting.setK(settingDto.getK());
            setting.setV(settingDto.getV());
            return setting;
        }).toList();
        settingService.update(settings);
        return Resp.success();
    }
}
