package moe.lyu.sapiblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.lyu.sapiblog.entity.Setting;
import moe.lyu.sapiblog.mapper.SettingMapper;
import moe.lyu.sapiblog.service.ISettingService;
import org.springframework.stereotype.Service;

@Service
public class ISettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements ISettingService {
}
