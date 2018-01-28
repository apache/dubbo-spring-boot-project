# Dubbo Spring Boot Project

[Dubbo](https://github.com/alibaba/dubbo) Spring Boot Project makes it easy to create [Spring Boot](https://github.com/spring-projects/spring-boot/) application using Dubbo as RPC Framework. What's more , it aslo provides production-ready features (e.g.security, health checks, externalized configuration).



> Dubbo *|ˈdʌbəʊ|* is a high-performance, java based [RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) framework open-sourced by Alibaba. As in many RPC systems, dubbo is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types. On the server side, the server implements this interface and runs a dubbo server to handle client calls. On the client side, the client has a stub that provides the same methods as the server.





## Getting Started

If you don't know about Dubbo , please take a few minutes to learn http://dubbo.io/ . After that  you could dive deep into dubbo [user guide](http://dubbo.io/books/dubbo-user-book-en/).

Usually , There are two usage scenarios for Dubbo applications , one is Dubbo service(s) provider , another is Dubbo service(s) consumer, thus let's get a quick start on them.

First of all , we suppose an interface as Dubbo RPC API that  a service provider exports and a service client consumes : 

```java
public interface DemoService {

    String sayHello(String name);

}
```





### Dubbo service(s) provider

Service Provider implements `DemoService` :

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



then , provides a bootstrap class : 

```java
@SpringBootApplication
public class DubboProviderDemo {

    public static void main(String[] args) {

        SpringApplication.run(DubboProviderDemo.class,args);

    }

}
```



last , configures `application.properties` :

```properties
# Spring boot application
spring.application.name = dubbo-provider-demo
server.port = 9090
management.port = 9091

# Base packages to scan Dubbo Components (e.g @Service , @Reference)
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



More details , please refer to [Dubbo Provider Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-provider).



### Dubbo service(s) consumer



Service consumer requires Spring Beans to reference `DemoService` :

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



then , also provide a bootstrap class :

```java
@SpringBootApplication(scanBasePackages = "com.alibaba.boot.dubbo.demo.consumer.controller")
public class DubboConsumerDemo {

    public static void main(String[] args) {

        SpringApplication.run(DubboConsumerDemo.class,args);

    }

}
```



last , configures `application.properties` :

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



If `DubboProviderDemo` works well , please mark sure Dubbo service(s) is active.



More details , please refer to [Dubbo Consumer Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-consumer)



## Externalized Configuration

Externalized Configuration is a core feature of Spring Boot , Dubbo Spring Boot not only supports it definitely , but also inherits Dubbo's Externalized Configuration, thus it provides single and multiple Dubbo's `*Config` Bindings from `PropertySources` , and `"dubbo."` is a common prefix of property name.

> If you'd like to learn more , please read [Dubbo Externalized Configuration](https://github.com/mercyblitz/blogs/blob/master/java/dubbo/Dubbo-Externalized-Configuration.md)(Chinese).



### Single Dubbo Config Bean Bindings

In most use scenarios , "Single Dubbo Config Bean Bindings" is enough , because a Dubbo application only requires single Bean of `*Config` (e.g `ApplicationConfig`). You add properties in `application.properties` to configure Dubbo's `*Config` Beans that you want , be like this :

```properties
dubbo.application.name = foo
dubbo.application.owner = bar
dubbo.registry.address = 10.20.153.10:9090
```

There are two Spring Beans will be initialized when Spring `ApplicatonContext` is ready, their Bean types are `ApplicationConfig` and `RegistryConfig`.



#### Getting Single Dubbo Config Bean

 If application requires current `ApplicationConfig` Bean in somewhere , you can get it from Spring `BeanFactory` as those code :

```java
BeanFactory beanFactory = .... 
ApplicationConfig applicationConfig = beanFactory.getBean(ApplicationConfig.class)
```

or inject it :

```java
@Autowired
private ApplicationConfig application;
```



#### Identifying Single Dubbo Config Bean

If you'd like to identify this `ApplicationConfig` Bean , you could add **"id"** property:

```properties
dubbo.application.id = application-bean-id
```



#### Mapping Single Dubbo Config Bean

The whole Properties Mapping of "Single Dubbo Config Bean Bindings" lists below :

| Dubbo `*Config` Type | The prefix of property name for Single Bindings |
| -------------------- | ---------------------------------------- |
| `ProtocolConfig`     | `dubbo.protocol`                         |
| `ApplicationConfig`  | `dubbo.application`                      |
| `ModuleConfig`       | `dubbo.module`                           |
| `RegistryConfig`     | `dubbo.registry`                         |
| `MonitorConfig`      | `dubbo.monitor`                          |
| `ProviderConfig`     | `dubbo.provider`                         |
| `ConsumerConfig`     | `dubbo.consumer`                         |



An example properties :

```properties
# Single Dubbo Config Bindings
## ApplicationConfig
dubbo.application.id = applicationBean
dubbo.application.name = dubbo-demo-application

## ModuleConfig
dubbo.module.id = moduleBean
dubbo.module.name = dubbo-demo-module

## RegistryConfig
dubbo.registry.address = zookeeper://192.168.99.100:32770

## ProtocolConfig
dubbo.protocol.name = dubbo
dubbo.protocol.port = 20880

## MonitorConfig
dubbo.monitor.address = zookeeper://127.0.0.1:32770

## ProviderConfig
dubbo.provider.host = 127.0.0.1

## ConsumerConfig
dubbo.consumer.client = netty
```



### Multiple Dubbo Config Bean Bindings

In contrast , "Multiple Dubbo Config Bean Bindings" means Externalized Configuration will be used to configure multiple Dubbo `*Config` Beans.



#### Getting Multiple Dubbo Config Bean

The whole Properties Mapping of "Multiple Dubbo Config Bean Bindings" lists below :

| Dubbo `*Config` Type | The prefix of property name for Multiple Bindings |
| -------------------- | ---------------------------------------- |
| `ProtocolConfig`     | `dubbo.protocols`                        |
| `ApplicationConfig`  | `dubbo.applications`                     |
| `ModuleConfig`       | `dubbo.modules`                          |
| `RegistryConfig`     | `dubbo.registries`                       |
| `MonitorConfig`      | `dubbo.monitors`                         |
| `ProviderConfig`     | `dubbo.providers`                        |
| `ConsumerConfig`     | `dubbo.consumers`                        |



#### Identifying Multiple Dubbo Config Bean

There is a  different wa to identify Multiple Dubbo Config Bean , the configuration pattern is like this :

`${config-property-prefix}.${config-bean-id}.${property-name} = some value` , let's explain those placehoders : 

* `${config-property-prefix}` : The The prefix of property name for Multiple Bindings , e.g. `dubbo.protocols`, `dubbo.applications` and so on.
* `${config-bean-id}` : The bean id of Dubbo's `*Config`
* `${property-name}`: The property name of  `*Config`



An example properties :

```properties
dubbo.applications.application1.name = dubbo-demo-application
dubbo.applications.application2.name = dubbo-demo-application2
dubbo.modules.module1.name = dubbo-demo-module
dubbo.registries.registry1.address = zookeeper://192.168.99.100:32770
dubbo.protocols.protocol1.name = dubbo
dubbo.protocols.protocol1.port = 20880
dubbo.monitors.monitor1.address = zookeeper://127.0.0.1:32770
dubbo.providers.provider1.host = 127.0.0.1
dubbo.consumers.consumer1.client = netty
```



## Getting help

Having trouble with Dubbo Spring Boot? We’d like to help!

- If you are upgrading, read the [release notes](https://github.com/dubbo/dubbo-spring-boot-project/releases) for upgrade instructions and "new and noteworthy" features.
- Ask a question - You can join [ours google groups](https://groups.google.com/group/dubbo) , or subscribe [Dubbo User Mailling List](mailto:dubbo+subscribe@googlegroups.com).
- Report bugs at [github.com/dubbo/dubbo-spring-boot-project/issues](https://github.com/dubbo/dubbo-spring-boot-project/issues).



## Building from Source

If you want to try out thr latest features of Dubbo Spring Boot , it can be easily built with the [maven wrapper](https://github.com/takari/maven-wrapper). Your JDK is 1.7 or above.

```
$ ./mvnw clean install
```



## Getting From Maven Repository



**Important Notes:** Work in-process , coming soon...



```xml
<dependency>
  <groupId>com.alibaba.boot</groupId>
  <artifactId>dubbo-spring-boot-starter</artifactId>
  <version>${revision}</version>
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



#### Endpoints

Dubbo Spring Boot providers actuator endpoint `dubbo` , however it is disable. If you'd like to enable it , please add property into `application.properties` :

```properties
# Dubbo Endpoint (default status is disable)
endpoints.dubbo.enabled = true
```



Actuator endpoint `dubbo` supports Spring Web MVC Endpoints : 

| URI                 | HTTP Method | Description                         | Content Type       |
| ------------------- | ----------- | ----------------------------------- | ------------------ |
| `/dubbo`            | `GET`       | Exposes Dubbo's meta data           | `application/json` |
| `/dubbo/properties` | `GET`       | Exposes all Dubbo's Properties      | `application/json` |
| `/dubbo/services`   | `GET`       | Exposes all Dubbo's `ServiceBean`   | `application/json` |
| `/dubbo/references` | `GET`       | Exposes all Dubbo's `ReferenceBean` | `application/json` |
| `/dubbo/configs`    | `GET`       | Exposes all Dubbo's `*Config`       | `application/json` |
| `/dubbo/shutdown`   | `POST`      | Shutdown Dubbo services             | `application/json` |



##### Endpoint : `/dubbo`

`/dubbo` exposes Dubbo's meta data : 

```json
{
  "timestamp": 1516623290166,
  "versions": {
    "dubbo-spring-boot": "1.0.0"
    "dubbo": "2.5.9"
  },
  "urls": {
    "dubbo": "https://github.com/alibaba/dubbo",
    "google-group": "http://groups.google.com/group/dubbo",
    "github": "https://github.com/dubbo/dubbo-spring-boot-project",
    "issues": "https://github.com/dubbo/dubbo-spring-boot-project/issues",
    "git": "https://github.com/dubbo/dubbo-spring-boot-project.git"
  },
  "endpoints": {
    "shutdown": "/shutdown",
    "configs": "/configs",
    "services": "/services",
    "references": "/references",
    "properties": "/properties"
  }
}
```





##### Endpoint : `/dubbo/properties`

`/dubbo/properties` exposes all Dubbo's Properties from Spring Boot Externalized Configuration (a.k.a `PropertySources`) : 

```json
{
  "dubbo.application.id": "dubbo-provider-demo",
  "dubbo.application.name": "dubbo-provider-demo",
  "dubbo.application.qos-enable": "false",
  "dubbo.application.qos-port": "33333",
  "dubbo.protocol.id": "dubbo",
  "dubbo.protocol.name": "dubbo",
  "dubbo.protocol.port": "12345",
  "dubbo.registry.address": "N/A",
  "dubbo.registry.id": "my-registry",
  "dubbo.scan.basePackages": "com.alibaba.boot.dubbo.demo.provider.service"
}
```

The structure of JSON is simple Key-Value format , the key is property name as and the value is property value.





##### Endpoint : `/dubbo/services`

`/dubbo/services` exposes all Dubbo's `ServiceBean` that are declared via `<dubbo:service/>` or `@Service`  present in Spring `ApplicationContext` :

```json
{
  "ServiceBean@com.alibaba.boot.dubbo.demo.api.DemoService#defaultDemoService": {
    "accesslog": null,
    "actives": null,
    "cache": null,
    "callbacks": null,
    "class": "com.alibaba.dubbo.config.spring.ServiceBean",
    "cluster": null,
    "connections": null,
    "delay": null,
    "document": null,
    "executes": null,
    "export": null,
    "exported": true,
    "filter": "",
    "generic": "false",
    "group": null,
    "id": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "interface": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "interfaceClass": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "layer": null,
    "listener": "",
    "loadbalance": null,
    "local": null,
    "merger": null,
    "mock": null,
    "onconnect": null,
    "ondisconnect": null,
    "owner": null,
    "path": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "proxy": null,
    "retries": null,
    "scope": null,
    "sent": null,
    "stub": null,
    "timeout": null,
    "token": null,
    "unexported": false,
    "uniqueServiceName": "com.alibaba.boot.dubbo.demo.api.DemoService:1.0.0",
    "validation": null,
    "version": "1.0.0",
    "warmup": null,
    "weight": null,
    "serviceClass": "com.alibaba.boot.dubbo.demo.provider.service.DefaultDemoService"
  }
}
```

The key is the Bean name of `ServiceBean` , `ServiceBean`'s properties compose value.



##### Endpoint : `/dubbo/references`

`/dubbo/references` exposes all Dubbo's `ReferenceBean` that are declared via `@Reference` annotating on `Field` or `Method  ` :

```json
{
  "private com.alibaba.boot.dubbo.demo.api.DemoService com.alibaba.boot.dubbo.demo.consumer.controller.DemoConsumerController.demoService": {
    "actives": null,
    "cache": null,
    "callbacks": null,
    "class": "com.alibaba.dubbo.config.spring.ReferenceBean",
    "client": null,
    "cluster": null,
    "connections": null,
    "filter": "",
    "generic": null,
    "group": null,
    "id": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "interface": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "interfaceClass": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "layer": null,
    "lazy": null,
    "listener": "",
    "loadbalance": null,
    "local": null,
    "merger": null,
    "mock": null,
    "objectType": "com.alibaba.boot.dubbo.demo.api.DemoService",
    "onconnect": null,
    "ondisconnect": null,
    "owner": null,
    "protocol": null,
    "proxy": null,
    "reconnect": null,
    "retries": null,
    "scope": null,
    "sent": null,
    "singleton": true,
    "sticky": null,
    "stub": null,
    "stubevent": null,
    "timeout": null,
    "uniqueServiceName": "com.alibaba.boot.dubbo.demo.api.DemoService:1.0.0",
    "url": "dubbo://localhost:12345",
    "validation": null,
    "version": "1.0.0",
    "invoker": {
      "class": "com.alibaba.dubbo.common.bytecode.proxy0"
    }
  }
}
```

The key is the string presentation of `@Reference` `Field` or `Method  `   , `ReferenceBean`'s properties compose value.



##### Endpoint : `/dubbo/configs`

 `/dubbo/configs` exposes all Dubbo's `*Config` :

```json
{
  "ApplicationConfig": {
    "dubbo-consumer-demo": {
      "architecture": null,
      "class": "com.alibaba.dubbo.config.ApplicationConfig",
      "compiler": null,
      "dumpDirectory": null,
      "environment": null,
      "id": "dubbo-consumer-demo",
      "logger": null,
      "name": "dubbo-consumer-demo",
      "organization": null,
      "owner": null,
      "version": null
    }
  },
  "ConsumerConfig": {
    
  },
  "MethodConfig": {
    
  },
  "ModuleConfig": {
    
  },
  "MonitorConfig": {
    
  },
  "ProtocolConfig": {
    "dubbo": {
      "accepts": null,
      "accesslog": null,
      "buffer": null,
      "charset": null,
      "class": "com.alibaba.dubbo.config.ProtocolConfig",
      "client": null,
      "codec": null,
      "contextpath": null,
      "dispatcher": null,
      "dispather": null,
      "exchanger": null,
      "heartbeat": null,
      "host": null,
      "id": "dubbo",
      "iothreads": null,
      "name": "dubbo",
      "networker": null,
      "path": null,
      "payload": null,
      "port": 12345,
      "prompt": null,
      "queues": null,
      "serialization": null,
      "server": null,
      "status": null,
      "telnet": null,
      "threadpool": null,
      "threads": null,
      "transporter": null
    }
  },
  "ProviderConfig": {
    
  },
  "ReferenceConfig": {
    
  },
  "RegistryConfig": {
    
  },
  "ServiceConfig": {
    
  }
}
```

The key is the simple name of Dubbo `*Config`  Class , the value is`*Config` Beans' Name-Properties Map.



##### Endpoint : `/dubbo/shutdown`

`/dubbo/shutdown` shutdowns Dubbo's components including registries, protocols, services and references :

```json
{
    "shutdown.count": {
        "registries": 0,
        "protocols": 1,
        "services": 0,
        "references": 1
    }
}
```

"shutdown.count" means the count of shutdown of Dubbo's components , and the value indicates how many components have been shutdown.



### [dubbo-spring-boot-starter](dubbo-spring-boot-starter)



[dubbo-spring-boot-starter](dubbo-spring-boot-starter) is a standard Spring Boot Starter, which contains [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) and [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator). It will be imported into your application directly.



### [dubbo-spring-boot-samples](dubbo-spring-boot-samples)



The samples project of Dubbo Spring Boot that includes two parts:

#### [Dubbo Provider Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-provider)



Dubbo Service will be exported on localhost with port `12345`.



#### [Dubbo Consumer Sample](dubbo-spring-boot-samples/dubbo-spring-boot-sample-consumer)



Dubbo Service will be cosumed at Spring WebMVC `Controller` , please visit http://localhost:8080/sayHello?name=HelloWorld after sample launch.

