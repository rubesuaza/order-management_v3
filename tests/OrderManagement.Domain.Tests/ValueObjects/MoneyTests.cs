using FluentAssertions;
using OrderManagement.Domain.ValueObjects;
using OrderManagement.Domain.Exceptions;

namespace OrderManagement.Domain.Tests.ValueObjects;

public class MoneyTests
{
    [Fact]
    public void Create_WithValidAmountAndCurrency_ShouldCreateMoney()
    {
        // Arrange & Act
        var money = new Money(100.50m, "USD");

        // Assert
        money.Amount.Should().Be(100.50m);
        money.Currency.Should().Be("USD");
    }

    [Fact]
    public void Add_WithSameCurrency_ShouldReturnSum()
    {
        // Arrange
        var money1 = new Money(50.00m, "USD");
        var money2 = new Money(25.50m, "USD");

        // Act
        var result = money1 + money2;

        // Assert
        result.Amount.Should().Be(75.50m);
        result.Currency.Should().Be("USD");
    }

    [Fact]
    public void Add_WithDifferentCurrencies_ShouldThrowCurrencyMismatchException()
    {
        // Arrange
        var money1 = new Money(50.00m, "USD");
        var money2 = new Money(25.50m, "EUR");

        // Act
        var act = () => money1 + money2;

        // Assert
        act.Should().Throw<CurrencyMismatchException>()
            .WithMessage("*different currencies*");
    }

    [Fact]
    public void Subtract_WithSameCurrency_ShouldReturnDifference()
    {
        // Arrange
        var money1 = new Money(100.00m, "USD");
        var money2 = new Money(25.50m, "USD");

        // Act
        var result = money1 - money2;

        // Assert
        result.Amount.Should().Be(74.50m);
        result.Currency.Should().Be("USD");
    }

    [Fact]
    public void Subtract_WithDifferentCurrencies_ShouldThrowCurrencyMismatchException()
    {
        // Arrange
        var money1 = new Money(100.00m, "USD");
        var money2 = new Money(25.50m, "EUR");

        // Act
        var act = () => money1 - money2;

        // Assert
        act.Should().Throw<CurrencyMismatchException>()
            .WithMessage("*different currencies*");
    }

    [Fact]
    public void Multiply_WithDecimal_ShouldReturnMultipliedAmount()
    {
        // Arrange
        var money = new Money(50.00m, "USD");

        // Act
        var result = money * 2.5m;

        // Assert
        result.Amount.Should().Be(125.00m);
        result.Currency.Should().Be("USD");
    }

    [Fact]
    public void Equals_WithSameAmountAndCurrency_ShouldReturnTrue()
    {
        // Arrange
        var money1 = new Money(100.00m, "USD");
        var money2 = new Money(100.00m, "USD");

        // Act & Assert
        money1.Should().Be(money2);
        (money1 == money2).Should().BeTrue();
    }

    [Fact]
    public void Equals_WithDifferentAmount_ShouldReturnFalse()
    {
        // Arrange
        var money1 = new Money(100.00m, "USD");
        var money2 = new Money(50.00m, "USD");

        // Act & Assert
        money1.Should().NotBe(money2);
        (money1 != money2).Should().BeTrue();
    }

    [Fact]
    public void Equals_WithDifferentCurrency_ShouldReturnFalse()
    {
        // Arrange
        var money1 = new Money(100.00m, "USD");
        var money2 = new Money(100.00m, "EUR");

        // Act & Assert
        money1.Should().NotBe(money2);
    }
}
