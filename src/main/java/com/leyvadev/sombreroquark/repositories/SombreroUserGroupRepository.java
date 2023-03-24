package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroGroup;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.model.SombreroUserGroup;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class SombreroUserGroupRepository {

    @Inject
    Mutiny.SessionFactory sessionFactory;


    public Uni<SombreroUserGroup> findByUserAndGroup(SombreroUser user, SombreroGroup group) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT ug FROM SombreroUserGroup ug WHERE ug.user = :user AND ug.group = :group", SombreroUserGroup.class)
                        .setParameter("user", user)
                        .setParameter("group", group)
                        .getSingleResultOrNull()
        );
    }
    public Uni<Boolean> findByUserAndGroupExists(UUID userId, UUID groupId) {
        return sessionFactory.withSession(session -> session
                .createNativeQuery("SELECT COUNT(*) FROM sombrero_user_groups WHERE user_id = :userId AND group_id = :groupId")
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .getSingleResult()
                .flatMap(result -> Uni.createFrom().item(((Number) result).intValue() > 0))
        );
    }

    public Uni<Void> addUserToGroupByUUID(UUID userId, UUID groupId) {
        String nativeQuery = "INSERT INTO sombrero_user_groups (user_id, group_id) VALUES (?, ?)";
        return sessionFactory.withTransaction((session, tx) -> session.createNativeQuery(nativeQuery)
                .setParameter(1, userId)
                .setParameter(2, groupId)
                .executeUpdate()
                .chain(() -> Uni.createFrom().nullItem()));
    }

    public Uni<Void> removeUserFromGroupByUUID(UUID userId, UUID groupId) {
        String nativeQuery = "DELETE FROM sombrero_user_groups WHERE user_id = ? AND group_id = ?";
        return sessionFactory.withTransaction((session, tx) -> session.createNativeQuery(nativeQuery)
                .setParameter(1, userId)
                .setParameter(2, groupId)
                .executeUpdate()
                .chain(() -> Uni.createFrom().nullItem()));
    }


    public Uni<SombreroUserGroup> save(SombreroUserGroup userGroup) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(userGroup).chain(() -> Uni.createFrom().item(userGroup)));
    }

}
