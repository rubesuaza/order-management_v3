package com.example.management.infrastructure.persistence.repository;

import com.example.management.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for OrderEntity.
 * Technical persistence interface used by the adapter.
 */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
