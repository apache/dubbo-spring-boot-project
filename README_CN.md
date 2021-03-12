# Dubbo Spring Boot 工程

[![Build Status](https://travis-ci.org/apache/dubbo-spring-boot-project.svg?branch=master)](https://travis-ci.org/apache/dubbo-spring-boot-project) 
[![codecov](https://codecov.io/gh/apache/dubbo-spring-boot-project/branch/master/graph/badge.svg)](https://codecov.io/gh/apache/dubbo-spring-boot-project)
![license](https://img.shields.io/github/license/apache/dubbo-spring-boot-project.svg)

[Dubbo](https://dubbo.apache.org/zh-cn/index.html) Apache Dubbo™ 是一款高性能Java RPC框架。
[Spring Boot](https://github.com/spring-projects/spring-boot/) 应用场景的开发。同时也整合了 Spring Boot 特性：

* [自动装配](dubbo-spring-boot-autoconfigure) (比如： 注解驱动, 自动装配等).
* [Production-Ready](dubbo-spring-boot-actuator) (比如： 安全, 健康检查, 外部化配置等).

> Apache Dubbo  |ˈdʌbəʊ| 是一款高性能、轻量级的开源Java RPC框架，它提供了三大核心能力：面向接口的远程方法调用，智能容错和负载均衡，以及服务自动注册和发现。


## [English README](README.md)


## 已发行版本

您可以为您的工程引入最新 `dubbo-spring-boot-starter` 的发布，增加以下依赖到工程的 `pom.xml` 文件中：
```xml
<properties>
    <spring-boot.version>2.3.0.RELEASE</spring-boot.version>
    <dubbo.version>2.7.8</dubbo.version>
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

        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>${dubbo.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework</groupId>
                    <artifactId>spring</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>javax.servlet</groupId>
                    <artifactId>servlet-api</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Dubbo Spring Boot Starter -->
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo-spring-boot-starter</artifactId>
        <version>2.7.8</version>
    </dependency>
    
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo</artifactId>
    </dependency>
</dependencies>
```

如果您的工程遇到了依赖问题, 请尝试添加如下 Maven 参考到工程的 `pom.xml` 文件中：
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
### 历史版本

如果您现在使用的Dubbo版本低于2.7.0，请使用如下对应版本的Dubbo Spring Boot：

| Dubbo Spring Boot | Dubbo  | Spring Boot |
| ----------------- | ------ | ----------- |
| [0.2.1.RELEASE](https://github.com/apache/dubbo-spring-boot-project/tree/0.2.x)     | 2.6.5+ | 2.x         |
| [0.1.2.RELEASE](https://github.com/apache/dubbo-spring-boot-project/tree/0.1.x)     | 2.6.5+ | 1.x         |


### 源代码构建

如果您需要尝试最新 `dubbo-spring-boot-project` 的特性，您可将当前工程手动 Maven install 到本地 Maven 仓库：

1. Maven install 当前工程
> Maven install = `mvn install`




## 快速开始

如果您对 Dubbo 不是非常了解，耽误您几分钟访问 http://dubbo.apache.org/ 。了解后，如果您期望更深入的探讨，可以移步[用户手册](http://dubbo.apache.org/zh-cn/docs/user/quick-start.html)。

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
    @DubboService(version = "1.0.0")
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



2. 编写 Spring Boot 引导程序

    ```java
    @EnableAutoConfiguration
    public class DubboProviderDemo {

        public static void main(String[] args) {
            SpringApplication.run(DubboProviderDemo.class,args);
        }
    }
    ```


3. 配置 `application.properties` :

    ```properties
    # Spring boot application
    spring.application.name=dubbo-auto-configuration-provider-demo
    # Base packages to scan Dubbo Component: @org.apache.dubbo.config.annotation.Service
    dubbo.scan.base-packages=org.apache.dubbo.spring.boot.sample.provider.service

    # Dubbo Application
    ## The default value of dubbo.application.name is ${spring.application.name}
    ## dubbo.application.name=${spring.application.name}

    # Dubbo Protocol
    dubbo.protocol.name=dubbo
    dubbo.protocol.port=12345

    ## Dubbo Registry
    dubbo.registry.address=N/A
    ```




### 实现 Dubbo 服务消费方


1. 通过 `@DubboReference` 注入 `DemoService` :

    ```java
    @EnableAutoConfiguration
    public class DubboAutoConfigurationConsumerBootstrap {
    
        private final Logger logger = LoggerFactory.getLogger(getClass());
    
        @DubboReference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
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



2. 配置 `application.yml` :

    ```yaml
    spring:
      application:
        name: dubbo-auto-configure-consumer-sample
    ```


请确保 Dubbo 服务提供方服务可用， `DubboProviderDemo` 运行方可正常。


更多的实现细节，请参考 [Dubbo 示例](dubbo-spring-boot-samples)




## 社区交流

如果您在使用 Dubbo Spring Boot 中遇到任何问题或者有什么建议? 我们非常需要您的支持!

- 如果您需要升级版本，请提前阅读[发布公告](https://github.com/dubbo/dubbo-spring-boot-project/releases)，了解最新的特性和问题修复。
- 如果您遇到任何问题 ，您可以订阅 [Dubbo 用户邮件列表](mailto:dubbo+subscribe@googlegroups.com)。
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
* [控制端点](dubbo-spring-boot-actuator#endpoints)
* [外部化配置](dubbo-spring-boot-actuator#externalized-configuration)


### [dubbo-spring-boot-starter](dubbo-spring-boot-starter)

[dubbo-spring-boot-starter](dubbo-spring-boot-starter) 模块为标准的 Spring Boot Starter ,
当您将它引入到工程后，[dubbo-spring-boot-autoconfigure](dubbo-spring-boot-autoconfigure) 模块会一同被间接依赖。


### [dubbo-spring-boot-samples](dubbo-spring-boot-samples)

Dubbo Spring Boot 示例工程包括:

- [自动装配](dubbo-spring-boot-samples/auto-configure-samples)
- [外部化配置](dubbo-spring-boot-samples/externalized-configuration-samples)
- [Zookeeper 注册中心](https://github.com/apache/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-samples/registry-samples/zookeeper-samples)
- [Nacos 注册中心](https://github.com/apache/dubbo-spring-boot-project/tree/master/dubbo-spring-boot-samples/registry-samples/nacos-samples)



## License

Apache Dubbo spring boot project 基于Apache 2.0许可证开源，详细请参考[LICENSE](https://github.com/apache/dubbo-spring-boot-project/blob/master/LICENSE)。

