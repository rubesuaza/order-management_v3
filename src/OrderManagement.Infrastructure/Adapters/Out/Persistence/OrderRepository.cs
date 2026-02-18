using Microsoft.EntityFrameworkCore;
using OrderManagement.Domain.Entities;
using OrderManagement.Domain.Ports;
using OrderManagement.Infrastructure.Adapters.Out.Persistence.Entities;
using OrderManagement.Infrastructure.Configuration;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Infrastructure.Adapters.Out.Persistence;

public class OrderRepository : IOrderRepository
{
    private readonly OrderDbContext _dbContext;

    public OrderRepository(OrderDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<Order?> GetByIdAsync(Guid orderId, CancellationToken cancellationToken = default)
    {
        var orderEntity = await _dbContext.Orders
            .AsNoTracking()
            .Include(o => o.Items)
            .FirstOrDefaultAsync(o => o.Id == orderId, cancellationToken);

        if (orderEntity == null)
        {
            return null;
        }

        return MapToDomain(orderEntity);
    }

    public async Task<IEnumerable<Order>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var orderEntities = await _dbContext.Orders
            .AsNoTracking()
            .Include(o => o.Items)
            .ToListAsync(cancellationToken);

        return orderEntities.Select(MapToDomain);
    }

    public async Task<Order> SaveAsync(Order order, CancellationToken cancellationToken = default)
    {
        var existingEntity = await _dbContext.Orders
            .Include(o => o.Items)
            .FirstOrDefaultAsync(o => o.Id == order.Id, cancellationToken);

        if (existingEntity == null)
        {
            var newEntity = MapToEntity(order);
            _dbContext.Orders.Add(newEntity);
        }
        else
        {
            UpdateEntity(existingEntity, order);
        }

        await _dbContext.SaveChangesAsync(cancellationToken);
        return order;
    }

    public async Task<bool> DeleteAsync(Guid orderId, CancellationToken cancellationToken = default)
    {
        var orderEntity = await _dbContext.Orders
            .Include(o => o.Items)
            .FirstOrDefaultAsync(o => o.Id == orderId, cancellationToken);

        if (orderEntity == null)
        {
            return false;
        }

        _dbContext.Orders.Remove(orderEntity);
        await _dbContext.SaveChangesAsync(cancellationToken);
        return true;
    }

    private static OrderEntity MapToEntity(Order order)
    {
        return new OrderEntity
        {
            Id = order.Id,
            Status = order.Status,
            Currency = order.Currency,
            ShippingAddress = order.ShippingAddress,
            BillingAddress = order.BillingAddress,
            Items = order.Items.Select(item => new OrderItemEntity
            {
                OrderId = order.Id,
                ProductId = item.ProductId,
                ProductName = item.ProductName,
                Quantity = item.Quantity,
                UnitPriceAmount = item.UnitPrice.Amount,
                UnitPriceCurrency = item.UnitPrice.Currency
            }).ToList()
        };
    }

    private void UpdateEntity(OrderEntity entity, Order order)
    {
        entity.Status = order.Status;
        entity.Currency = order.Currency;
        entity.ShippingAddress = order.ShippingAddress;
        entity.BillingAddress = order.BillingAddress;

        // Remove existing items
        _dbContext.OrderItems.RemoveRange(entity.Items);
        
        // Add updated items
        entity.Items = order.Items.Select(item => new OrderItemEntity
        {
            OrderId = order.Id,
            ProductId = item.ProductId,
            ProductName = item.ProductName,
            Quantity = item.Quantity,
            UnitPriceAmount = item.UnitPrice.Amount,
            UnitPriceCurrency = item.UnitPrice.Currency
        }).ToList();
    }

    private static Order MapToDomain(OrderEntity entity)
    {
        var items = entity.Items
            .Select(item => new OrderItem(
                item.ProductId,
                item.ProductName,
                item.Quantity,
                new Money(item.UnitPriceAmount, item.UnitPriceCurrency)))
            .ToList();

        return Order.CreateExisting(
            entity.Id,
            items,
            entity.Currency,
            entity.Status,
            entity.ShippingAddress,
            entity.BillingAddress);
    }
}
