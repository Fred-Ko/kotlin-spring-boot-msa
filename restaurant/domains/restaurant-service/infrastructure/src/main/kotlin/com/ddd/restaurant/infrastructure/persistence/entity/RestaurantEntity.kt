package com.ddd.restaurant.infrastructure.persistence.entity

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.model.vo.*
import com.ddd.support.infrastructure.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "restaurants")
class RestaurantEntity(
        @Id @Column(nullable = false, columnDefinition = "UUID") override var id: UUID? = null,
        @Column(nullable = false) var name: String,
        @Embedded var address: RestaurantAddressEmbeddable,
        @ElementCollection
        @CollectionTable(
                name = "restaurant_menu_items",
                joinColumns = [JoinColumn(name = "restaurant_id")]
        )
        var menuItems: List<MenuItemEmbeddable>,
        @Enumerated(EnumType.STRING) @Column(nullable = false) var status: RestaurantStatus,
        @Embedded var operationHours: RestaurantOperationHoursEmbeddable,
        @Version var version: Long = 0
) : BaseEntity<UUID>() {

    fun toDomain(): Restaurant {
        return Restaurant(
                id = id!!,
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
        fun from(restaurant: Restaurant): RestaurantEntity {
            return RestaurantEntity(
                    id = restaurant.id,
                    name = restaurant.name,
                    address = RestaurantAddressEmbeddable.from(restaurant.address),
                    menuItems = restaurant.menuItems.map { MenuItemEmbeddable.from(it) },
                    status = restaurant.status,
                    operationHours =
                            RestaurantOperationHoursEmbeddable.from(restaurant.operationHours),
                    version = restaurant.version
            )
        }
    }
}

@Embeddable
class RestaurantAddressEmbeddable(
        @Column(nullable = false) var street: String,
        @Column(nullable = false) var city: String,
        @Column(nullable = false) var zipCode: String
) {
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
class MenuItemEmbeddable(
        @Column(name = "item_name", nullable = false) var name: String,
        @Column(name = "item_price", nullable = false) var price: BigDecimal,
        @Column(name = "item_quantity", nullable = false) var quantity: Int
) {
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
class RestaurantOperationHoursEmbeddable(
        @Column(nullable = false) var startTime: LocalTime,
        @Column(nullable = false) var endTime: LocalTime
) {
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
