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
package org.apache.dubbo.spring.boot.sample.consumer.bootstrap;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.spring.boot.sample.consumer.DemoService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@EnableAutoConfiguration
@RestController
public class DubboServletContainerConsumerBootstrap extends SpringBootServletInitializer {

    @DubboReference(version = "${demo.service.version}", url = "${demo.service.url}")
    private DemoService demoService;

    @RequestMapping(value = "/say-hello", method = GET)
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(getClass());
    }

    public static void main(String[] args) { // Run as the generic Spring Boot Web(Servlet) Application
        SpringApplication application = new SpringApplication(DubboServletContainerConsumerBootstrap.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }
}
