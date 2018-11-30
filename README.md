# Dubbo Spring Boot Project 

[![Build Status](https://travis-ci.org/apache/incubator-dubbo-spring-boot-project.svg?branch=master)](https://travis-ci.org/apache/incubator-dubbo-spring-boot-project) 
[![codecov](https://codecov.io/gh/apache/incubator-dubbo-spring-boot-project/branch/master/graph/badge.svg)](https://codecov.io/gh/apache/incubator-dubbo-spring-boot-project)
[![Gitter](https://badges.gitter.im/alibaba/dubbo.svg)](https://gitter.im/alibaba/dubbo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
![license](https://img.shields.io/github/license/apache/incubator-dubbo-spring-boot-project.svg)
![maven](https://img.shields.io/maven-central/v/com.alibaba.boot/dubbo-spring-boot-starter.svg)

[Apache Dubbo(incubating)](https://github.com/apache/incubator-dubbo) Spring Boot Project makes it easy to create [Spring Boot](https://github.com/spring-projects/spring-boot/) application using Dubbo as RPC Framework. What's more, it aslo provides 

* [auto-configure features](dubbo-spring-boot-autoconfigure) (e.g., annotation-driven, auto configuration, externalized configuration).
* [production-ready features](dubbo-spring-boot-actuator) (e.g., security, health checks, externalized configuration).

> Apache Dubbo(incubating) is a high-performance, java based [RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) framework open-sourced by Alibaba. As in many RPC systems, dubbo is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types. On the server side, the server implements this interface and runs a dubbo server to handle client calls. On the client side, the client has a stub that provides the same methods as the server.

## [中文说明](README_CN.md)


## Released version

You can introduce the latest `dubbo-spring-boot-starter` to your project by adding the following dependency to your pom.xml
```xml
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>0.2.0</version>
</dependency>
```

If your project failed to resolve the dependency, try to add the following repository:
```xml
<repositories>
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```


## Developing Versions

For now, `dubbo-spring-boot-starter` will separate two versions for Spring Boot 2.x and 1.x once release : 

* [`0.2.x`](https://github.com/apache/incubator-dubbo-spring-boot-project) is a main stream release version for Spring Boot 2.x

* [`0.1.x`](https://github.com/apache/incubator-dubbo-spring-boot-project/tree/0.1.x) is a legacy version for maintaining Spring Boot 1.x


### Build from Source

If you'd like to attempt to experience latest features, you also can build from source as follow:

1. Maven install current project in your local repository.
> Maven install = `mvn install`


### Dependencies

| versions | Java  | Spring Boot | Dubbo      |
| -------- | ----- | ----------- | ---------- |
| `0.2.0`  | 1.8+ | `2.0.x` | `2.6.2` + |
| `0.1.1`  | 1.7+ | `1.5.x` | `2.6.2` + |



## Getting Started

If you don't know about Dubbo, please take a few minutes to learn http://dubbo.apache.org/. After that  you could dive deep into dubbo [user guide](http://dubbo.apache.org/books/dubbo-user-book-en/).

Usually, There are two usage scenarios for Dubbo applications, one is Dubbo service(s) provider, another is Dubbo service(s) consumer, thus let's get a quick start on them.

First of all, we suppose an interface as Dubbo RPC API that  a service provider exports and a service client consumes: 

```java
public interface DemoService {

    String sayHello(String name);

}
```



### Dubbo service(s) provider

Service Provider implements `DemoService`:

```java
@Service(
        version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class DefaultDemoService implements DemoService {

    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }

}
```



then, provides a bootstrap class: 

```java
@SpringBootApplication
public class DubboProviderDemo {

    public static void main(String[] args) {

        SpringApplication.run(DubboProviderDemo.class,args);

    }

}
```



last, configures `application.properties`:

```properties
# Spring boot application
spring.application.name = dubbo-provider-demo
server.port = 9090
management.port = 9091

# Base packages to scan Dubbo Components (e.g., @Service, @Reference)
dubbo.scan.basePackages  = com.alibaba.boot.dubbo.demo.provider.service

# Dubbo Config properties
## ApplicationConfig Bean
dubbo.application.id = dubbo-provider-demo
dubbo.application.name = dubbo-provider-demo

## ProtocolConfig Bean
dubbo.protocol.id = dubbo
dubbo.protocol.name = dubbo
dubbo.protocol.port = 12345

## RegistryConfig Bean
dubbo.registry.id = my-registry
dubbo.registry.address = N/A
```



`DefaultDemoService`'s placeholders( `${dubbo.application.id}`, `${dubbo.protocol.id}`, `${dubbo.registry.id}` ) sources from `application.properties`.



More details, please refer to [Dubbo Provider Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-provider).



### Dubbo service(s) consumer



Service consumer requires Spring Beans to reference `DemoService`:

```java
@RestController
public class DemoConsumerController {

    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            url = "dubbo://localhost:12345")
    private DemoService demoService;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

}
```



then, also provide a bootstrap class:

```java
@SpringBootApplication(scanBasePackages = "com.alibaba.boot.dubbo.demo.consumer.controller")
public class DubboConsumerDemo {

    public static void main(String[] args) {

        SpringApplication.run(DubboConsumerDemo.class,args);

    }

}
```



last, configures `application.properties`:

```properties
# Spring boot application
spring.application.name = dubbo-consumer-demo
server.port = 8080
management.port = 8081


# Dubbo Config properties
## ApplicationConfig Bean
dubbo.application.id = dubbo-consumer-demo
dubbo.application.name = dubbo-consumer-demo

## ProtocolConfig Bean
dubbo.protocol.id = dubbo
dubbo.protocol.name = dubbo
dubbo.protocol.port = 12345
```



If `DubboProviderDemo` works well, please mark sure Dubbo service(s) is active.



More details, please refer to [Dubbo Consumer Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-consumer)



## Getting help

Having trouble with Dubbo Spring Boot? We’d like to help!

- If you are upgrading, read the [release notes](https://github.com/dubbo/dubbo-spring-boot-project/releases) for upgrade instructions and "new and noteworthy" features.
- Ask a question - You can join [ours google groups](https://groups.google.com/group/dubbo), or subscribe [Dubbo User Mailling List](mailto:dubbo+subscribe@googlegroups.com).
- Report bugs at [github.com/dubbo/dubbo-spring-boot-project/issues](https://github.com/dubbo/dubbo-spring-boot-project/issues).




## Building from Source

If you want to try out thr latest features of Dubbo Spring Boot, it can be easily built with the [maven wrapper](https://github.com/takari/maven-wrapper). Your JDK is 1.7 or above.

```
$ ./mvnw clean install
```



## Modules

There are some modules in Dubbo Spring Boot Project, let's take a look at below overview:



### [dubbo-spring-boot-parent](dubbo-spring-boot-parent)

The main usage of `dubbo-spring-boot-parent` is providing dependencies management for other modules.



### [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure)

`dubbo-spring-boot-autoconfigure` uses Spring Boot's `@EnableAutoConfiguration` which helps core Dubbo's components to be auto-configured by `DubboAutoConfiguration`. It reduces code, eliminates XML configuration. 



### [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator)

`dubbo-spring-boot-actuator` provides production-ready features (e.g., [health checks](https://github.com/dubbo/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-actuator#health-checks), [endpoints](https://github.com/dubbo/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-actuator#endpoints), and [externalized configuration](https://github.com/dubbo/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-actuator#externalized-configuration)).



### [dubbo-spring-boot-starter](dubbo-spring-boot-starter)

`dubbo-spring-boot-starter` is a standard Spring Boot Starter, which contains [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) and [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator). It will be imported into your application directly.



### [dubbo-spring-boot-samples](dubbo-spring-boot-samples)

The samples project of Dubbo Spring Boot that includes two parts:



#### [Dubbo Provider Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-provider)

Dubbo Service will be exported on localhost with port `12345`.

* [Health Checks](dubbo-spring-boot-actuator#health-checks): http://localhost:9091/health
* [Dubbo Endpoint](dubbo-spring-boot-actuator#endpoints): http://localhost:9091/dubbo



#### [Dubbo Consumer Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-consumer)

Dubbo Service will be consumed at Spring WebMVC `Controller`.

* Demo `Controller`: http://localhost:8080/sayHello?name=HelloWorld
* [Health Checks](dubbo-spring-boot-actuator#health-checks): http://localhost:8081/actuator/health
* [Dubbo Endpoint](dubbo-spring-boot-actuator#endpoints): http://localhost:8081/actuator/dubbo

