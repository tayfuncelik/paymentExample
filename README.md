# Getting Started

### Reference Documentation
I designed this way
- Player which has an Account 
- Every Transaction has its own unique transaction Id and request has playerId and transaction details.
- I added default Player values while application running up
- You can send request debit/ credit 
    There are few business rules.
- In  "asyn_way" branch you can find asynchronous way of it.


```mvn clean install```
```mvn spring-boot:run```

```http://localhost:8080/swagger-ui/index.html#/```

### Design Idea Under Huge Traffics  
We shouldn't get transactionId from DB.We can create an layer which is CQRS. 
Or we can implement Redis and put all unique Id's in to. Later on we can check it inside.
We can update later.

Another idea we can design asynchronous. 
If we don't make user wait so long. We can take his/her payment request and I would process behind. I would warn user.
They can check through transaction history.
So he/she can check it later if it is completed or still pending. 

-Profile can be added 
