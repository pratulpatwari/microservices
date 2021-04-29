This is a microservices architecture which I am designing to learn all the concepts of micoservices and different techniques used to design a robust architecture with multiple services.

In order to keep this architecure as close to industry standards, I have created some accounts and assets in the database which I would be using to load the data in memory.

Steps to start the services:

1) Start spring-cloud-config-server to load all the configuration file from local/remote repository.
2) Start Discovery server to which all the services will subscribe to.
3) Start user-service which will have DB connection to pull all the user specific information.
4) Start account-service which will have DB connection to pull all the account specific information. The account service will make API calls to user-service to pull the list of user-account mapping
5) Start sprinc-cloud-api-gateway service which will act as the single entry point of all the services.

Technologies used in building this architecture:
Java 11,
Spring boot,
Spring security,
Eureka Discovery Server/Client,
Hystrix Circuit Breaker,
Zuul/Spring-cloud-api-gateway,
PostgreSql DB,
