using OrderManagement.Domain.Entities;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Infrastructure.Adapters.Out.Persistence.Entities;

public class OrderEntity
{
    public Guid Id { get; set; }
    public OrderStatus Status { get; set; }
    public string Currency { get; set; } = string.Empty;
    public Address? ShippingAddress { get; set; }
    public Address? BillingAddress { get; set; }
    public List<OrderItemEntity> Items { get; set; } = new();
}
