package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    SettingMapper settingMapper;
    ISettingService iSettingService;

    @Autowired
    public SettingService(SettingMapper settingMapper,
                          ISettingService iSettingService
    ) {
        this.settingMapper = settingMapper;
        this.iSettingService = iSettingService;
    }

    public List<Setting> list(Boolean isCustom) {
        LambdaQueryChainWrapper<Setting> settingLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(settingMapper);
        return settingLambdaQueryChainWrapper.eq(Setting::getOptionType, isCustom).list();
    }

    public void update(List<Setting> settings) {
        List<Setting> settingsUpdate = settings.stream().filter(setting -> !setting.getV().isEmpty()).toList();
        List<Setting> settingsDelete = settings.stream().filter(setting -> setting.getV().isEmpty()).toList();

        iSettingService.saveOrUpdateBatch(settingsUpdate);
        iSettingService.removeBatchByIds(settingsDelete);
    }
}
