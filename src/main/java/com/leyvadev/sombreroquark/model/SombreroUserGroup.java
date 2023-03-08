package com.leyvadev.sombreroquark.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "sombrero_user_group")
public class SombreroUserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private SombreroUser user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private SombreroGroup group;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SombreroUser getUser() {
        return user;
    }

    public void setUser(SombreroUser user) {
        this.user = user;
    }

    public SombreroGroup getGroup() {
        return group;
    }

    public void setGroup(SombreroGroup group) {
        this.group = group;
    }
}
