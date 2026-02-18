using Microsoft.EntityFrameworkCore;
using OrderManagement.Domain.Entities;
using OrderManagement.Infrastructure.Adapters.Out.Persistence.Entities;

namespace OrderManagement.Infrastructure.Configuration;

public class OrderDbContext : DbContext
{
    public OrderDbContext(DbContextOptions<OrderDbContext> options) : base(options)
    {
    }

    public DbSet<OrderEntity> Orders { get; set; } = null!;
    public DbSet<OrderItemEntity> OrderItems { get; set; } = null!;

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<OrderEntity>(entity =>
        {
            entity.ToTable("Orders");
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Id).ValueGeneratedNever();
            entity.Property(e => e.Status).HasConversion<string>();
            entity.Property(e => e.Currency).IsRequired().HasMaxLength(10);
            
            // Value Objects stored as JSON or separate columns
            entity.OwnsOne(e => e.ShippingAddress, sa =>
            {
                sa.Property(a => a.Street).HasColumnName("ShippingStreet").HasMaxLength(200);
                sa.Property(a => a.City).HasColumnName("ShippingCity").HasMaxLength(100);
                sa.Property(a => a.State).HasColumnName("ShippingState").HasMaxLength(100);
                sa.Property(a => a.ZipCode).HasColumnName("ShippingZipCode").HasMaxLength(20);
                sa.Property(a => a.Country).HasColumnName("ShippingCountry").HasMaxLength(100);
            });

            entity.OwnsOne(e => e.BillingAddress, ba =>
            {
                ba.Property(a => a.Street).HasColumnName("BillingStreet").HasMaxLength(200);
                ba.Property(a => a.City).HasColumnName("BillingCity").HasMaxLength(100);
                ba.Property(a => a.State).HasColumnName("BillingState").HasMaxLength(100);
                ba.Property(a => a.ZipCode).HasColumnName("BillingZipCode").HasMaxLength(20);
                ba.Property(a => a.Country).HasColumnName("BillingCountry").HasMaxLength(100);
            });

            entity.HasMany(e => e.Items)
                .WithOne()
                .HasForeignKey(i => i.OrderId)
                .OnDelete(DeleteBehavior.Cascade);
        });

        modelBuilder.Entity<OrderItemEntity>(entity =>
        {
            entity.ToTable("OrderItems");
            entity.HasKey(e => new { e.OrderId, e.ProductId });
            entity.Property(e => e.ProductId).ValueGeneratedNever();
            entity.Property(e => e.ProductName).IsRequired().HasMaxLength(200);
            entity.Property(e => e.Quantity).IsRequired();
            entity.Property(e => e.UnitPriceAmount).IsRequired().HasColumnType("decimal(18,2)");
            entity.Property(e => e.UnitPriceCurrency).IsRequired().HasMaxLength(10);
        });
    }
}
