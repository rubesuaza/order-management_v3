using FluentAssertions;
using OrderManagement.Domain.Entities;
using OrderManagement.Domain.Exceptions;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Domain.Tests.Entities;

public class OrderTests
{
    [Fact]
    public void Create_WithValidItems_ShouldCreateOrder()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var unitPrice = new Money(25.50m, "USD");
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 2, unitPrice)
        };

        // Act
        var order = Order.Create(items, "USD");

        // Assert
        order.Id.Should().NotBeEmpty();
        order.Items.Should().HaveCount(1);
        order.Status.Should().Be(OrderStatus.Pending);
        order.Currency.Should().Be("USD");
    }

    [Fact]
    public void Create_WithEmptyItems_ShouldThrowInvalidOrderException()
    {
        // Arrange
        var items = new List<OrderItem>();

        // Act
        var act = () => Order.Create(items, "USD");

        // Assert
        act.Should().Throw<InvalidOrderException>()
            .WithMessage("*must contain at least one item*");
    }

    [Fact]
    public void Create_WithNullItems_ShouldThrowInvalidOrderException()
    {
        // Act
        var act = () => Order.Create(null!, "USD");

        // Assert
        act.Should().Throw<InvalidOrderException>();
    }

    [Fact]
    public void GetTotalAmount_ShouldReturnSumOfAllItems()
    {
        // Arrange
        var productId1 = Guid.NewGuid();
        var productId2 = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId1, "Product 1", 2, new Money(25.50m, "USD")),
            new OrderItem(productId2, "Product 2", 3, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");

        // Act
        var total = order.GetTotalAmount();

        // Assert
        total.Amount.Should().Be(81.00m); // (2 * 25.50) + (3 * 10.00) = 51.00 + 30.00 = 81.00
        total.Currency.Should().Be("USD");
    }

    [Fact]
    public void MarkAsPaid_WithMinimumValue_ShouldChangeStatusToPaid()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");

        // Act
        order.MarkAsPaid();

        // Assert
        order.Status.Should().Be(OrderStatus.Paid);
    }

    [Fact]
    public void MarkAsPaid_WithValueBelowMinimum_ShouldThrowInvalidOrderException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(9.99m, "USD"))
        };
        var order = Order.Create(items, "USD");

        // Act
        var act = () => order.MarkAsPaid();

        // Assert
        act.Should().Throw<InvalidOrderException>()
            .WithMessage("*minimum value of 10.00 USD*");
    }

    [Fact]
    public void MarkAsPaid_WhenAlreadyPaid_ShouldThrowInvalidOrderStateException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");
        order.MarkAsPaid();

        // Act
        var act = () => order.MarkAsPaid();

        // Assert
        act.Should().Throw<InvalidOrderStateException>()
            .WithMessage("*cannot transition from Paid to Paid*");
    }

    [Fact]
    public void MarkAsShipped_WhenPaid_ShouldChangeStatusToShipped()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");
        order.MarkAsPaid();

        // Act
        order.MarkAsShipped();

        // Assert
        order.Status.Should().Be(OrderStatus.Shipped);
    }

    [Fact]
    public void MarkAsShipped_WhenNotPaid_ShouldThrowInvalidOrderStateException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");

        // Act
        var act = () => order.MarkAsShipped();

        // Assert
        act.Should().Throw<InvalidOrderStateException>()
            .WithMessage("*can only be shipped from Paid status*");
    }

    [Fact]
    public void Cancel_WhenPending_ShouldChangeStatusToCancelled()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");

        // Act
        order.Cancel();

        // Assert
        order.Status.Should().Be(OrderStatus.Cancelled);
    }

    [Fact]
    public void Cancel_WhenPaid_ShouldChangeStatusToCancelled()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");
        order.MarkAsPaid();

        // Act
        order.Cancel();

        // Assert
        order.Status.Should().Be(OrderStatus.Cancelled);
    }

    [Fact]
    public void Cancel_WhenShipped_ShouldThrowInvalidOrderStateException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");
        order.MarkAsPaid();
        order.MarkAsShipped();

        // Act
        var act = () => order.Cancel();

        // Assert
        act.Should().Throw<InvalidOrderStateException>()
            .WithMessage("*can only be cancelled from Pending or Paid status*");
    }

    [Fact]
    public void Cancel_WhenAlreadyCancelled_ShouldThrowInvalidOrderStateException()
    {
        // Arrange
        var productId = Guid.NewGuid();
        var items = new List<OrderItem>
        {
            new OrderItem(productId, "Product 1", 1, new Money(10.00m, "USD"))
        };
        var order = Order.Create(items, "USD");
        order.Cancel();

        // Act
        var act = () => order.Cancel();

        // Assert
        act.Should().Throw<InvalidOrderStateException>()
            .WithMessage("*cannot transition from Cancelled to Cancelled*");
    }
}
