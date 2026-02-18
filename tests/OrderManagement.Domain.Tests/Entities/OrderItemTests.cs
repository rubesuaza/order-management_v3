using FluentAssertions;
using OrderManagement.Domain.Entities;
using OrderManagement.Domain.Exceptions;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Domain.Tests.Entities;

public class OrderItemTests
{
    [Fact]
    public void Create_WithValidProperties_ShouldCreateOrderItem()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var unitPrice = new Money(25.50m, "USD");

        // Act
        var orderItem = new OrderItem(productId, "Test Product", 2, unitPrice);

        // Assert
        orderItem.ProductId.Should().Be(productId);
        orderItem.ProductName.Should().Be("Test Product");
        orderItem.Quantity.Should().Be(2);
        orderItem.UnitPrice.Should().Be(unitPrice);
    }

    [Fact]
    public void Create_WithZeroQuantity_ShouldThrowInvalidItemException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var unitPrice = new Money(25.50m, "USD");

        // Act
        var act = () => new OrderItem(productId, "Test Product", 0, unitPrice);

        // Assert
        act.Should().Throw<InvalidItemException>()
            .WithMessage("*quantity must be greater than zero*");
    }

    [Fact]
    public void Create_WithNegativeQuantity_ShouldThrowInvalidItemException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var unitPrice = new Money(25.50m, "USD");

        // Act
        var act = () => new OrderItem(productId, "Test Product", -1, unitPrice);

        // Assert
        act.Should().Throw<InvalidItemException>()
            .WithMessage("*quantity must be greater than zero*");
    }

    [Fact]
    public void Create_WithNegativeUnitPrice_ShouldThrowInvalidItemException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var negativePrice = new Money(-10.00m, "USD");

        // Act
        var act = () => new OrderItem(productId, "Test Product", 2, negativePrice);

        // Assert
        act.Should().Throw<InvalidItemException>()
            .WithMessage("*unit price cannot be negative*");
    }

    [Fact]
    public void GetTotal_ShouldReturnQuantityTimesUnitPrice()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var unitPrice = new Money(25.50m, "USD");
        var orderItem = new OrderItem(productId, "Test Product", 3, unitPrice);

        // Act
        var total = orderItem.GetTotal();

        // Assert
        total.Amount.Should().Be(76.50m);
        total.Currency.Should().Be("USD");
    }
}
