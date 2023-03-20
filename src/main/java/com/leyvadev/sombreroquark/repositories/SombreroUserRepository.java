package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.dto.PaginatedUserResponseDTO;
import com.leyvadev.sombreroquark.model.SombreroUser;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SombreroUserRepository implements PanacheRepositoryBase<SombreroUser, UUID> {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<SombreroUser> findByEmail(String email) {
        return sessionFactory.withSession(session ->
                session.createQuery("SELECT u FROM SombreroUser u LEFT JOIN FETCH u.groups WHERE u.email = :email", SombreroUser.class)
                        .setParameter("email", email)
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

    public Uni<SombreroUser> save(SombreroUser user) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(user));
    }

    public Uni<PaginatedUserResponseDTO> getPaginatedUsers(PaginatedRequestDTO request) {
        int page = request.getPage();
        int pageSize = request.getPageSize();

        return sessionFactory.withSession(session -> {
            Uni<Long> countQuery = session.createQuery("SELECT count(u) FROM SombreroUser u ", Long.class).getSingleResult();
            Uni<List<SombreroUser>> resultsQuery = session.createQuery("SELECT u FROM SombreroUser u LEFT JOIN FETCH u.groups", SombreroUser.class)
                    .setFirstResult(pageSize * (page - 1))
                    .setMaxResults(pageSize)
                    .getResultList();

            return Uni.combine().all().unis(countQuery, resultsQuery)
                    .combinedWith((totalItems, results) -> {
                        PaginatedUserResponseDTO response = new PaginatedUserResponseDTO();
                        response.setUsers(results);
                        response.setCurrentPage(page);
                        response.setPageSize(pageSize);
                        response.setTotalItems(totalItems);
                        return response;
                    });
        });
    }

    public Uni<SombreroUser> update(SombreroUser user) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(user));
    }


}