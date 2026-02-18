using FluentAssertions;
using Microsoft.EntityFrameworkCore;
using OrderManagement.Domain.Entities;
using OrderManagement.Domain.Ports;
using OrderManagement.Domain.ValueObjects;
using OrderManagement.Infrastructure.Adapters.Out.Persistence;
using OrderManagement.Infrastructure.Configuration;

namespace OrderManagement.Infrastructure.Tests.Adapters.Out.Persistence;

public class OrderRepositoryTests : IDisposable
{
    private readonly OrderDbContext _dbContext;
    private readonly IOrderRepository _repository;

    public OrderRepositoryTests()
    {
        var options = new DbContextOptionsBuilder<OrderDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        _dbContext = new OrderDbContext(options);
        _repository = new OrderRepository(_dbContext);
    }

    [Fact]
    public async Task GetByIdAsync_WhenOrderExists_ShouldReturnOrder()
    {
        // Arrange
        var order = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 1", 2, new Money(10.00m, "USD"))
            },
            "USD"
        );

        await _repository.SaveAsync(order);

        // Act
        var result = await _repository.GetByIdAsync(order.Id);

        // Assert
        result.Should().NotBeNull();
        result!.Id.Should().Be(order.Id);
        result.Items.Should().HaveCount(1);
    }

    [Fact]
    public async Task GetByIdAsync_WhenOrderDoesNotExist_ShouldReturnNull()
    {
        // Arrange
        var nonExistentId = Guid.NewGuid();

        // Act
        var result = await _repository.GetByIdAsync(nonExistentId);

        // Assert
        result.Should().BeNull();
    }

    [Fact]
    public async Task GetAllAsync_WhenOrdersExist_ShouldReturnAllOrders()
    {
        // Arrange
        var order1 = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 1", 1, new Money(10.00m, "USD"))
            },
            "USD"
        );

        var order2 = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 2", 2, new Money(15.00m, "USD"))
            },
            "USD"
        );

        await _repository.SaveAsync(order1);
        await _repository.SaveAsync(order2);

        // Act
        var result = await _repository.GetAllAsync();

        // Assert
        result.Should().HaveCount(2);
        result.Should().Contain(o => o.Id == order1.Id);
        result.Should().Contain(o => o.Id == order2.Id);
    }

    [Fact]
    public async Task GetAllAsync_WhenNoOrdersExist_ShouldReturnEmptyCollection()
    {
        // Act
        var result = await _repository.GetAllAsync();

        // Assert
        result.Should().BeEmpty();
    }

    [Fact]
    public async Task SaveAsync_WhenOrderIsNew_ShouldCreateOrder()
    {
        // Arrange
        var order = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 1", 1, new Money(10.00m, "USD"))
            },
            "USD"
        );

        // Act
        var result = await _repository.SaveAsync(order);

        // Assert
        result.Should().NotBeNull();
        result.Id.Should().Be(order.Id);
        
        var savedOrder = await _repository.GetByIdAsync(order.Id);
        savedOrder.Should().NotBeNull();
        savedOrder!.Items.Should().HaveCount(1);
    }

    [Fact]
    public async Task SaveAsync_WhenOrderExists_ShouldUpdateOrder()
    {
        // Arrange
        var order = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 1", 1, new Money(10.00m, "USD"))
            },
            "USD"
        );

        await _repository.SaveAsync(order);
        order.MarkAsPaid();

        // Act
        var result = await _repository.SaveAsync(order);

        // Assert
        result.Status.Should().Be(OrderStatus.Paid);
        
        var updatedOrder = await _repository.GetByIdAsync(order.Id);
        updatedOrder.Should().NotBeNull();
        updatedOrder!.Status.Should().Be(OrderStatus.Paid);
    }

    [Fact]
    public async Task SaveAsync_WhenOrderHasAddresses_ShouldPersistAddresses()
    {
        // Arrange
        var order = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 1", 1, new Money(10.00m, "USD"))
            },
            "USD"
        );

        var shippingAddress = new Address("123 Main St", "New York", "NY", "10001", "USA");
        var billingAddress = new Address("456 Oak Ave", "Los Angeles", "CA", "90001", "USA");

        order.SetShippingAddress(shippingAddress);
        order.SetBillingAddress(billingAddress);

        // Act
        var result = await _repository.SaveAsync(order);

        // Assert
        result.Should().NotBeNull();
        
        var savedOrder = await _repository.GetByIdAsync(order.Id);
        savedOrder.Should().NotBeNull();
        savedOrder!.ShippingAddress.Should().Be(shippingAddress);
        savedOrder.BillingAddress.Should().Be(billingAddress);
    }

    [Fact]
    public async Task DeleteAsync_WhenOrderExists_ShouldDeleteOrder()
    {
        // Arrange
        var order = Order.Create(
            new List<OrderItem>
            {
                new OrderItem(Guid.NewGuid(), "Product 1", 1, new Money(10.00m, "USD"))
            },
            "USD"
        );

        await _repository.SaveAsync(order);

        // Act
        var result = await _repository.DeleteAsync(order.Id);

        // Assert
        result.Should().BeTrue();
        
        var deletedOrder = await _repository.GetByIdAsync(order.Id);
        deletedOrder.Should().BeNull();
    }

    [Fact]
    public async Task DeleteAsync_WhenOrderDoesNotExist_ShouldReturnFalse()
    {
        // Arrange
        var nonExistentId = Guid.NewGuid();

        // Act
        var result = await _repository.DeleteAsync(nonExistentId);

        // Assert
        result.Should().BeFalse();
    }

    public void Dispose()
    {
        _dbContext.Dispose();
    }
}
