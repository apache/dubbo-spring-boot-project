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
package org.apache.dubbo.spring.boot.autoconfigure;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.properties.DubboConfigBinder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link RelaxedDubboConfigBinder} Test
 */
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "dubbo.application.NAME=hello",
        "dubbo.application.owneR=world",
        "dubbo.registry.Address=10.20.153.17",
        "dubbo.protocol.pORt=20881",
        "dubbo.service.invoke.timeout=2000",
})
@ContextConfiguration(classes = RelaxedDubboConfigBinder.class)
public class RelaxedDubboConfigBinderTest {

    @Autowired
    private DubboConfigBinder dubboConfigBinder;

    @Test
    public void testBinder() {

        ApplicationConfig applicationConfig = new ApplicationConfig();
        dubboConfigBinder.bind("dubbo.application", applicationConfig);
        Assert.assertEquals("hello", applicationConfig.getName());
        Assert.assertEquals("world", applicationConfig.getOwner());

        RegistryConfig registryConfig = new RegistryConfig();
        dubboConfigBinder.bind("dubbo.registry", registryConfig);
        Assert.assertEquals("10.20.153.17", registryConfig.getAddress());

        ProtocolConfig protocolConfig = new ProtocolConfig();
        dubboConfigBinder.bind("dubbo.protocol", protocolConfig);
        Assert.assertEquals(Integer.valueOf(20881), protocolConfig.getPort());

    }
}
