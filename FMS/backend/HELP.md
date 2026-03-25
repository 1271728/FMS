# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.13/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.13/maven-plugin/reference/html/#build-image)
* [MyBatis Framework](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.13/reference/htmlsingle/#web)

### Guides

The following guides illustrate how to use some features concretely:

* [MyBatis Quick Start](https://github.com/mybatis/spring-boot-starter/wiki/Quick-Start)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)


### Environment Profiles (dev/prod)

This project now separates shared and environment-specific configuration:

- `application.properties`: shared/base settings (profile switch, MyBatis, Sa-Token)
- `application-dev.properties`: local development defaults
- `application-prod.properties`: production settings (expects env vars)

Recommended environment variables:

- `SPRING_PROFILES_ACTIVE` (`dev` or `prod`)
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `SA_TOKEN_LOG`
- `MYBATIS_LOG_IMPL`
