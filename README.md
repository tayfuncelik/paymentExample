# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.4/maven-plugin/reference/html/#build-image)

```http://localhost:8080/v2/api-docs```
```http://localhost:8080/swagger-ui/index.html#/```

If I need to design under huge traffics, we shouldn't ask transaction id if it does exist or not. Also this query shouldn't effect our fetch queries.
So we can CQRS design for better impl.
