package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroGroup;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.model.SombreroUserGroup;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;
@ApplicationScoped
public class SombreroUserGroupRepository implements PanacheRepositoryBase<SombreroUserGroup, UUID> {
    @Inject
    Mutiny.SessionFactory sessionFactory;


    public Uni<SombreroUserGroup> findByUserAndGroup(SombreroUser user, SombreroGroup group) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT ug FROM SombreroUserGroup ug WHERE ug.user = :user AND ug.group = :group", SombreroUserGroup.class)
                        .setParameter("user", user)
                        .setParameter("group", group)
                        .getResultList()
                        .onItem()
                        .ifNotNull()
                        .transformToUni(list -> {
                            if (list.size() > 0) {
                                return Uni.createFrom().item(list.get(0));
                            } else {
                                return Uni.createFrom().nullItem();
                            }
                        })
        );
    }

    public void addUserToGroupByEntity(SombreroUser user, SombreroGroup group) {
        SombreroUserGroup userGroup = new SombreroUserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        sessionFactory.withTransaction((session, tx) -> session.merge(userGroup));
    }

    public Uni<SombreroUserGroup> save(SombreroUserGroup userGroup) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(userGroup));
    }
}
