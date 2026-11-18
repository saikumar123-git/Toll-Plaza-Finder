Toll Plaza Finder
A Java Spring Boot REST API that finds toll plazas along a route between
two Indian PIN codes. It uses Mappls for route information, MySQL to
store toll plaza locations, and Caffeine to cache repeated route
requests.
Features
REST endpoint to find toll plazas between a source and destination
PIN code.
Mappls route integration to obtain route distance and route
geometry.
Toll plaza lookup using stored latitude and longitude coordinates.
Distance-from-source calculation for toll plazas along the route.
MySQL persistence for toll plaza records.
CSV-based toll plaza data import with duplicate handling.
Caffeine caching for repeated requests.
Request validation and centralized error responses.
JUnit tests for service behaviour, including the no-toll-plaza
scenario.
Technology Stack
Java 21
Spring Boot 4.1.2
Maven
Spring Web / REST
Spring Data JPA and Hibernate
MySQL
Mappls Routing API
Caffeine Cache
Apache Commons CSV
JUnit 5 and Mockito
Prerequisites
Install the following before running the application:
JDK 21
Maven
MySQL Server
A Mappls API key with access to the routing API
Database Setup
Create the database in MySQL:
``` sql
CREATE DATABASE toll\\\_plaza\\\_db;
```
The application uses Spring Data JPA/Hibernate to create or update the
tables when configured with `spring.jpa.hibernate.ddl-auto=update`.
Configuration
The application reads sensitive values from environment variables. Do
not commit API keys or database passwords to GitHub.
Set these environment variables before starting the application:
Windows PowerShell
``` powershell
$env:DB\\\_PASSWORD="your\\\_mysql\\\_password"
$env:MAPPLS\\\_API\\\_KEY="your\\\_mappls\\\_api\\\_key"
```
The `src/main/resources/application.properties` file should contain
configuration similar to:
``` properties
spring.application.name=toll-plaza-finder

spring.datasource.url=jdbc:mysql://localhost:3306/toll\\\_plaza\\\_db
spring.datasource.username=root
spring.datasource.password=${DB\\\_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format\\\_sql=true

spring.cache.type=caffeine

mappls.api-key=${MAPPLS\\\_API\\\_KEY}
mappls.routing-url=https://route.mappls.com/route/direction

toll-plaza.route-corridor-km=2.0
```
Use your own local database credentials and Mappls API key. Keep the
values out of source control.
Toll Plaza CSV
Place the toll plaza CSV file here:
``` text
src/main/resources/data/toll\\\_plaza\\\_india.csv
```
The importer expects these column headers:
`toll\\\_name`
`latitude`
`longitude`
`geo\\\_state`
The importer validates the records and passes them to the import
service, which avoids inserting duplicate records already present in the
database.
Run the Application
From the project root directory (the directory containing `pom.xml`),
run:
``` bash
mvn clean test
mvn spring-boot:run
```
The application starts on:
``` text
http://localhost:8080
```
API Usage
Find toll plazas between two PIN codes
Endpoint
``` http
POST /api/v1/toll-plazas
```
Full URL
``` text
http://localhost:8080/api/v1/toll-plazas
```
Request body
``` json
{
  "sourcePincode": "560064",
  "destinationPincode": "411045"
}
```
Example success response
The exact toll plaza records and route distance depend on the Mappls
route and the records in the local database.
``` json
{
  "route": {
    "sourcePincode": "560064",
    "destinationPincode": "411045",
    "distanceInKm": 851.8224
  },
  "tollPlazas": \\\[
    {
      "name": "Example Toll Plaza",
      "latitude": 13.042116,
      "longitude": 77.475715,
      "distanceFromSourceInKm": 24.0426
    }
  ]
}
```
If no toll plazas are found along a valid route, the API returns the
route information with an empty array:
``` json
{
  "route": {
    "sourcePincode": "560064",
    "destinationPincode": "411045",
    "distanceInKm": 100.0
  },
  "tollPlazas": \\\[]
}
```
Validation and Error Handling
The API validates that:
Source and destination PIN codes are provided.
PIN codes contain six digits and begin with a non-zero digit.
Source and destination PIN codes are different.
Coordinates are configured for the requested PIN codes.
Errors are returned as JSON, for example:
``` json
{
  "error": "Invalid source pincode"
}
```
Typical validation errors use HTTP `400 Bad Request`. Unexpected server
errors use HTTP `500 Internal Server Error`.
Caching
Repeated requests for the same source and destination PIN-code pair are
cached using Spring Cache with Caffeine. This avoids calling the Mappls
routing API again for an identical request while the cached result is
available.
The reverse direction is a different route request and uses a different
cache key.
Tests
Run all tests:
``` bash
mvn clean test
```
A focused no-toll-plaza service test can be run with:
``` bash
mvn -Dtest=TollPlazaFinderServiceTest#shouldReturnEmptyListWhenNoTollPlazasFound test
```
Current Scope and Limitations
Coordinate lookup is currently configured for these PIN codes:
`560064`, `411045`, and `110001`. Other correctly formatted PIN
codes are rejected until coordinates are added.
Toll plaza results depend on the completeness and accuracy of the
supplied CSV data.
Toll plaza matching uses a configurable route corridor
(`toll-plaza.route-corridor-km`).
The Bengaluru-to-Pune route distance was compared with Mappls route
options during manual testing. The 80--90% toll-plaza matching
target has not been independently confirmed against a complete
reference toll-plaza list.
Project Structure
The project follows a layered Spring Boot structure:
``` text
src
├── main
│   ├── java
│   │   └── com/tollplaza/toll\\\_plaza\\\_finder
│   │       ├── client
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── entity
│   │       ├── exception
│   │       ├── repository
│   │       ├── service
│   │       └── util
│   └── resources
│       ├── application.properties
│       └── data
│           └── toll\\\_plaza\\\_india.csv
└── test
    └── java
```
Author
Add your name and contact details here before submission.
