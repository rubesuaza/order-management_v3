package com.example.ordermanagement.infrastructure.persistence.repository;

import com.example.ordermanagement.infrastructure.persistence.entity.OrderEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for Order persistence.
 * Used by OrderRepositoryAdapter to fulfill the OrderRepository port.
 */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
