package com.madadipouya.cisapify.user.model;

import javax.persistence.*;

@Entity
@Table(name = "roles",
        indexes = {
                @Index(columnList = "role", name = "idx_role_role_name")},
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_role_role_name", columnNames = "role")
        })
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role")
    private String role;

    public long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}