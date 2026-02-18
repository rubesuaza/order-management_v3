namespace OrderManagement.Infrastructure.Adapters.Out.Persistence.Entities;

public class OrderItemEntity
{
    public Guid OrderId { get; set; }
    public Guid ProductId { get; set; }
    public string ProductName { get; set; } = string.Empty;
    public int Quantity { get; set; }
    public decimal UnitPriceAmount { get; set; }
    public string UnitPriceCurrency { get; set; } = string.Empty;
}
