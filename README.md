**Lunch Decider – Spring Boot Application:**

A backend RESTful application that allows a group of users to collaboratively decide where to have lunch.
Users can create a session, join it, submit restaurant options, and randomly select a restaurant when the session ends.
This project is implemented using Spring Boot, Spring Data JPA, Spring Batch, Flyway, and Swagger OpenAPI.

**Prerequisites:**
Java 17(JDK 17), Maven 3.9+, Git and postman for testing API (optional).
**Verify installations:**
java -version
mvn -v
git --version
Note: Install mvn if not found. Add MAVEN_HOME to system variable in enviornment variables and ensure MAVEN_HOME/bin is added to your PATH, then restart the terminal.

**Clone the repository:**
git clone https://github.com/SindujaRaghavan/lunch-decider.git
cd lunch-decider

**Start the application:**
mvn clean spring-boot:run

**The application starts on:**
Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2

**Local Database Setup (H2):**
This project uses an in-memory H2 database for local development.

H2 Console:

URL: http://localhost:8080/h2
JDBC URL: jdbc:h2:mem:lunchdb
Username: sa
Password: (empty)

**Schema & Data Initialization:**
Flyway automatically creates all database tables on startup.
Spring Batch loads predefined users from: src/main/resources/predefined-users.csv
Preloaded users:alice,bob,charlie,david.(Only these users are allowed to interact with the application).

**API Documentation:**
All APIs are documented and testable using Swagger OpenAPI:
Swagger UI:http://localhost:8080/swagger-ui.html

**Available Endpoints:**
Create Session: 
POST /api/sessions
{"createdByUsername": "alice"}

Join Session:
POST /api/sessions/{code}/join
{ "username": "bob"}

Submit Restaurant:
POST /api/sessions/{code}/restaurants
{  "username": "bob",
  "restaurantName": "Din Tai Fung"
}
{  "username": "alice",
  "restaurantName": "Shui Tai"
}

Get Session:
GET /api/sessions/{code}
Returns:
session status
participants
submitted restaurants
picked restaurant (if session ended)

End Session (Creator Only):
POST /api/sessions/{code}/end?by=alice
At session end:
a restaurant is randomly selected
session status becomes ENDED
the picked restaurant is visible to all participants


