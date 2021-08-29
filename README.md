# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.4/maven-plugin/reference/html/#build-image)


```http://localhost:8080/swagger-ui/index.html#/```

###Design Idea Under Huge Traffics  
If I need to design under huge traffics, we shouldn't ask transaction id if it does exist or not. Also this query shouldn't effect our fetch queries.
So we can CQRS design for better impl.

Another idea we can design with Async. If user don't need to wait his payment we could take his payment request and process it as asynchronous.
So he/she can check it later if it is completed or still pending. 
