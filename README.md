# app4share Project - Getting Started

The following will guide you through in setting up the project in your local machine to get started.  ( *Note: This guide is based on Windows machine *)

## 1. Pre-requisites 

Please have the following items installed/ configured before proceeding further:

* Apache Maven
* JDK 17
* Configure environment path for JAVA_HOME

## 2. Maven Clean Install

1) In your terminal, cd into the root folder (i.e `app4share`), run 
```
mvn clean install
```

## 3. Spring Boot Run (localhost)

i) In your terminal, cd into the root folder (i.e `app4share`), run 
```
mvn spring-boot:run
```

ii) Once it is started, it would start listening on port 9999 with contenxt path `/app4share`

iii) Open any internet browser and enter url http://localhost:9999/app4share

## 4. Default Credential

The default credential is:

Username: adminuser
Password: adminuser1$