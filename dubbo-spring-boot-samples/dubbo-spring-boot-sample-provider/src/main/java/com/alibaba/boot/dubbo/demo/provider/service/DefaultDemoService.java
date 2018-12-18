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
package com.alibaba.boot.dubbo.demo.provider.service;

import com.alibaba.boot.dubbo.demo.consumer.DemoService;
import com.alibaba.dubbo.config.annotation.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Default {@link DemoService}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DemoService
 * @since 1.0.0
 */
@Service(
        version = "${demo.service.version}",
        protocol = {"dubbo", "rest"},
        registry = "${dubbo.registry.id}"
)
@Path("demo")
public class DefaultDemoService implements DemoService {

    @GET
    @Path("/say-hello")
    public String sayHello(@QueryParam("name") String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }
}