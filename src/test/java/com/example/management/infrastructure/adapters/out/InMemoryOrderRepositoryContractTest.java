package com.example.management.infrastructure.adapters.out;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.infrastructure.adapters.out.persistence.InMemoryOrderRepository;
import org.junit.jupiter.api.DisplayName;

@DisplayName("InMemoryOrderRepository")
class InMemoryOrderRepositoryContractTest extends OrderRepositoryContractTest {

    @Override
    OrderRepository createRepository() {
        return new InMemoryOrderRepository();
    }
}
