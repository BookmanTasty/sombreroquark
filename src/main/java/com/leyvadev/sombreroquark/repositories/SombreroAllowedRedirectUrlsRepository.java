package com.leyvadev.sombreroquark.repositories;

import com.leyvadev.sombreroquark.model.SombreroAllowedRedirectUrls;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
@ApplicationScoped
public class SombreroAllowedRedirectUrlsRepository implements PanacheRepositoryBase<SombreroAllowedRedirectUrls, UUID> {
    public Uni<SombreroAllowedRedirectUrls> existsByUrlAndIsActive(String url) {
        return this.find("url = ?1 and isActive = true", url).firstResult();
    }
}
