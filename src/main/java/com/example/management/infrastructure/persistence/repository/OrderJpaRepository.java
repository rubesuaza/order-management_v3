package com.example.management.infrastructure.persistence.repository;

import com.example.management.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for orders. Used only by infrastructure adapters.
 * Use {@link #findByIdWithItems(UUID)} when loading an order to map to domain to avoid N+1 on items.
 */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") UUID id);
}
