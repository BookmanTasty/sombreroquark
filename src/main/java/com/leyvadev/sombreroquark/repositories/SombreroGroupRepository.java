package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroGroup;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
@ApplicationScoped
public class SombreroGroupRepository implements PanacheRepositoryBase<SombreroGroup, UUID> {

    @Inject
    Mutiny.SessionFactory sessionFactory;
    public Uni<SombreroGroup> findByName(String name) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT g FROM SombreroGroup g LEFT JOIN FETCH g.permissions WHERE g.name = :name", SombreroGroup.class)
                        .setParameter("name", name)
                        .getResultList()
                        .onItem()
                        .ifNotNull()
                        .transformToUni(list -> {
                            if (!list.isEmpty()) {
                                return Uni.createFrom().item(list.get(0));
                            } else {
                                return Uni.createFrom().nullItem();
                            }
                        })
        );
    }

    public Uni<SombreroGroup> findByUUID(String id) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT g FROM SombreroGroup g LEFT JOIN FETCH g.permissions WHERE g.id = :id", SombreroGroup.class)
                        .setParameter("id", UUID.fromString(id))
                        .getResultList()
                        .onItem()
                        .ifNotNull()
                        .transformToUni(list -> {
                            if (!list.isEmpty()) {
                                return Uni.createFrom().item(list.get(0));
                            } else {
                                return Uni.createFrom().nullItem();
                            }
                        })
        );
    }

    public Uni<List<SombreroGroup>> getGroups() {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT DISTINCT g FROM SombreroGroup g LEFT JOIN FETCH g.permissions", SombreroGroup.class)
                        .getResultList()
        );
    }

    public Uni<SombreroGroup> save(SombreroGroup group) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(group));
    }

    public Uni<Integer> getSmallestPriorityByGroupNames(List<String> groupNames) {
        String query = "SELECT COALESCE(MIN(g.priority), 99999) FROM SombreroGroup g WHERE g.name IN (:groupNames) AND g.priority IS NOT NULL";
        return sessionFactory.withSession(session -> session.createQuery(query, Integer.class)
                .setParameter("groupNames", groupNames)
                .getSingleResult());
    }
}
