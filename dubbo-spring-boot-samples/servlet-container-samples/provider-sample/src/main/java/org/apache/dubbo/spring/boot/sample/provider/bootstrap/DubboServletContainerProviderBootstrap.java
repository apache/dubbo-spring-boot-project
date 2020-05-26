/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.spring.boot.sample.provider.bootstrap;

import org.apache.dubbo.spring.boot.sample.consumer.DemoService;
import org.apache.dubbo.spring.boot.sample.provider.service.DefaultDemoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dubbo Servlet Container Provider Bootstrap
 *
 * @see DefaultDemoService
 * @since 2.7.7
 */
@EnableAutoConfiguration
@RestController
public class DubboServletContainerProviderBootstrap extends SpringBootServletInitializer {

    @Autowired
    private DemoService demoService;

    @RequestMapping("/say/{name}")
    public String say(@PathVariable String name) {
        return demoService.sayHello(name);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(getClass());
    }

    public static void main(String[] args) { // Run as the generic Spring Boot Web(Servlet) Application
        SpringApplication application = new SpringApplication(DubboServletContainerProviderBootstrap.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }
}
