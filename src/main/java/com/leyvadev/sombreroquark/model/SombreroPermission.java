package com.leyvadev.sombreroquark.model;


import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "sombrero_permissions")
public class SombreroPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public SombreroPermission() {}

    public SombreroPermission(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}