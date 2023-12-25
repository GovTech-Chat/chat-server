# chat-server

## Prerequisite

- JDK11+
- MySQL 8+

## Configure MySQL

- Create schema govtech
- Configure Spring boot project application.yml 
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/govtech
    username: root
    password: passw0rd4
```

## Starting the server

- Start MySQL
- Import chat-server into IDE
- Run as spring boot application
- Or open command prompt and goto the project root directory and run: mvn spring-boot:run
