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

---

## 环境配置说明（dev/prod）

后端配置已拆分为“基础配置 + 环境配置”：

- `application.properties`：环境无关的公共配置（端口、MyBatis 基础项、Sa-Token 基础项）
- `application-dev.properties`：本地开发默认配置
- `application-prod.properties`：生产配置（必须通过环境变量注入敏感信息）

### 默认行为

- 未设置 `SPRING_PROFILES_ACTIVE` 时，默认使用 `dev`。
- 生产环境请显式设置：`SPRING_PROFILES_ACTIVE=prod`。

### 推荐环境变量

- `SPRING_PROFILES_ACTIVE`：`dev` / `prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
- `SA_TOKEN_LOG`
- `MYBATIS_LOG_IMPL`

### 启动示例

开发环境（使用默认 dev）：

```bash
mvn spring-boot:run
```

生产环境（示例）：

```bash
SPRING_PROFILES_ACTIVE=prod \
DB_URL='jdbc:mysql://db-host:3306/uni_research_fund?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai' \
DB_USERNAME='app_user' \
DB_PASSWORD='***' \
SERVER_PORT=8080 \
mvn spring-boot:run
```

## Quality Gate (建议在提交前执行)

```bash
# 后端单元测试
mvn test

# 后端打包（含编译检查）
mvn -DskipTests package
```
