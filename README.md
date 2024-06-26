# Simple products

This is a simple REST api for simple product management.

Application was developed using tools such as:

- Java 17
- Spring Boot 3
- PostgreSQL 
- IntelliJ IDEA
- Liquibase

### How to run this project

Prerequisites to run the project:

- JRE 17
- PostgreSQL database server

Steps to run:

1. Clone this repository
2. Position yourself in the root folder of the project
3. Edit the _application.yml_ file for your needs:

```yaml
url: jdbc:postgresql://<postgres_db_host>:<postgres_db_port>/<postgres_db_name>
username: postgres_db_user
password: postgres_db_password
```

4. Create a database with the name '<postgres_db_name>' you specified in the _.yml_ file
5. run command:
   
```bash
mvn clean install
``` 
or if you don't have maven set up in your system:
```bash
.\mvnw clean install
```
6. Run the _.jar_ file you just installed

If you have any IDE installed, e.g. IntelliJ IDEA, you can open the project with said IDE and just run the _SimpleProductsApplication_.

If you have docker installed:

1. Position yourself in the root folder of the project
3. Run the command ```docker compose up --build``` or ```docker-compose up --build```

The REST service will be available on port 8080 unless you specified otherwise.

You can visit the next link to see the swagger documentation and available endpoints:

http://localhost:8080/simple-products/api/swagger-ui.html

You can also import the Postman collection from the _postman-collections_ folder inside the root folder of the project and test the endpoints.

