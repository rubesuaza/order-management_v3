using OrderManagement.Domain.Exceptions;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Domain.Entities;

public class Order
{
    public Guid Id { get; private set; }
    public List<OrderItem> Items { get; private set; }
    public OrderStatus Status { get; private set; }
    public string Currency { get; private set; }
    public Address? ShippingAddress { get; private set; }
    public Address? BillingAddress { get; private set; }

    private const decimal MinimumOrderValue = 10.00m;

    private Order()
    {
        // Private constructor for factory pattern
        Items = new List<OrderItem>();
        Currency = string.Empty;
    }

    public static Order Create(List<OrderItem> items, string currency)
    {
        if (items == null || items.Count == 0)
        {
            throw new InvalidOrderException("Order must contain at least one item.");
        }

        var order = new Order
        {
            Id = Guid.NewGuid(),
            Items = new List<OrderItem>(items),
            Status = OrderStatus.Pending,
            Currency = currency
        };

        return order;
    }

    public Money GetTotalAmount()
    {
        var total = new Money(0m, Currency);
        foreach (var item in Items)
        {
            total = total + item.GetTotal();
        }
        return total;
    }

    public void MarkAsPaid()
    {
        if (Status == OrderStatus.Paid)
        {
            throw new InvalidOrderStateException(
                $"Order cannot transition from {Status} to {OrderStatus.Paid}.");
        }

        var total = GetTotalAmount();
        if (total.Amount < MinimumOrderValue)
        {
            throw new InvalidOrderException(
                $"Order must have a minimum value of {MinimumOrderValue} USD before it can be marked as paid. Current total: {total.Amount} {total.Currency}.");
        }

        Status = OrderStatus.Paid;
    }

    public void MarkAsShipped()
    {
        if (Status != OrderStatus.Paid)
        {
            throw new InvalidOrderStateException(
                $"Order can only be shipped from {OrderStatus.Paid} status. Current status: {Status}.");
        }

        Status = OrderStatus.Shipped;
    }

    public void Cancel()
    {
        if (Status == OrderStatus.Cancelled)
        {
            throw new InvalidOrderStateException(
                $"Order cannot transition from {Status} to {OrderStatus.Cancelled}.");
        }

        if (Status == OrderStatus.Shipped)
        {
            throw new InvalidOrderStateException(
                $"Order can only be cancelled from {OrderStatus.Pending} or {OrderStatus.Paid} status. Current status: {Status}.");
        }

        Status = OrderStatus.Cancelled;
    }

    public void SetShippingAddress(Address address)
    {
        ShippingAddress = address;
    }

    public void SetBillingAddress(Address address)
    {
        BillingAddress = address;
    }
}
