using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using OrderManagement.Domain.Ports;
using OrderManagement.Infrastructure.Adapters.Out.ExternalServices;
using OrderManagement.Infrastructure.Adapters.Out.Persistence;

namespace OrderManagement.Infrastructure.Configuration;

public static class DependencyInjection
{
    public static IServiceCollection AddInfrastructure(
        this IServiceCollection services,
        IConfiguration configuration)
    {
        // Database configuration
        var connectionString = configuration.GetConnectionString("DefaultConnection") 
            ?? "Data Source=order_management.db";

        services.AddDbContext<OrderDbContext>(options =>
        {
            options.UseSqlServer(connectionString);
        });

        // Register repositories (Output Adapters)
        services.AddScoped<IOrderRepository, OrderRepository>();

        // Register external service adapters (Output Adapters)
        services.AddScoped<IPaymentGateway, PaymentGatewayAdapter>();

        return services;
    }

    public static IServiceCollection AddInfrastructureInMemory(
        this IServiceCollection services)
    {
        // In-memory database for testing
        services.AddDbContext<OrderDbContext>(options =>
        {
            options.UseInMemoryDatabase("OrderManagementTestDb");
        });

        // Register repositories (Output Adapters)
        services.AddScoped<IOrderRepository, OrderRepository>();

        // Register external service adapters (Output Adapters)
        services.AddScoped<IPaymentGateway, PaymentGatewayAdapter>();

        return services;
    }
}
