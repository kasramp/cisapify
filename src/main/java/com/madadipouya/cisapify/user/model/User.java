package com.madadipouya.cisapify.user.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users",
        indexes = {
                @Index(columnList = "email_address", name = "idx_user_email_address")},
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_user_email_address", columnNames = "email_address")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email_address", nullable = false)
    @NotBlank
    @Email
    @Size(max = 512)
    private String emailAddress;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(max = 1024)
    private String password;

    public User(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public User() {

    }

    public long getId() {
        return id;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
