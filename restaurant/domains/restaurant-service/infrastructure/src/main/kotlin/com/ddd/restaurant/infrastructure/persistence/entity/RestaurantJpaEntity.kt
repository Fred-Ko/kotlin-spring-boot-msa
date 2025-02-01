package com.ddd.restaurant.infrastructure.persistence.entity

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.model.vo.Location
import com.ddd.restaurant.domain.model.vo.OperatingHours
import com.ddd.restaurant.domain.model.vo.RestaurantName
import com.ddd.restaurant.domain.model.vo.RestaurantStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "restaurant")
class RestaurantJpaEntity(
    @Id
    val id: UUID,
    var name: String,
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "restaurant_menu", joinColumns = [JoinColumn(name = "restaurant_id")])
    val menus: MutableList<MenuJpaEntity> = mutableListOf(),
    var startTime: LocalTime,
    var endTime: LocalTime,
    @Enumerated(EnumType.STRING)
    var status: RestaurantStatus,
    var latitude: Double,
    var longitude: Double,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    @Version
    var version: Long? = null
) {
    fun toDomain(): Restaurant {
        return Restaurant(
            id = this.id,
            name = RestaurantName(this.name),
            menus = this.menus.map { it.toDomain() }.toMutableList(),
            operatingHours = OperatingHours(this.startTime, this.endTime),
            status = this.status,
            location = Location(this.latitude, this.longitude),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            version = this.version ?: 0
        )
    }

    companion object {
        fun fromDomain(restaurant: Restaurant): RestaurantJpaEntity {
            return RestaurantJpaEntity(
                id = restaurant.id,
                name = restaurant.name.value,
                menus = restaurant.menus.map { MenuJpaEntity.fromDomain(it) }.toMutableList(),
                startTime = restaurant.operatingHours.startTime,
                endTime = restaurant.operatingHours.endTime,
                status = restaurant.status,
                latitude = restaurant.location.latitude,
                longitude = restaurant.location.longitude,
                createdAt = restaurant.createdAt,
                updatedAt = restaurant.updatedAt,
            )
        }
    }
}


@Embeddable
class MenuJpaEntity(
    val id: UUID,
    val name: String,
    val price: Double
) {
    fun toDomain() = com.ddd.restaurant.domain.model.entity.Menu(
        id = this.id,
        name = this.name,
        price = this.price
    )

    companion object {
        fun fromDomain(menu: com.ddd.restaurant.domain.model.entity.Menu) = MenuJpaEntity(
            id = menu.id,
            name = menu.name,
            price = menu.price
        )
    }
}
