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

## Run any of the commands below on the test database
By default, running the commands below will be actioned on the 'live' database (football).
However, if you want to run any of the commands below against the 'test' database (football_test), then include a
command-line argument to use the 'test' spring profile:

e.g.

```
mvn clean spring-boot:run -Drun.arguments="PRINT_SEASON,--spring.profiles.active=test" -P jpa
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

## Add missing fixtures for a season, based upon the season division team shape
```
mvn clean spring-boot:run -Drun.arguments="ADD_MISSING_FIXTURES,2016" -P jpa
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
