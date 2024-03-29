package com.example.eksamen.trip
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-08/scores/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/scores/RestApiTest.kt
import com.example.eksamen.trip.RestApi.Companion.LATEST
import com.example.eksamen.trip.service.TripRepository
import com.example.eksamen.trip.service.TripService
import com.example.eksamen.utils.response.PageDto
import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.annotation.PostConstruct


@ActiveProfiles("FakeData,test")
@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(Application::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(RestApiTest.Companion.Initializer::class)])
internal class RestApiTest {

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var tripRepository: TripRepository

    @Autowired
    private lateinit var tripService: TripService

    companion object {

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        /*
            Here, going to use an actual Redis instance started in Docker
         */

        @Container
        @JvmField
        val redis = KGenericContainer("redis:latest").withExposedPorts(6379)

        @Container
        @JvmField
        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

                TestPropertyValues
                        .of("spring.redis.host=${redis.containerIpAddress}",
                                "spring.redis.port=${redis.getMappedPort(6379)}",
                                "spring.rabbitmq.host=" + rabbitMQ.containerIpAddress,
                                "spring.rabbitmq.port=" + rabbitMQ.getMappedPort(5672))
                        .applyTo(configurableApplicationContext.environment);
            }
        }
    }

    @PostConstruct
    fun init(){
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    val page : Int = 5

    @Test
    fun testGetPage() {

        RestAssured.given().auth().basic("foo", "123").accept(ContentType.JSON)
                .get("/api/trips")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(page))
    }


    @Test
    fun testUpdateTrip(){
        val tripId = "foo"


        val id = RestAssured.given().auth().basic("admin", "admin").accept(ContentType.JSON)
                .put("/api/trips/$tripId")
                .then()
                .statusCode(201)

        assertTrue(tripRepository.existsById(tripId))

    }

    @Test
    fun testGetSingleTrip(){
        val tripId = "Oslo"

        tripService.registerNewTrip(tripId)

        val id = RestAssured.given().auth().basic("admin", "admin").accept(ContentType.JSON)
                .get("/api/trips/$tripId")
                .then()
                .statusCode(200)

    }


    @Test
    fun testPostTrip(){
        val tripId = "bar"

        val id = RestAssured.given().auth().basic("admin", "admin").accept(ContentType.JSON)
                .post("/api/trips/$tripId")
                .then()
                .statusCode(201)

        assertTrue(tripRepository.existsById(tripId))
    }
    


    @Test
    fun testDeleteMovie(){
        val tripId = "foo"

        val id = RestAssured.given().auth().basic("admin", "admin").accept(ContentType.JSON)
                .put("/api/trips/$tripId")
                .then()
                .statusCode(201)

        assertTrue(tripRepository.existsById(tripId))


        RestAssured.given().auth().basic("admin", "admin").delete("/api/trips/$tripId").then().statusCode(204)

        RestAssured.get().then().body("id", CoreMatchers.not(CoreMatchers.containsString(tripId)))

        RestAssured.given().auth().basic("admin", "admin").delete("/api/trips/$tripId").then().statusCode(404)


    }


    @Test
    fun testAllPages(){

        val read = mutableSetOf<String>()

        var page = RestAssured.given().accept(ContentType.JSON)
                .get("/api/trips")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(page))
                .extract().body().jsonPath().getObject("data",object: TypeRef<PageDto<Map<String, Object>>>(){})
        read.addAll(page.list.map { it["tripId"].toString()})

        checkOrder(page)

        while(page.next != null){
            page = RestAssured.given().accept(ContentType.JSON)
                    .get(page.next)
                    .then()
                    .statusCode(200)
                    .extract().body().jsonPath().getObject("data",object: TypeRef<PageDto<Map<String, Object>>>(){})
            read.addAll(page.list.map { it["tripId"].toString()})
            checkOrder(page)
        }

        val total = tripRepository.count().toInt()

        //recall that sets have unique elements
        Assertions.assertEquals(total, read.size)
    }


    private fun checkOrder(page: PageDto<Map<String, Object>>) {
        for (i in 0 until page.list.size - 1) {
            val aprice = page.list[i]["pricePerPerson"].toString().toInt()
            val bprice = page.list[i + 1]["pricePerPerson"].toString().toInt()
            val aid = page.list[i]["tripId"].toString()
            val bid = page.list[i + 1]["tripId"].toString()
            assertTrue(aprice >= bprice)
            if (aprice == bprice) {
                assertTrue(aid > bid)
            }
        }
    }


    @Test
    fun testGetCollection(){
        RestAssured.given().get("/api/trips/collection_$LATEST")
                .then()
                .statusCode(200)
                .body("data.trips.size", Matchers.greaterThan(2))
    }

}