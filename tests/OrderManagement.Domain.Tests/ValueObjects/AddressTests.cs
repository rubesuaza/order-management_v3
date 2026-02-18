using FluentAssertions;
using OrderManagement.Domain.ValueObjects;

namespace OrderManagement.Domain.Tests.ValueObjects;

public class AddressTests
{
    [Fact]
    public void Create_WithValidProperties_ShouldCreateAddress()
    {
        // Arrange & Act
        var address = new Address(
            "123 Main St",
            "New York",
            "NY",
            "10001",
            "USA"
        );

        // Assert
        address.Street.Should().Be("123 Main St");
        address.City.Should().Be("New York");
        address.State.Should().Be("NY");
        address.ZipCode.Should().Be("10001");
        address.Country.Should().Be("USA");
    }

    [Fact]
    public void Equals_WithSameProperties_ShouldReturnTrue()
    {
        // Arrange
        var address1 = new Address(
            "123 Main St",
            "New York",
            "NY",
            "10001",
            "USA"
        );
        var address2 = new Address(
            "123 Main St",
            "New York",
            "NY",
            "10001",
            "USA"
        );

        // Act & Assert
        address1.Should().Be(address2);
        (address1 == address2).Should().BeTrue();
    }

    [Fact]
    public void Equals_WithDifferentProperties_ShouldReturnFalse()
    {
        // Arrange
        var address1 = new Address(
            "123 Main St",
            "New York",
            "NY",
            "10001",
            "USA"
        );
        var address2 = new Address(
            "456 Oak Ave",
            "Los Angeles",
            "CA",
            "90001",
            "USA"
        );

        // Act & Assert
        address1.Should().NotBe(address2);
        (address1 != address2).Should().BeTrue();
    }
}
