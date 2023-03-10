# Account Service and Transaction Service Microservices


This project consists of two microservices developed using Spring Boot: Account Service and Transaction Service. The purpose of this project is to provide a simple example of how to implement microservices architecture using Spring Boot. And also show some level of distributed transactions.

# Technologies Used
Java 11
Spring Boot 2.7.9
Spring cloud tools
Maven 3.8.2
MySQL 8.0


**Github** repository location: https://github.com/aabibi/account_transactions

# How to deploy:
This project is using dockers. There is a docker compose file that you can run ( I tested this using docker desktop so assumptions is you have an environment that can run dockers images ).
In intellij or any IDE and create a run configuration and point to the docker-compose.yaml in the root directory.

I added a few extra features that I will list below before talking about the microservices.

# Discovery server
I added a discovery server ( since we have at least 2 services ) which will run on  http://localhost:8761/


 user name: eureka and password: password

# Zipkin
For tracing I also installed zipkin. Very useful to look the at the flow of the requests as well as exceptions.
http://localhost:9411/zipkin


## Account Service
The Account microservice is responsible for managing user accounts.
It provides REST APIs for creating, updating, and retrieving account information. The Account Service uses MySQL as its data store.
I also added a column to store the account balance to make this more interesting. I also added a few extra end points. Note: this service will run
on port 8081.



**Account service Swagger URL**
http://localhost:8081/swagger-ui.html

**Endpoints**

POST /accounts: creates a new account.

POST /accounts/update_balance: updates an existing account  

GET /accounts/{accountId}: retrieves an account by ID




## Transaction Service
The Transaction Service microservice is responsible for managing transactions between accounts. It provides REST APIs for creating, updating, and retrieving transactions. The Transaction Service uses MySQL as its data store. Note: this service will run
on port 8082.

**Account service Swagger URL**
http://localhost:8082/swagger-ui.html

**Endpoints**

POST /transactions: creates a new transaction

GET /transactions/{transactionId}: retrieves a transaction by ID


## Dependencies and application flow

The Account Service and Transaction Service microservices are designed to work together. The Transaction Service depends on the Account Service to retrieve account information for transactions.
Once accounts are created , you can make transactions based on the requirement and transactions type. When a transaction happens, the transaction service act like the coordinator and ask the account service to verify
the request. If the request pass all the validation, only then do we create a transaction.

**Validations**

Operation/Transaction Type
1-PURCHASE
2-INSTALLMENT PURCHASE
3-WITHDRAWAL
4-PAYMENT

- The account must exist before trying to do any transactions or you will get an error ( account not found etc..)
- Only valid transaction type above will be handled. Anything not in this range will result in a error.
- PURCHASE, INSTALLMENT PURCHASE and WITHDRAWAL must have a negative amount or it will result in an error.
- If the Account owner balance does not have sufficient funds to handle the transactions, an error will be shown.


## Future Improvements
This project is meant to be a simple example of how to implement microservices architecture using Spring Boot. Some possible future improvements to this project could include:

- Adding authentication and authorization to the microservices

- Adding more functionality to the microservices, such as support for different types of transactions
- Due to time constraints, I did not use any queueing mechanism but this could be an improvement in terms of scaling. We could have a third service that sit in between the account and transaction service and handle the coordination between the two via messages.


## Potential issues while running/deploying in dockers. 

1) During testing i noticed some error messages were not being shown in swagger, maybe something in dockers. 
If you encounter this issue,I would suggest looking at zipkin f as they all show up there.

2) Sometimes in Docker desktop, the Account or Transaction service may not start correctly. I think this is  due 
to the database not being up on time. To fix this just restart these services in docker desktop and it should work. I did not 
have time to investigate further.

## Conclusion
Feel free to reach out to me if you have any issue running this.
