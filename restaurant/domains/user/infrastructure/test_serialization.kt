import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.vo.UserId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.UUID

fun main() {
    val json =
        Json {
            prettyPrint = true
            isLenient = false
            ignoreUnknownKeys = false
            encodeDefaults = true
        }

    val event =
        UserEvent.Created(
            username = "testuser",
            email = "test@example.com",
            name = "Test User",
            phoneNumber = "123-456-7890",
            userType = "CUSTOMER",
            id = UserId.of(UUID.randomUUID()),
            occurredAt = Instant.now(),
        )

    println("kotlinx.serialization 출력:")
    println(json.encodeToString(event))
}
