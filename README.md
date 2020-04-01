# Apache Dubbo Spring Boot Project 

[![Build Status](https://travis-ci.org/apache/dubbo-spring-boot-project.svg?branch=master)](https://travis-ci.org/apache/dubbo-spring-boot-project) 
[![codecov](https://codecov.io/gh/apache/dubbo-spring-boot-project/branch/master/graph/badge.svg)](https://codecov.io/gh/apache/dubbo-spring-boot-project)
![license](https://img.shields.io/github/license/apache/dubbo-spring-boot-project.svg)

[Apache Dubbo](https://github.com/apache/dubbo) Spring Boot Project makes it easy to create [Spring Boot](https://github.com/spring-projects/spring-boot/) application using Dubbo as RPC Framework. What's more, it also provides 
[![codecov](https://codecov.io/gh/apache/dubbo-spring-boot-project/branch/master/graph/badge.svg)](https://codecov.io/gh/apache/ubbo-spring-boot-project)
![license](https://img.shields.io/github/license/apache/dubbo-spring-boot-project.svg)


* [auto-configure features](dubbo-spring-boot-autoconfigure) (e.g., annotation-driven, auto configuration, externalized configuration).
* [production-ready features](dubbo-spring-boot-actuator) (e.g., security, health checks, externalized configuration).

> Apache Dubbo  |ˈdʌbəʊ| is a high-performance, light weight, java based RPC framework. Dubbo offers three key functionalities, which include interface based remote call, fault tolerance & load balancing, and automatic service registration & discovery.

## [中文说明](README_CN.md)


## Released version

You can introduce the latest `dubbo-spring-boot-starter` to your project by adding the following dependency to your pom.xml
```xml
<properties>
    <spring-boot.version>2.2.6.RELEASE</spring-boot.version>
    <dubbo.version>2.7.6</dubbo.version>
</properties>
    
<dependencyManagement>
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        <!-- Apache Dubbo  -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-dependencies-bom</artifactId>
            <version>${dubbo.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
      
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Dubbo Spring Boot Starter -->
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo-spring-boot-starter</artifactId>
        <version>2.7.5</version>
    </dependency>    
</dependencies>
```

If your project failed to resolve the dependency, try to add the following repository:
```xml
<repositories>
    <repository>
        <id>apache.snapshots.https</id>
        <name>Apache Development Snapshot Repository</name>
        <url>https://repository.apache.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```


### Legacy Versions

If you still use the legacy Dubbo whose version is less than 2.7.0, please use the following Spring Boot starters:

| Dubbo Spring Boot | Dubbo  | Spring Boot |
| ----------------- | ------ | ----------- |
| [0.2.1.RELEASE](https://github.com/apache/dubbo-spring-boot-project/tree/0.2.x)     | 2.6.5+ | 2.x         |
| [0.1.2.RELEASE](https://github.com/apache/dubbo-spring-boot-project/tree/0.1.x)     | 2.6.5+ | 1.x         |



### Build from Source

If you'd like to attempt to experience latest features, you also can build from source as follow:

1. Maven install current project in your local repository.
> Maven install = `mvn install`




## Getting Started

If you don't know about Dubbo, please take a few minutes to learn http://dubbo.apache.org/. After that  you could dive deep into dubbo [user guide](http://dubbo.apache.org/en-us/docs/user/quick-start.html).

Usually, There are two usage scenarios for Dubbo applications, one is Dubbo service(s) provider, another is Dubbo service(s) consumer, thus let's get a quick start on them.

First of all, we suppose an interface as Dubbo RPC API that  a service provider exports and a service client consumes: 

```java
public interface DemoService {

    String sayHello(String name);

}
```



### Dubbo service(s) provider

1. Service Provider implements `DemoService`

    ```java
    @Service(version = "1.0.0")
    public class DefaultDemoService implements DemoService {
    
        /**
         * The default value of ${dubbo.application.name} is ${spring.application.name}
         */
        @Value("${dubbo.application.name}")
        private String serviceName;
    
        public String sayHello(String name) {
            return String.format("[%s] : Hello, %s", serviceName, name);
        }
    }
    ```

2. Provides a bootstrap class

    ```java
    @EnableAutoConfiguration
    public class DubboProviderDemo {

        public static void main(String[] args) {
            SpringApplication.run(DubboProviderDemo.class,args);
        }
    }
    ```

3. Configures the `application.properties`:

    ```properties
    # Spring boot application
    spring.application.name=dubbo-auto-configuration-provider-demo
    # Base packages to scan Dubbo Component: @org.apache.dubbo.config.annotation.Service
    dubbo.scan.base-packages=org.apache.dubbo.spring.boot.demo.provider.service

    # Dubbo Application
    ## The default value of dubbo.application.name is ${spring.application.name}
    ## dubbo.application.name=${spring.application.name}

    # Dubbo Protocol
    dubbo.protocol.name=dubbo
    dubbo.protocol.port=12345

    ## Dubbo Registry
    dubbo.registry.address=N/A
    ```



### Dubbo service(s) consumer

1. Service consumer also provides a bootstrap class to reference `DemoService`

    ```java
    @EnableAutoConfiguration
    public class DubboAutoConfigurationConsumerBootstrap {
    
        private final Logger logger = LoggerFactory.getLogger(getClass());
    
        @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
        private DemoService demoService;
    
        public static void main(String[] args) {
            SpringApplication.run(DubboAutoConfigurationConsumerBootstrap.class).close();
        }
    
        @Bean
        public ApplicationRunner runner() {
            return args -> {
                logger.info(demoService.sayHello("mercyblitz"));
            };
        }
    }
    ```

2. configures `application.yml`

    ```yaml
    spring:
      application:
        name: dubbo-auto-configure-consumer-sample
    ```

If `DubboProviderDemo` doesn't work well, please make sure `DubboProviderDemo` is started.

More details, please refer to [Samples](dubbo-spring-boot-samples).



## Getting help

Having trouble with Dubbo Spring Boot? We’d like to help!

- If you are upgrading, read the [release notes](https://github.com/dubbo/dubbo-spring-boot-project/releases) for upgrade instructions and "new and noteworthy" features.
- Ask a question - You can subscribe [Dubbo User Mailling List](mailto:dubbo+subscribe@googlegroups.com).
- Report bugs at [github.com/dubbo/dubbo-spring-boot-project/issues](https://github.com/dubbo/dubbo-spring-boot-project/issues).




## Building from Source

If you want to try out the latest features of Dubbo Spring Boot, it can be easily built with the [maven wrapper](https://github.com/takari/maven-wrapper). Your JDK is 1.8 or above.

```
$ ./mvnw clean install
```



## Modules

There are some modules in Apache Dubbo Spring Boot Project, let's take a look at below overview:



### [dubbo-spring-boot-parent](dubbo-spring-boot-parent)

The main usage of `dubbo-spring-boot-parent` is providing dependencies management for other modules.



### [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure)

`dubbo-spring-boot-autoconfigure` uses Spring Boot's `@EnableAutoConfiguration` which helps core Dubbo's components to be auto-configured by `DubboAutoConfiguration`. It reduces code, eliminates XML configuration. 



### [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator)

`dubbo-spring-boot-actuator` provides production-ready features (e.g., [health checks](https://github.com/dubbo/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-actuator#health-checks), [endpoints](https://github.com/dubbo/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-actuator#endpoints), and [externalized configuration](https://github.com/dubbo/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-actuator#externalized-configuration)).



### [dubbo-spring-boot-starter](dubbo-spring-boot-starter)

`dubbo-spring-boot-starter` is a standard Spring Boot Starter, which contains [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) and [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator). It will be imported into your application directly.



### [dubbo-spring-boot-samples](dubbo-spring-boot-samples)

The samples project of Dubbo Spring Boot that includes:

- [Auto-Configuaration Samples](dubbo-spring-boot-samples/auto-configure-samples)
- [Externalized Configuration Samples](dubbo-spring-boot-samples/externalized-configuration-samples)
- [Registry Zookeeper Samples](dubbo-spring-boot-samples/dubbo-registry-zookeeper-samples)
- [Registry Nacos Samples](dubbo-spring-boot-samples/dubbo-registry-nacos-samples)



## License

Apache Dubbo spring boot project is under the Apache 2.0 license. See the [LICENSE](https://github.com/apache/dubbo-spring-boot-project/blob/master/LICENSE) file for details.
