using OrderManagement.Domain.Exceptions;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Domain.Entities;

public class OrderItem
{
    public Guid ProductId { get; private set; }
    public string ProductName { get; private set; }
    public int Quantity { get; private set; }
    public Money UnitPrice { get; private set; }

    public OrderItem(Guid productId, string productName, int quantity, Money unitPrice)
    {
        if (quantity <= 0)
        {
            throw new InvalidItemException("Item quantity must be greater than zero.");
        }

        if (unitPrice.Amount < 0)
        {
            throw new InvalidItemException("Item unit price cannot be negative.");
        }

        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        UnitPrice = unitPrice;
    }

    public Money GetTotal()
    {
        return UnitPrice * Quantity;
    }
}
