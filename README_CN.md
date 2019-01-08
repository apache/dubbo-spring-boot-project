# Dubbo Spring Boot 工程

[Dubbo](https://github.com/alibaba/dubbo) Spring Boot 工程致力于简化 Dubbo RPC 框架在
[Spring Boot](https://github.com/spring-projects/spring-boot/) 应用场景的开发。同时也整合了 Spring Boot 特性：

* [自动装配](dubbo-spring-boot-autoconfigure) (比如： 注解驱动, 自动装配等).
* [Production-Ready](dubbo-spring-boot-actuator) (比如： 安全, 健康检查, 外部化配置等).



> Dubbo *|ˈdʌbəʊ|* is a high-performance, java based [RPC](https://en.wikipedia.org/wiki/Remote_procedure_call) framework open-sourced by Alibaba. As in many RPC systems, dubbo is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types. On the server side, the server implements this interface and runs a dubbo server to handle client calls. On the client side, the client has a stub that provides the same methods as the server.


**当前工程是支持 Spring Boot 1.x 的维护工程**


## 已发行版本

您可以为您的工程引入最新 `dubbo-spring-boot-starter` 的发布，增加以下依赖到工程的 `pom.xml` 文件中：
```xml
<dependencies>

    ...
    
    <dependency>
        <groupId>com.alibaba.boot</groupId>
        <artifactId>dubbo-spring-boot-starter</artifactId>
        <version>0.1.2</version>
    </dependency>

    <!-- Dubbo -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>dubbo</artifactId>
        <version>2.6.5</version>
    </dependency>
    <!-- Spring Context Extras -->
    <dependency>
        <groupId>com.alibaba.spring</groupId>
        <artifactId>spring-context-support</artifactId>
        <version>1.0.2</version>
    </dependency>
    
    ...
    
</dependencies>
```

如果您的工程遇到了依赖问题, 请尝试添加如下 Maven 参考到工程的 `pom.xml` 文件中：
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


## 开发版本

### 源代码构建

如果你需要尝试最新 `dubbo-spring-boot-project` 的特性，您可将当前工程手动 Maven install 到本地 Maven 仓库：

1. Maven install 当前工程
> Maven install = `mvn install`

### 依赖关系

| 版本 | Java  | Spring Boot       | Dubbo      |
| -------- | ----- | ----------------- | ---------- |
| `0.1.x`  | 1.7+ | `1.5.x` | `2.6.x`+ |



## 快速开始

如果您对 Dubbo 不是非常了解，耽误您几分钟访问 http://dubbo.io/。了解后，如果你期望更深入的探讨，可以移步[用户手册](http://dubbo.io/books/dubbo-user-book/)。

通常情况 , Dubbo 应用有两种使用场景 , 其一为 Dubbo 服务提供方 , 另外一个是 Dubbo 服务消费方，当然也允许两者混合，下面我们一起快速开始！

首先，我们假设存在一个 Dubbo RPC API ，由服务提供方为服务消费方暴露接口 :

```java
public interface DemoService {

    String sayHello(String name);

}
```



### 实现 Dubbo 服务提供方

1. 实现 `DemoService` 接口

```java
@Service(
        version = "${demo.service.version}",
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



2. 编写 Spring Boot 引导程序

```java
@SpringBootApplication
public class DubboProviderDemo {

    public static void main(String[] args) {

        new SpringApplicationBuilder(DubboProviderDemo.class)
                .web(false) // 非 Web 应用
                .run(args);

    }

}
```


3. 配置 `application.properties` :

```properties
# Spring boot application
spring.application.name = dubbo-provider-demo
server.port = 9090
management.port = 9091

# Service version
demo.service.version = 1.0.0

# Base packages to scan Dubbo Components (e.g @Service , @Reference)
dubbo.scan.base-packages  = com.alibaba.boot.dubbo.demo.provider.service

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

更多的实现细节 , 请参考 [Dubbo 服务提供方示例](dubbo-spring-boot-samples/dubbo-spring-boot-sample-provider).



### 实现 Dubbo 服务消费方


1. 通过 `@Reference` 注入 `DemoService` :

```java
@RestController
public class DemoConsumerController {

    @Reference(version = "${demo.service.version}",
            application = "${dubbo.application.id}",
            url = "dubbo://localhost:12345")
    private DemoService demoService;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

}
```



2. 编写 Spring Boot 引导程序（Web 应用） :

```java
@SpringBootApplication(scanBasePackages = "com.alibaba.boot.dubbo.demo.consumer.controller")
public class DubboConsumerDemo {

    public static void main(String[] args) {

        SpringApplication.run(DubboConsumerDemo.class,args);

    }

}
```



3. 配置 `application.properties` :

```properties
# Spring boot application
spring.application.name = dubbo-consumer-demo
server.port = 8080
management.port = 8081

# Service Version
demo.service.version = 1.0.0

# Dubbo Config properties
## ApplicationConfig Bean
dubbo.application.id = dubbo-consumer-demo
dubbo.application.name = dubbo-consumer-demo

## ProtocolConfig Bean
dubbo.protocol.id = dubbo
dubbo.protocol.name = dubbo
dubbo.protocol.port = 12345
```


请确保 Dubbo 服务提供方服务可用， `DubboProviderDemo` 运行方可正常。


更多的实现细节，请参考 [Dubbo 服务消费方示例](dubbo-spring-boot-samples/dubbo-spring-boot-sample-consumer)



## 社区交流

如果您在使用 Dubbo Spring Boot 中遇到任何问题或者有什么建议? 我们非常需要您的支持!

- 如果您需要升级版本，请提前阅读[发布公告](https://github.com/dubbo/dubbo-spring-boot-project/releases)，了解最新的特性和问题修复。
- 如果您遇到任何问题 ，您可以加入官方 [Google 讨论组](https://groups.google.com/group/dubbo) , 或者订阅 [Dubbo 用户邮件列表](mailto:dubbo+subscribe@googlegroups.com)。
- 问题反馈，您可以在 [issues](https://github.com/dubbo/dubbo-spring-boot-project/issues) 提出您遇到的使用问题。



## 模块工程

Dubbo Spring Boot 采用多 Maven 模块工程 , 模块如下：



### [dubbo-spring-boot-parent](dubbo-spring-boot-parent)

[dubbo-spring-boot-parent](dubbo-spring-boot-parent) 模块主要管理 Dubbo Spring Boot 工程的 Maven 依赖

### [dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure)

[dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) 模块提供 Spring Boot's `@EnableAutoConfiguration` 的实现 - `DubboAutoConfiguration`，
它简化了 Dubbo 核心组件的装配。



### [dubbo-spring-boot-actuator](dubbo-spring-boot-actuator)

[dubbo-spring-boot-actuator](dubbo-spring-boot-actuator) 提供 Production-Ready 特性：

* [健康检查](dubbo-spring-boot-actuator#health-checks)
* [控制断点](dubbo-spring-boot-actuator#endpoints)
* [外部化配置](dubbo-spring-boot-actuator#externalized-configuration))


### [dubbo-spring-boot-starter](dubbo-spring-boot-starter)

[dubbo-spring-boot-starter](dubbo-spring-boot-starter) 模块为标准的 Spring Boot Starter ,
当您将它引入到工程后，[dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) 模块会一同被间接依赖。



### [dubbo-spring-boot-samples](dubbo-spring-boot-samples)

The samples project of Dubbo Spring Boot that includes two parts:
[dubbo-spring-boot-samples](dubbo-spring-boot-samples) 为 Dubbo Spring Boot 示例工程，包括：


#### [Dubbo 服务提供方示例](dubbo-spring-boot-samples/dubbo-spring-boot-sample-provider)

Dubbo 服务将会通过 localhost 的 `12345` 端口暴露服务，并且提供 JMX Endpoints。


#### [Dubbo 服务消费方示例](dubbo-spring-boot-samples/dubbo-spring-boot-sample-consumer)

Dubbo 服务将被 Spring WebMVC `Controller` 消费，并且提供 JMX 以及 Web Endpoints 端口：

* 示例 `Controller` : http://localhost:8080/sayHello?name=HelloWorld
* [健康检查](dubbo-spring-boot-actuator#health-checks) : http://localhost:8081/health
* [Dubbo Endpoints](dubbo-spring-boot-actuator#endpoints) : http://localhost:8081/dubbo
