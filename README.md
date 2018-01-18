# Dubbo Spring Boot Project

[Dubbo](https://github.com/alibaba/dubbo) Spring Boot Project makes it easy to create [Spring Boot](https://github.com/spring-projects/spring-boot/) application using Dubbo as RPC Framework. What's more , it aslo provides production-ready features (e.g.security, health checks, externalized configuration).



> Dubbo *|ˈdʌbəʊ|* is a high-performance, java based [RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) framework open-sourced by Alibaba. As in many RPC systems, dubbo is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types. On the server side, the server implements this interface and runs a dubbo server to handle client calls. On the client side, the client has a stub that provides the same methods as the server.



## Getting Started

If you don't know about Dubbo , please take a few minutes to learn http://dubbo.io/ . After that  you could dive deep into dubbo [user guide](http://dubbo.io/books/dubbo-user-book-en/).



**Important Notes:** Work in-process , coming soon...



## Getting help

Having trouble with Dubbo Spring Boot? We’d like to help!

- If you are upgrading, read the [release notes](https://github.com/dubbo/dubbo-spring-boot-project/releases) for upgrade instructions and "new and noteworthy" features.
- Ask a question - You can join [ours google groups](https://groups.google.com/group/dubbo) , or subscribe [Dubbo User Mailling List](mailto:dubbo+subscribe@googlegroups.com).
- Report bugs at [github.com/dubbo/dubbo-spring-boot-project/issues](https://github.com/dubbo/dubbo-spring-boot-project/issues).



## Building from Source

If you want to try out thr lastest features of Dubbo Spring Boot , it can be easily built with the [maven wrapper](https://github.com/takari/maven-wrapper). Your JDK is 1.7 or above.

```
$ ./mvnw clean install
```



## Getting From Maven Repository



**Important Notes:** Work in-process , coming soon...



```xml
<dependency>
  <groupId>com.alibaba.boot</groupId>
  <artifactId>dubbo-spring-boot-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```



## Modules

There are some modules in Dubbo Spring Boot Project  , let's take a look at below overview :



### [dubbo-spring-boot-parent](dubbo-spring-boot-parent)



The main usage of [dubbo-spring-boot-parent](dubbo-spring-boot-parent)  is providing dependencies management for other modules.



### [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure)

Since  `2.5.7`  , Dubbo totally supports Annotation-Driven , core Dubbo's components that are registered and initialized in  Spring application context , including exterialized configuration features. However , those features need to trigger in manual configuration , e.g `@DubboComponentScan` , `@EnableDubboConfig` or `@EnableDubbo`.

[dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) uses Spring Boot's `@EnableAutoConfiguration` which helps core Dubbo's components to be auto-configured by `DubboAutoConfiguration`. It reduces code, eliminates XML configuration. 

If you used advanced IDE tools , for instance [Jetbrains IDEA Ultimate](https://www.jetbrains.com/idea/) develops Dubbo Spring Boot application, it will popup the tips of Dubbo Configuration Bindings in `application.properties` : 

* Case 1 - Single Bindings 

  ![](config-popup-window.png)

* Case 2 - Mutiple Bindings 

  ![](mconfig-popup-window.png)

  ​

### [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator)



[dubbo-spring-boot-actuator](dubbo-spring-boot-actuator) provides production-ready features (e.g.security, health checks, externalized configuration).



**Important Notes:** Work-in-process , coming soon...



### [dubbo-spring-boot-starter](dubbo-spring-boot-starter)



[dubbo-spring-boot-starter](dubbo-spring-boot-starter) is a standard Spring Boot Starter, which contains [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) and [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator). It will be imported into your application directly.



### [dubbo-spring-boot-samples](dubbo-spring-boot-samples)



The samples project of Dubbo Spring Boot.



**Important Notes:** Work-in-process , coming soon...