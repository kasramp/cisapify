package com.madadipouya.cisapify.admin.setting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "system_settings", indexes = @Index(columnList = "name", name = "idx_system_settings_name"),
        uniqueConstraints = @UniqueConstraint(name = "uc_system_settings_name", columnNames = {"name"}))
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 8, max = 1024)
    private String name;

    @Column(name = "value", nullable = false)
    @NotBlank
    @Size(min = 2, max = 1024)
    private String value;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 2, max = 1024)
    private String description;

    @Column(name = "creation_date", nullable = false)
    @NotBlank
    private LocalDateTime creationDate;

    @Column(name = "last_update_date", nullable = false)
    @NotBlank
    private LocalDateTime lastUpdatedDate;

    @PrePersist
    public void onPersist() {
        this.creationDate = LocalDateTime.now(ZoneOffset.UTC);
        this.lastUpdatedDate = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedDate = LocalDateTime.now(ZoneOffset.UTC);
    }

    public long getId() {
        return id;
    }

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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }
}
