package com.ddd.restaurant.infrastructure.persistence.entity

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "menu")
class MenuJpaEntity(
        @Id val id: UUID,
        var name: String,
        var price: Double,
        @ManyToOne(fetch = FetchType.LAZY) // RestaurantJpaEntity와 Many-to-One 관계, Lazy 로딩
        @JoinColumn(name = "restaurant_id") // RestaurantJpaEntity의 PK를 FK로 사용
        var restaurant: RestaurantJpaEntity // RestaurantJpaEntity 참조
) {
    fun toDomain() =
            com.ddd.restaurant.domain.model.entity.Menu(
                    id = this.id,
                    name = this.name,
                    price = this.price
            )

    companion object {
        fun fromDomain(
                menu: com.ddd.restaurant.domain.model.entity.Menu,
                restaurant: Restaurant
        ): MenuJpaEntity { // Restaurant 정보를 파라미터로 받음
            return MenuJpaEntity(
                    id = menu.id,
                    name = menu.name,
                    price = menu.price,
                    restaurant =
                            RestaurantJpaEntity.fromDomain(
                                    restaurant
                            ) // RestaurantJpaEntity.fromDomain으로 변환
            )
        }
    }
}
