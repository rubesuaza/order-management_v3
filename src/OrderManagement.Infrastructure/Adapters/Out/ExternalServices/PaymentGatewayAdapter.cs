using OrderManagement.Domain.Ports;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Infrastructure.Adapters.Out.ExternalServices;

public class PaymentGatewayAdapter : IPaymentGateway
{
    private static readonly HashSet<string> ValidPaymentMethods = new(StringComparer.OrdinalIgnoreCase)
    {
        "credit_card",
        "debit_card",
        "paypal",
        "bank_transfer"
    };

    public async Task<PaymentResult> ProcessPaymentAsync(
        Money amount,
        string paymentMethod,
        string transactionId,
        CancellationToken cancellationToken = default)
    {
        // Simulate async operation
        await Task.Delay(100, cancellationToken);

        // Validate inputs
        if (string.IsNullOrWhiteSpace(transactionId))
        {
            return new PaymentResult(
                IsSuccessful: false,
                TransactionId: transactionId,
                ErrorMessage: "Transaction ID cannot be empty."
            );
        }

        if (amount.Amount < 0)
        {
            return new PaymentResult(
                IsSuccessful: false,
                TransactionId: transactionId,
                ErrorMessage: "Payment amount cannot be negative."
            );
        }

        if (!ValidPaymentMethods.Contains(paymentMethod))
        {
            return new PaymentResult(
                IsSuccessful: false,
                TransactionId: transactionId,
                ErrorMessage: $"Invalid payment method: {paymentMethod}. Valid methods are: {string.Join(", ", ValidPaymentMethods)}."
            );
        }

        // Simulate payment processing
        // In a real implementation, this would call an external payment service API
        // For now, we simulate a successful payment for valid inputs
        return new PaymentResult(
            IsSuccessful: true,
            TransactionId: transactionId
        );
    }
}
