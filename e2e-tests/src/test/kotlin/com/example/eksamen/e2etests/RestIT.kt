package com.example.eksamen.e2etests


import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit


@Disabled
@Testcontainers
class RestIT {


    companion object {

        init {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
            RestAssured.port = 80
        }

        class KDockerComposeContainer(id: String, path: File) : DockerComposeContainer<KDockerComposeContainer>(id, path)

        @Container
        @JvmField
        val env = KDockerComposeContainer("trip-booker", File("../docker-compose.yml"))
                .withExposedService("discovery", 8500,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(240)))
                .withLogConsumer("trip_0") { print("[TRIP_0] " + it.utf8String) }
                .withLogConsumer("trip_1") { print("[TRIP_1] " + it.utf8String) }
                .withLogConsumer("user-collections") { print("[USER_COLLECTIONS] " + it.utf8String) }
                //.withLogConsumer("scores") { print("[SCORES] " + it.utf8String) }
                .withLocalCompose(true)


        @BeforeAll
        @JvmStatic
        fun waitForServers() {

            Awaitility.await().atMost(240, TimeUnit.SECONDS)
                    .pollDelay(Duration.ofSeconds(20))
                    .pollInterval(Duration.ofSeconds(10))
                    .ignoreExceptions()
                    .until {

                        RestAssured.given().baseUri("http://${env.getServiceHost("discovery", 8500)}")
                                .port(env.getServicePort("discovery", 8500))
                                .get("/v1/agent/services")
                                .then()
                                .body("size()", CoreMatchers.equalTo(4))

                        true
                    }

        }
    }


    @Test
    fun testGetTripsCollection() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {
                    RestAssured.given().auth().basic("foo", "123").get("/api/trips/collection_v1_000")
                            .then()
                            .statusCode(200)
                            .body("data.trips.size", Matchers.greaterThan(10))
                    true
                }
    }

    @Test
    fun testGetTrips() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {
                    RestAssured.given().accept(ContentType.JSON)
                            .get("/api/trips")
                            .then()
                            .statusCode(200)
                            .body("data.list.size()", Matchers.greaterThan(0))
                    true
                }
    }


    @Test
    fun testCreateUser() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {

                    val id = "foo_testCreateUser_" + System.currentTimeMillis()

                    RestAssured.given().get("/api/user-collections/$id")
                            .then()
                            .statusCode(401)


                    val password = "123456"

                    val cookie = RestAssured.given().contentType(ContentType.JSON)
                            .body("""
                                {
                                    "userId": "$id",
                                    "password": "$password"
                                }
                            """.trimIndent())
                            .post("/api/auth/signUp")
                            .then()
                            .statusCode(201)
                            .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                            .extract().cookie("SESSION")

                    RestAssured.given().cookie("SESSION", cookie)
                            .put("/api/user-collections/$id")
//                            .then()
                    //could be 400 if AMQP already registered it
//                            .statusCode(201)

                    RestAssured.given().cookie("SESSION", cookie)
                            .get("/api/user-collections/$id")
                            .then()
                            .statusCode(200)

                    true
                }
    }


    @Test
    fun testUserCollectionAccessControl() {

        val alice = "alice_testUserCollectionAccessControl_" + System.currentTimeMillis()
        val eve = "eve_testUserCollectionAccessControl_" + System.currentTimeMillis()

        RestAssured.given().get("/api/user-collections/$alice").then().statusCode(401)
        RestAssured.given().put("/api/user-collections/$alice").then().statusCode(401)
        RestAssured.given().patch("/api/user-collections/$alice").then().statusCode(401)

        val cookie = RestAssured.given().contentType(ContentType.JSON)
                .body("""
                                {
                                    "userId": "$eve",
                                    "password": "123456"
                                }
                            """.trimIndent())
                .post("/api/auth/signUp")
                .then()
                .statusCode(201)
                .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                .extract().cookie("SESSION")



        RestAssured.given().cookie("SESSION", cookie)
                .get("/api/user-collections/$alice")
                .then()
                .statusCode(403)
    }


    @Test
    fun testAMQPCreateAndDeleteTrip() {
        Awaitility.await().atMost(60, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {

                    //ADMIN role username
                    val id = "admin"
                    val tripId = "tripToPostAndDeletee"

                    //Checking if access if not logged in
                    RestAssured.given().get("/api/user-collections/$id")
                            .then()
                            .statusCode(401)

                    //ADMIN role password
                    val password = "admin"

                    //Logging in to an existing user with ADMIN permissions
                    val cookie = RestAssured.given().contentType(ContentType.JSON)
                            .body("""
                                {
                                    "userId": "$id",
                                    "password": "$password"
                                }
                            """.trimIndent())
                            .post("/api/auth/login")
                            .then()
                            .statusCode(204)
                            .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                            .extract().cookie("SESSION")

                    //Checking if user was logged in
                    RestAssured.given().cookie("SESSION", cookie)
                            .get("/api/auth/user")
                            .then()
                            .statusCode(200)
                    //.body("roles", Matchers.contains("ROLE_ADMIN"))


                    RestAssured.given().cookie("SESSION", cookie)
                            .get("/api/trips/collection_v1_000")
                            .then()
                            .statusCode(200)

                    //Posting a trip with the ADMIN user
                    RestAssured.given().cookie("SESSION", cookie).contentType(ContentType.JSON)
                            .post("/api/trips/$tripId")
                            .then()
                            .statusCode(201)


                    RestAssured.given().cookie("SESSION", cookie)
                            .put("/api/user-collections/$id")

                    //checking the users collection
                    RestAssured.given().cookie("SESSION", cookie)
                            .get("/api/user-collections/$id")
                            .then()
                            .statusCode(200)

                    //the desired trip to purchase
                    val postData = ObjectMapper().createObjectNode()
                            .put("tripId", tripId)
                            .put("numberOfPeopleBooked", 2)
                            .put("numberOfTrips", 1)

                    //Purchasing the trip that was created (purchasing with ADMIN for simplicity)
                    RestAssured.given().cookie("SESSION", cookie).contentType(ContentType.JSON)
                            .body(postData)
                            .post("/api/user-collections/$id")
                            .then()
                            .statusCode(201)

                    //Deleting a trip with the ADMIN user
                    RestAssured.given().cookie("SESSION", cookie).contentType(ContentType.JSON)
                            .delete("/api/trips/$tripId")
                            .then()
                            .statusCode(204)

                    Awaitility.await().atMost(10, TimeUnit.SECONDS)
                            .pollInterval(Duration.ofSeconds(2))
                            .ignoreExceptions()
                            .until {
                                //Checking if booked trip has been marked as CANCELED, since its been deleted from /api/trips
                                RestAssured.given().cookie("SESSION", cookie)
                                        .get("/api/user-collections/$id")
                                        .then()
                                        .statusCode(200)
                                        .body(CoreMatchers.containsString("CANCELED"))

                                true
                            }


                    true
                }
    }


    @Test
    fun testAMQPSignUp() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {

                    val id = "foo_testCreateUser_" + System.currentTimeMillis()

                    RestAssured.given().get("/api/auth/user")
                            .then()
                            .statusCode(401)

                    RestAssured.given().put("/api/user-collections/$id")
                            .then()
                            .statusCode(401)



                    val password = "123456"

                    val cookie = RestAssured.given().contentType(ContentType.JSON)
                            .body("""
                                {
                                    "userId": "$id",
                                    "password": "$password"
                                }
                            """.trimIndent())
                            .post("/api/auth/signUp")
                            .then()
                            .statusCode(201)
                            .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                            .extract().cookie("SESSION")

                    RestAssured.given().cookie("SESSION", cookie)
                            .get("/api/auth/user")
                            .then()
                            .statusCode(200)

                    Awaitility.await().atMost(10, TimeUnit.SECONDS)
                            .pollInterval(Duration.ofSeconds(2))
                            .ignoreExceptions()
                            .until {
                                RestAssured.given().cookie("SESSION", cookie)
                                        .get("/api/user-collections/$id")
                                        .then()
                                        .statusCode(200)


                                true
                            }

                    true
                }
    }


}










