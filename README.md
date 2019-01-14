# Rentals

An API as a product for any rental application. The tech stack is kotlin with Spring Boot2 using webflux

## Prerequisites

JVM : 1.8
kotlin : 1.2.71
Spring Boot : 2.1.1

### Database

Cassandra 3.11


## Starting the Project

Run the commands

### Start the Cassandra Database in the default port 9042

```
cassandra -f
```

### Start the Application in the default port 8080

```
./gradlew clean build bootrun
```

## Using the Application

### Make a post request to
 ```
 http://localhost:8080/asset
```

with the body
```
{
	"id": "38cf3d7c-f449-4cd4-85e1-ba61dd2db455",
	"name": "Art Of Computer Programming",
	"category": {"type" : "Books", "maker": "Donald Knuth", "size" : "1050 pages"}
}
```

on success the application returns 201 CREATED
