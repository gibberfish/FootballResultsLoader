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

## Load Recent Results
This is the main action that the loader was designed to perform: To check for updates to results on a daily basis

```
mvn clean spring-boot:run -Drun.arguments="LOAD_RESULTS" -P jpa
```

## Integration Tests
To run the JPA integration test, run the following:

```
mvn clean spring-boot:run -Drun.arguments="INT_TEST" -P jpa
```

## Import data from JSON file
```
mvn clean spring-boot:run -Drun.arguments="IMPORT_FROM_JSON" -P jpa
```

## Load Season
```
mvn clean spring-boot:run -Drun.arguments="LOAD_SEASON" -P jpa
```

## Print Season Shapes
```
mvn clean spring-boot:run -Drun.arguments="PRINT_SEASON" -P jpa
```
