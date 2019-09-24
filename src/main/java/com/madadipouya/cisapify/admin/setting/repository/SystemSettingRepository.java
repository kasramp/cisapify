package com.madadipouya.cisapify.admin.setting.repository;

import com.madadipouya.cisapify.admin.setting.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    
    Optional<SystemSetting> getByName(String name);
}
