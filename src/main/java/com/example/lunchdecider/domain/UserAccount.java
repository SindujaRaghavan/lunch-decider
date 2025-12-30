package com.example.lunchdecider.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_account", uniqueConstraints = @UniqueConstraint(name = "uq_user_username", columnNames = "username"))
public class UserAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    protected UserAccount() {}

    public UserAccount(String username) {
        this.username = username;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
}
