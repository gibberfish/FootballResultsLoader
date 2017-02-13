# FootballResultsLoader Application

## Choose the Repository Implementation
There are 2 database implementations that will be supported by the loader applications:

* MySQL via JPA using an EclipseLink implementation
* Couchbase 

At this point, only the JPA implementation is wired-in.

To build with and use JPA use the jpa build profile:

```
mvn clean spring-boot:run -Drun.arguments="<command to run>" -P jpa
```

When Couchbase has been integrated, use the couchbase build profile:

```
mvn clean spring-boot:run -Drun.arguments="<command to run>" -P couchbase
```

## Integration Tests
To run the JPA integration test, run the following:

```
mvn clean spring-boot:run -Drun.arguments="INT_TEST"
```
