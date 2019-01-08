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
package com.alibaba.boot.dubbo.demo.provider.bootstrap;

import com.alibaba.boot.dubbo.demo.provider.service.DefaultDemoService;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

/**
 * Dubbo Registry ZooKeeper Provider Bootstrap
 *
 * @see DefaultDemoService
 * @since 1.0.0
 */
@EnableAutoConfiguration
public class DubboRegistryZooKeeperProviderBootstrap {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DubboRegistryZooKeeperProviderBootstrap.class)
                .web(false)
                .listeners(new ApplicationListener<ApplicationEnvironmentPreparedEvent>() {
                    @Override
                    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
                        Environment environment = event.getEnvironment();
                        int port = environment.getProperty("embedded.zookeeper.port", int.class);
                        new EmbeddedZooKeeper(port, false).start();
                    }
                }).
                run(args);
    }
}
