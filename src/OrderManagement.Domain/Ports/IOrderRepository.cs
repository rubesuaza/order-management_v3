using OrderManagement.Domain.Entities;

namespace OrderManagement.Domain.Ports;

public interface IOrderRepository
{
    Task<Order?> GetByIdAsync(Guid orderId, CancellationToken cancellationToken = default);
    Task<IEnumerable<Order>> GetAllAsync(CancellationToken cancellationToken = default);
    Task<Order> SaveAsync(Order order, CancellationToken cancellationToken = default);
    Task<bool> DeleteAsync(Guid orderId, CancellationToken cancellationToken = default);
}
