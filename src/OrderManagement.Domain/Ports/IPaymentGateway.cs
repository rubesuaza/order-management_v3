using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Domain.Ports;

public interface IPaymentGateway
{
    Task<PaymentResult> ProcessPaymentAsync(
        Money amount,
        string paymentMethod,
        string transactionId,
        CancellationToken cancellationToken = default);
}

public record PaymentResult(
    bool IsSuccessful,
    string TransactionId,
    string? ErrorMessage = null
);
