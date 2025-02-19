package com.ddd.restaurant.infrastructure.persistence.entity

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.model.vo.*
import com.ddd.support.entity.BaseJpaEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "restaurants")
class RestaurantJpaEntity
private constructor(
        @Id @Column(nullable = false, columnDefinition = "UUID") override val id: UUID,
        @Column(nullable = false) val name: String,
        @Embedded val address: RestaurantAddressEmbeddable,
        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(
                name = "restaurant_menu_items",
                joinColumns = [JoinColumn(name = "restaurant_id")]
        )
        val menuItems: MutableList<MenuItemEmbeddable>,
        @Enumerated(EnumType.STRING) @Column(nullable = false) val status: RestaurantStatus,
        @Embedded val operationHours: RestaurantOperationHoursEmbeddable,
        @Version override var version: Long = 0
) : BaseJpaEntity<UUID>() {

    fun toDomain(): Restaurant {
        return Restaurant.create(
                id = id,
                createdAt = createdAt,
                name = name,
                address = address.toDomain(),
                menuItems = menuItems.map { it.toDomain() },
                status = status,
                operationHours = operationHours.toDomain(),
                updatedAt = updatedAt,
                version = version
        )
    }

    companion object {
        fun from(restaurant: Restaurant): RestaurantJpaEntity {
            return RestaurantJpaEntity(
                    id = restaurant.id,
                    name = restaurant.name,
                    address = RestaurantAddressEmbeddable.from(restaurant.address),
                    menuItems =
                            restaurant
                                    .menuItems
                                    .map { MenuItemEmbeddable.from(it) }
                                    .toMutableList(),
                    status = restaurant.status,
                    operationHours =
                            RestaurantOperationHoursEmbeddable.from(restaurant.operationHours),
                    version = restaurant.version
            )
        }
    }
}

@Embeddable
class RestaurantAddressEmbeddable
private constructor(
        @Column(nullable = false) val street: String,
        @Column(nullable = false) val city: String,
        @Column(nullable = false) val zipCode: String
) {
    init {
        require(street.isNotBlank()) { "Street cannot be blank" }
        require(city.isNotBlank()) { "City cannot be blank" }
        require(zipCode.isNotBlank()) { "Zip code cannot be blank" }
    }
    fun toDomain(): RestaurantAddress {
        return RestaurantAddress(street, city, zipCode)
    }

    companion object {
        fun from(address: RestaurantAddress): RestaurantAddressEmbeddable {
            return RestaurantAddressEmbeddable(
                    street = address.street,
                    city = address.city,
                    zipCode = address.zipCode
            )
        }
    }
}

@Embeddable
class MenuItemEmbeddable
private constructor(
        @Column(name = "item_name", nullable = false) val name: String,
        @Column(name = "item_price", nullable = false) val price: BigDecimal,
        @Column(name = "item_quantity", nullable = false) val quantity: Int
) {
    init {
        require(name.isNotBlank()) { "Menu item name cannot be blank" }
        require(price >= BigDecimal.ZERO) { "Price must be non-negative" }
        require(quantity >= 0) { "Quantity must be non-negative" }
    }
    fun toDomain(): MenuItem {
        return MenuItem(name = name, price = price, quantity = quantity)
    }

    companion object {
        fun from(menuItem: MenuItem): MenuItemEmbeddable {
            return MenuItemEmbeddable(
                    name = menuItem.name,
                    price = menuItem.price,
                    quantity = menuItem.quantity
            )
        }
    }
}

@Embeddable
class RestaurantOperationHoursEmbeddable
private constructor(
        @Column(nullable = false) val startTime: LocalTime,
        @Column(nullable = false) val endTime: LocalTime
) {
    init {
        require(startTime.isBefore(endTime)) { "Start time must be before end time" }
    }
    fun toDomain(): RestaurantOperationHours {
        return RestaurantOperationHours(startTime, endTime)
    }

    companion object {
        fun from(operationHours: RestaurantOperationHours): RestaurantOperationHoursEmbeddable {
            return RestaurantOperationHoursEmbeddable(
                    startTime = operationHours.startTime,
                    endTime = operationHours.endTime
            )
        }
    }
}
