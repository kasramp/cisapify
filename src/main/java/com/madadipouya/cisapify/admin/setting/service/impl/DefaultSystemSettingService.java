package com.madadipouya.cisapify.admin.setting.service.impl;

import com.madadipouya.cisapify.admin.setting.exception.SettingNotFoundException;
import com.madadipouya.cisapify.admin.setting.model.SystemSetting;
import com.madadipouya.cisapify.admin.setting.repository.SystemSettingRepository;
import com.madadipouya.cisapify.admin.setting.service.SystemSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DefaultSystemSettingService implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public DefaultSystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Override
    public SystemSetting createSetting(SystemSetting systemSetting) {
        return systemSettingRepository.save(systemSetting);
    }

    @Override
    public SystemSetting getSetting(String systemSettingName) throws SettingNotFoundException {
        return systemSettingRepository.getByName(systemSettingName).orElseThrow(() -> new SettingNotFoundException(String.format("Unable to find setting '%s'.", systemSettingName)));
    }

    @Override
    public SystemSetting getSetting(long id) throws SettingNotFoundException {
        return systemSettingRepository.findById(id).orElseThrow(() -> new SettingNotFoundException(String.format("Unable to find setting '%s'.", id)));
    }

    @Override
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }

    @Override
    public SystemSetting updateSetting(long id, SystemSetting updatedSetting) throws SettingNotFoundException {
        SystemSetting systemSetting = systemSettingRepository.findById(id).orElseThrow(() -> new SettingNotFoundException(String.format("Unable to find setting %s.", id)));
        systemSetting.setName(updatedSetting.getName());
        systemSetting.setValue(updatedSetting.getValue());
        systemSetting.setDescription(updatedSetting.getDescription());
        return systemSettingRepository.save(systemSetting);
    }

    @Override
    public void deleteSetting(long id) {
        systemSettingRepository.deleteById(id);
    }

    @Override
    public String getValue(String systemSettingName) {
        return systemSettingRepository.getByName(systemSettingName).map(SystemSetting::getValue).orElse(StringUtils.EMPTY);
    }
}
