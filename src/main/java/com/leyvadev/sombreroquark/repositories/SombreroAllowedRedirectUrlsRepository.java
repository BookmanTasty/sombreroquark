package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.dto.PaginatedRequestDTO;
import com.leyvadev.sombreroquark.dto.PaginatedURLResponseDTO;
import com.leyvadev.sombreroquark.model.SombreroAllowedRedirectUrl;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
@ApplicationScoped
public class SombreroAllowedRedirectUrlsRepository implements PanacheRepositoryBase<SombreroAllowedRedirectUrl, UUID> {
    @Inject
    Mutiny.SessionFactory sessionFactory;
    public Uni<SombreroAllowedRedirectUrl> existsByUrlAndIsActive(String url) {
        return this.find("url = ?1 and isActive = true", url).firstResult();
    }
    public Uni<SombreroAllowedRedirectUrl> findByUrl(String url) {
        return this.find("url = ?1", url).firstResult();
    }
    public Uni<SombreroAllowedRedirectUrl> findByUUID(UUID id) {
        return this.find("id = ?1", id).firstResult();
    }
    public Uni<SombreroAllowedRedirectUrl> save(SombreroAllowedRedirectUrl allowedRedirectUrl) {
        return sessionFactory.withTransaction((session, tx) -> session.merge(allowedRedirectUrl));
    }
    public Uni<PaginatedURLResponseDTO> getPaginatedUrls(PaginatedRequestDTO request) {
        int page = request.getPage();
        int pageSize = request.getPageSize();
        return sessionFactory.withSession(session -> {
            Uni<Long> countQuery = session.createQuery("SELECT count(u) FROM SombreroAllowedRedirectUrl u ", Long.class).getSingleResult();
            Uni<List<SombreroAllowedRedirectUrl>> resultsQuery = session.createQuery("SELECT u FROM SombreroAllowedRedirectUrl u", SombreroAllowedRedirectUrl.class)
                    .setFirstResult(pageSize * (page - 1))
                    .setMaxResults(pageSize)
                    .getResultList();
            return Uni.combine().all().unis(countQuery, resultsQuery)
                    .combinedWith((totalItems, results) -> {
                        PaginatedURLResponseDTO response = new PaginatedURLResponseDTO();
                        response.setUrls(results);
                        response.setCurrentPage(page);
                        response.setPageSize(pageSize);
                        response.setTotalItems(totalItems);
                        return response;
                    });
        });
    }
}
