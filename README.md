

# PG6102 - Enterprise Programmering 2 - Exam


## About
This project is the exam of PG6102 Enterprise Programming 2. It is a microservice application using Docker-Compose. Among the other technologies used are: SpringBoot, Kotlin, PostgreSQL, Redis and AMQP. 


## How to run: 
1. Install application:
    * `mvn clean install` (to run with unit tests, E2E-tests will also run)
    * `mvn clean install -DskipTests` (to run without tests)
2. Run `docker-compose build` to build docker-compose
3. Run `docker-compose up` to start docker-compose
* Run with coverage for testcoverage on each module

### E2E-tests
To run E2E tests you can use `mvn clean verify` or run the file RestIT manually.
Also added AMQP E2E-test. The name of this test is: `testAMQPCreateAndDeleteTrip`. If you want to disable E2E-tests running when running mvn, uncomment the @Disabled annotation in RestIT test-class.

### Individual modules
To run seperat modules in the microservice application, you can run LocalApplicationRunner in modules: trip, user-collection, auth.
Visit SwaggerAPI at with URL: http://localhost:8080/swagger-ui/. 
NOTE: the URL in the assignment description says to use this URL: http://localhost:8080/swagger-ui.html. But it does not work. Use the one mentioned initially.

### Shortcomings
I did not have time to complete the T4 and R5 requirements. 

##Completed Requirements:
Did manage to do R1, R2, R3, R4 & T1, T2, T3
#### R1:
- [x] Write a new REST API using SpringBoot and Kotlin.
- [x] Have AT LEAST one endpoint per main HTTP method, i.e., GET, POST, PUT, PATCH and DELETE.
- [x] Each endpoint MUST use Wrapped Responses.
- [x] Endpoints returning collections of data MUST use Keyset Pagination
- [x] MUST provide OpenAPI/Swagger documentation for all your endpoints.
- [x] Write AT LEAST one test with RestAssured per each endpoint.
- [x] Add enough tests (unit or integration, it is up to you) such that, when they are run from IntelliJ,
      they MUST achieve AT LEAST a 70% code coverage. (trip = 81%, 90%), (user-collections = 81%, 78%), (auth = 86%, 91%)
- [x] If the service communicates with another REST API, you need to use WireMock in the integration
      tests to mock it, and use as well a Circuit Breaker.
- [x] You MUST provide a LocalApplicationRunner in the test folder which is able to run the REST API
      independently from the whole microservice. http://localhost:8080/swagger-ui/
- [x] In “production” mode, the API MUST be configured to connect to a PostgreSQL database
- [x] You MUST use Flyway for migration handling
- [x] Configure Maven to build a self-executable uber/fat jar for the service.
       
       
#### R2:
- [x] Your microservices MUST be accessible only from a single entry point, i.e., an API Gateway.
- [x] Your whole application must be started via Docker-Compose. The API Gateway MUST be the only
      service with an exposed port.
- [x] You MUST have at least one REST API service that is started more than once (i.e., more than one
      instance in the Docker-Compose file), and load-balanced (use service-discovery, e.g., Consul).
- [x] In Docker-Compose, MUST use real databases (e.g., PostgreSQL) instead of embedded ones (e.g.,
      H2) directly in the services.
- [x] You MUST have at least 1 end-to-end test for each REST API using Docker-Compose starting the
      whole microservice.
      
      
#### R3:
- [x] You MUST have security mechanisms in place to protect your REST APIs. (WebSecurityConfig in trip, user-collections and auth)
- [x] You MUST set up a distributed session-based authentication with Redis, and you MUST setup an API for login/logout/create of users.


#### R4:
- [x] You MUST have at least one communication relying on AMQP between two different web services.
- [x] You MUST have at least one E2E test in which the correct behavior of AMQP is verified, i.e., a call to a service X which leads to an update to service Y via AMQP, and then in the test check that such update was correctly executed
- [x] The name of this test MUST be specified in the readme.md file = `testAMQPCreateAndDeleteTrip` & `testAMQPSignUp`

### Application topic requirements: 
#### T1:
- [x] REST API to handle trip details: e.g., place, duration and cost per person.
- [x] When the API starts, it must have some already existing data.
- [x] All requirements in R1 must be satisfied.


#### T2:
- [x] REST API to handle booking of trips.
- [x] When booking a trip, need to specify for how many people.
- [x] Only a logged-in user can book trips. Make sure users cannot alter the reservations of other users.
- [x] Users should be able to cancel their reservations, and alter them (e.g., number of people).
- [x] All requirements in R1 must be satisfied for this API.
- [x] Only admins should be able to modifications in the API of T1.

#### T3:
- [x] Every time a new trip is added or deleted in the API for T1, this latter should do a broadcast with AMQP. Then, the API in T2 should listen to such events. Reservations for deleted trips should be marked as “cancelled”.




