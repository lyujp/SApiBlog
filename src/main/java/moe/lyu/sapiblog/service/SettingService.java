package moe.lyu.sapiblog.service;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.mapper.SettingMapper;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        LambdaQueryWrapper<Setting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setting::getOptionType, isCustom);
        return settingMapper.selectList(lambdaQueryWrapper);
    }

    public void update(List<Setting> settings) {
        List<Setting> settingsUpdate = settings.stream().filter(setting -> !setting.getV().isEmpty()).toList();
        List<Setting> settingsDelete = settings.stream().filter(setting -> setting.getV().isEmpty()).toList();

        iSettingService.saveOrUpdateBatch(settingsUpdate);
        iSettingService.removeBatchByIds(settingsDelete);
    }
}
