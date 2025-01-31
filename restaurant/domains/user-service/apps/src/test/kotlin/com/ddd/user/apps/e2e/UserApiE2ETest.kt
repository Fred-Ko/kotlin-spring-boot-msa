package com.ddd.user.apps.e2e

import com.ddd.user.apps.UserApplication
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
        classes = [UserApplication::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class UserApiE2ETest {

  @LocalServerPort private var port: Int = 0

  @BeforeEach
  fun setUp() {
    RestAssured.port = port
    RestAssured.baseURI = "http://localhost"
  }

  @Test
  fun `사용자 생성 후 조회 및 수정, 삭제 테스트`() {
    // 1. 사용자 생성
    val userId =
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(
                            """
                {
                    "email": "test@example.com",
                    "password": "password123",
                    "name": "Test User",
                    "phoneNumber": "010-1234-5678",
                    "address": {
                        "street": "Test Street",
                        "city": "Test City",
                        "state": "Test State",
                        "zipCode": "12345"
                    }
                }
            """.trimIndent()
                    )
                    .`when`()
                    .post("/api/v1/users")
                    .then()
                    .log()
                    .all()
                    .statusCode(200)
                    .body(notNullValue())
                    .extract()
                    .body()
                    .asString()

    // 2. 생성된 사용자 조회
    RestAssured.given()
            .`when`()
            .get("/api/v1/users/$userId")
            .then()
            .log()
            .all()
            .statusCode(200)
            .body("email", equalTo("test@example.com"))
            .body("name", equalTo("Test User"))

    // 3. 사용자 정보 수정
    RestAssured.given()
            .contentType(ContentType.JSON)
            .body(
                    """
                {
                    "email": "updated@example.com",
                    "password": "newpassword123",
                    "name": "Updated User",
                    "phoneNumber": "010-8765-4321",
                    "address": {
                        "street": "Updated Street",
                        "city": "Updated City",
                        "state": "Updated State",
                        "zipCode": "54321"
                    }
                }
            """.trimIndent()
            )
            .`when`()
            .put("/api/v1/users/$userId")
            .then()
            .log()
            .all()
            .statusCode(200)

    // 4. 수정된 사용자 정보 확인
    RestAssured.given()
            .`when`()
            .get("/api/v1/users/$userId")
            .then()
            .log()
            .all()
            .statusCode(200)
            .body("email", equalTo("updated@example.com"))
            .body("name", equalTo("Updated User"))

    // 5. 사용자 삭제
    RestAssured.given().`when`().delete("/api/v1/users/$userId").then().log().all().statusCode(200)

    // 6. 삭제된 사용자 조회 시 실패 확인
    RestAssured.given().`when`().get("/api/v1/users/$userId").then().log().all().statusCode(400)
  }

  @Test
  fun `사용자 목록 조회 테스트`() {
    // 1. 여러 사용자 생성
    repeat(3) { index ->
      RestAssured.given()
              .contentType(ContentType.JSON)
              .body(
                      """
                    {
                        "email": "user$index@example.com",
                        "password": "password123",
                        "name": "User $index",
                        "phoneNumber": "010-1234-567$index",
                        "address": {
                            "street": "Street $index",
                            "city": "City",
                            "state": "State",
                            "zipCode": "1234$index"
                        }
                    }
                """.trimIndent()
              )
              .`when`()
              .post("/api/v1/users")
              .then()
              .log()
              .all()
              .statusCode(200)
    }

    // 2. 사용자 목록 조회
    RestAssured.given()
            .`when`()
            .get("/api/v1/users?page=0&size=10")
            .then()
            .log()
            .all()
            .statusCode(200)
            .body("users.size()", equalTo(3))
  }
}
