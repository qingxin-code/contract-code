package com.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.SysConfig;

public interface ISysConfigService extends IService<SysConfig> {
    String getValueByKey(String key);
}
