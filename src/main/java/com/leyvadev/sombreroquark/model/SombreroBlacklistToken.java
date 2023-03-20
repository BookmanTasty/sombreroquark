package com.leyvadev.sombreroquark.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "sombrero_blacklist_tokens")
public class SombreroBlacklistToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token", columnDefinition = "TEXT", unique = true, nullable = false)
    private String token;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
