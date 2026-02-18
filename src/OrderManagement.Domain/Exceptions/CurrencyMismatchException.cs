namespace OrderManagement.Domain.Exceptions;

public class CurrencyMismatchException : DomainException
{
    public CurrencyMismatchException(string message) : base(message)
    {
    }

    public CurrencyMismatchException(string currency1, string currency2)
        : base($"Cannot perform operation with different currencies: {currency1} and {currency2}")
    {
    }
}
