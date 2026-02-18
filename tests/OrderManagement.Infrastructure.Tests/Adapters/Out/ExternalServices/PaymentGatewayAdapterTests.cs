using FluentAssertions;
using OrderManagement.Domain.Ports;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Infrastructure.Tests.Adapters.Out.ExternalServices;

public class PaymentGatewayAdapterTests
{
    private readonly IPaymentGateway _paymentGateway;

    public PaymentGatewayAdapterTests()
    {
        _paymentGateway = new OrderManagement.Infrastructure.Adapters.Out.ExternalServices.PaymentGatewayAdapter();
    }

    [Fact]
    public async Task ProcessPaymentAsync_WhenPaymentIsValid_ShouldReturnSuccessfulResult()
    {
        // Arrange
        var amount = new Money(100.00m, "USD");
        var paymentMethod = "credit_card";
        var transactionId = Guid.NewGuid().ToString();

        // Act
        var result = await _paymentGateway.ProcessPaymentAsync(amount, paymentMethod, transactionId);

        // Assert
        result.Should().NotBeNull();
        result.IsSuccessful.Should().BeTrue();
        result.TransactionId.Should().Be(transactionId);
        result.ErrorMessage.Should().BeNull();
    }

    [Fact]
    public async Task ProcessPaymentAsync_WhenAmountIsZero_ShouldReturnSuccessfulResult()
    {
        // Arrange
        var amount = new Money(0m, "USD");
        var paymentMethod = "credit_card";
        var transactionId = Guid.NewGuid().ToString();

        // Act
        var result = await _paymentGateway.ProcessPaymentAsync(amount, paymentMethod, transactionId);

        // Assert
        result.Should().NotBeNull();
        result.IsSuccessful.Should().BeTrue();
    }

    [Fact]
    public async Task ProcessPaymentAsync_WhenPaymentMethodIsInvalid_ShouldReturnFailedResult()
    {
        // Arrange
        var amount = new Money(100.00m, "USD");
        var paymentMethod = "invalid_method";
        var transactionId = Guid.NewGuid().ToString();

        // Act
        var result = await _paymentGateway.ProcessPaymentAsync(amount, paymentMethod, transactionId);

        // Assert
        result.Should().NotBeNull();
        result.IsSuccessful.Should().BeFalse();
        result.ErrorMessage.Should().NotBeNullOrEmpty();
    }

    [Fact]
    public async Task ProcessPaymentAsync_WhenTransactionIdIsEmpty_ShouldReturnFailedResult()
    {
        // Arrange
        var amount = new Money(100.00m, "USD");
        var paymentMethod = "credit_card";
        var transactionId = string.Empty;

        // Act
        var result = await _paymentGateway.ProcessPaymentAsync(amount, paymentMethod, transactionId);

        // Assert
        result.Should().NotBeNull();
        result.IsSuccessful.Should().BeFalse();
        result.ErrorMessage.Should().NotBeNullOrEmpty();
    }

    [Fact]
    public async Task ProcessPaymentAsync_WhenAmountIsNegative_ShouldReturnFailedResult()
    {
        // Arrange
        var amount = new Money(-10.00m, "USD");
        var paymentMethod = "credit_card";
        var transactionId = Guid.NewGuid().ToString();

        // Act
        var result = await _paymentGateway.ProcessPaymentAsync(amount, paymentMethod, transactionId);

        // Assert
        result.Should().NotBeNull();
        result.IsSuccessful.Should().BeFalse();
        result.ErrorMessage.Should().NotBeNullOrEmpty();
    }

    [Fact]
    public async Task ProcessPaymentAsync_ShouldHandleCancellationToken()
    {
        // Arrange
        var amount = new Money(100.00m, "USD");
        var paymentMethod = "credit_card";
        var transactionId = Guid.NewGuid().ToString();
        var cancellationToken = new CancellationToken();

        // Act
        var result = await _paymentGateway.ProcessPaymentAsync(amount, paymentMethod, transactionId, cancellationToken);

        // Assert
        result.Should().NotBeNull();
        result.IsSuccessful.Should().BeTrue();
    }
}
