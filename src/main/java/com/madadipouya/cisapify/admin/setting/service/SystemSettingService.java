package com.madadipouya.cisapify.admin.setting.service;

import com.madadipouya.cisapify.admin.setting.exception.SettingNotFoundException;
import com.madadipouya.cisapify.admin.setting.model.SystemSetting;

import java.util.List;

public interface SystemSettingService {

    SystemSetting createSetting(SystemSetting systemSetting);

    SystemSetting getSetting(String systemSettingName) throws SettingNotFoundException;

    List<SystemSetting> getAllSettings();

    SystemSetting updateSetting(long id, SystemSetting updatedSetting) throws SettingNotFoundException;

    void deleteSetting(long id);

    String getValue(String systemSettingName);
}
