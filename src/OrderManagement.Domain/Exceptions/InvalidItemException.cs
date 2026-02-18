namespace OrderManagement.Domain.Exceptions;

public class InvalidItemException : DomainException
{
    public InvalidItemException(string message) : base(message)
    {
    }
}
