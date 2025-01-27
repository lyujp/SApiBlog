package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.exception.SettingNotExistException;
import moe.lyu.sapiblog.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

//    public void update(List<Setting> settings) {
//        settings.forEach(setting -> setting.setOptionType(null));
//        List<Setting> settingsUpdate = settings.stream().filter(setting -> !setting.getV().isEmpty()).toList();
//        List<Setting> settingsDelete = settings.stream().filter(setting -> setting.getV().isEmpty()).toList();
//
//        iSettingService.saveOrUpdateBatch(settingsUpdate);
//        iSettingService.removeBatchByIds(settingsDelete);
//    }

    public void update(Map<String,String> settings) {
        LambdaQueryChainWrapper<Setting> settingLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(settingMapper);
        List<String> systemSettingsKey = settingLambdaQueryChainWrapper.eq(Setting::getOptionType, false).list()
                        .stream().map(Setting::getK).toList();
        List<Setting> settingsUpdate = new ArrayList<>();
        List<Setting> settingsDelete = new ArrayList<>();
        settings.forEach((k,v)->{
            if (v == null || v.isEmpty()) {
                if(systemSettingsKey.contains(k)){
                    Setting systemSetting = new Setting();
                    systemSetting.setK(k);
                    if(v == null){
                        v = "";
                    }
                    systemSetting.setV(v);
                    systemSetting.setOptionType(false);
                    settingsUpdate.add(systemSetting);
                }else{
                    Setting deleteSetting = new Setting();
                    deleteSetting.setK(k);
                    settingsDelete.add(deleteSetting);
                }
            }else{
                Setting customSetting = new Setting();
                customSetting.setK(k);
                customSetting.setV(v);
                customSetting.setOptionType(true);
                settingsUpdate.add(customSetting);
            }
        });

        iSettingService.saveOrUpdateBatch(settingsUpdate);
        iSettingService.removeBatchByIds(settingsDelete);
    }

    public String getValue(String k) throws SettingNotExistException {
        return get(k).getV();
    }

    public Setting get(String k) throws SettingNotExistException {
        LambdaQueryChainWrapper<Setting> settingLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(settingMapper);
        Setting one = settingLambdaQueryChainWrapper.eq(Setting::getK, k).one();
        if (one == null) {
            throw new SettingNotExistException(k + " is not exist");
        }
        return one;
    }
}
