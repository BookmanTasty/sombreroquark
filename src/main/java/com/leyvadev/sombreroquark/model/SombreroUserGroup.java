package com.leyvadev.sombreroquark.model;

import com.leyvadev.sombreroquark.model.idClasses.SombreroUserGroupId;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "sombrero_user_groups")
@IdClass(SombreroUserGroupId.class)
public class SombreroUserGroup {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private SombreroUser user;

    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private SombreroGroup group;

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
