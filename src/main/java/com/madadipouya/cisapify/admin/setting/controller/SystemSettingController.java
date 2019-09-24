package com.madadipouya.cisapify.admin.setting.controller;

import com.madadipouya.cisapify.admin.setting.exception.SettingNotFoundException;
import com.madadipouya.cisapify.admin.setting.model.SystemSetting;
import com.madadipouya.cisapify.admin.setting.service.SystemSettingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/system/settings")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    public SystemSettingController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SystemSetting createSystemSetting(@Valid SystemSettingDto systemSettingDto) {
        return systemSettingService.createSetting(systemSettingDto.getAsSystemSetting());
    }

    @GetMapping(value = "/{name}")
    @ResponseStatus(HttpStatus.OK)
    private SystemSetting getSystemSettingByName(@PathVariable String name) throws SettingNotFoundException {
        return systemSettingService.getSetting(name);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    private SystemSetting updateSystemSetting(@PathVariable long id, @Valid SystemSettingDto systemSettingDto) throws SettingNotFoundException {
        return systemSettingService.updateSetting(id, systemSettingDto.getAsSystemSetting());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    private List<SystemSetting> getAllSystemSettings() {
        return systemSettingService.getAllSettings();
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteSystemSetting(@PathVariable long id) {
        systemSettingService.deleteSetting(id);
    }

    public static class SystemSettingDto {
        @NotBlank
        @Size(min = 8, max = 1024)
        private String name;

        @NotBlank
        @Size(min = 2, max = 1024)
        private String value;

        @NotBlank
        @Size(min = 2, max = 1024)
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public SystemSetting getAsSystemSetting() {
            SystemSetting systemSetting = new SystemSetting();
            systemSetting.setName(name);
            systemSetting.setValue(value);
            systemSetting.setDescription(description);
            return systemSetting;
        }
    }
}
