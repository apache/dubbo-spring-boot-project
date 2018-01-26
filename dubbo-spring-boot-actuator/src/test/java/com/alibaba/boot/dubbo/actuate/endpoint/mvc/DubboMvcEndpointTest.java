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
package com.alibaba.boot.dubbo.actuate.endpoint.mvc;

import com.alibaba.boot.dubbo.actuate.endpoint.DubboEndpoint;
import com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration;
import com.alibaba.dubbo.config.annotation.Service;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.SortedMap;

/**
 * {@link DubboMvcEndpoint} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DubboMvcEndpoint
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(
        properties = {
                "dubbo.application.id = my-application",
                "dubbo.application.name = dubbo-demo-application",
                "dubbo.module.id = my-module",
                "dubbo.module.name = dubbo-demo-module",
                "dubbo.registry.id = my-registry",
                "dubbo.registry.address = N/A",
                "dubbo.protocol.id=my-protocol",
                "dubbo.protocol.name=dubbo",
                "dubbo.protocol.port=20880",
                "dubbo.provider.id=my-provider",
                "dubbo.provider.host=127.0.0.1",
                "dubbo.scan.basePackages=com.alibaba.boot.dubbo.actuate.endpoint.mvc"
        }
)
@SpringApplicationConfiguration(
        classes = {
                DubboAutoConfiguration.class,
                DubboEndpoint.class,
                DubboMvcEndpointTest.class
        }
)
@IntegrationTest
public class DubboMvcEndpointTest {

    @Bean
    public DubboMvcEndpoint dubboMvcEndpoint(DubboEndpoint dubboEndpoint) {
        return new DubboMvcEndpoint(dubboEndpoint);
    }

    @Autowired
    private DubboMvcEndpoint dubboMvcEndpoint;


    @Test
    public void testShutdown() throws Exception {

        DeferredResult result = dubboMvcEndpoint.shutdown();

        Map<String, Object> value = (Map<String, Object>) result.getResult();

        Map<String, Object> shutdownCounts = (Map<String, Object>) value.get("shutdown.count");

        Assert.assertEquals(0, shutdownCounts.get("registries"));
        Assert.assertEquals(1, shutdownCounts.get("protocols"));
        Assert.assertEquals(1, shutdownCounts.get("services"));
        Assert.assertEquals(0, shutdownCounts.get("references"));

    }

    @Test
    public void testConfigs() {

        Map<String, Map<String, Map<String, Object>>> configsMap = dubboMvcEndpoint.configs();

        Map<String, Map<String, Object>> beansMetadata = configsMap.get("ApplicationConfig");
        Assert.assertEquals("dubbo-demo-application", beansMetadata.get("my-application").get("name"));

        beansMetadata = configsMap.get("ConsumerConfig");
        Assert.assertTrue(beansMetadata.isEmpty());

        beansMetadata = configsMap.get("MethodConfig");
        Assert.assertTrue(beansMetadata.isEmpty());

        beansMetadata = configsMap.get("ModuleConfig");
        Assert.assertEquals("dubbo-demo-module", beansMetadata.get("my-module").get("name"));

        beansMetadata = configsMap.get("MonitorConfig");
        Assert.assertTrue(beansMetadata.isEmpty());

        beansMetadata = configsMap.get("ProtocolConfig");
        Assert.assertEquals("dubbo", beansMetadata.get("my-protocol").get("name"));

        beansMetadata = configsMap.get("ProviderConfig");
        Assert.assertEquals("127.0.0.1", beansMetadata.get("my-provider").get("host"));

        beansMetadata = configsMap.get("ReferenceConfig");
        Assert.assertTrue(beansMetadata.isEmpty());

        beansMetadata = configsMap.get("RegistryConfig");
        Assert.assertEquals("N/A", beansMetadata.get("my-registry").get("address"));

        beansMetadata = configsMap.get("ServiceConfig");
        Assert.assertFalse(beansMetadata.isEmpty());

    }

    @Test
    public void testServices() {

        Map<String, Map<String, Object>> services = dubboMvcEndpoint.services();

        Assert.assertEquals(1, services.size());

        Map<String, Object> demoServiceMeta = services.get("ServiceBean@com.alibaba.boot.dubbo.actuate.endpoint.mvc.DubboMvcEndpointTest$DemoService#dubboMvcEndpointTest.DefaultDemoService");

        Assert.assertEquals("1.0.0", demoServiceMeta.get("version"));

    }

    @Test
    public void testReferences() {

        Map<String, Map<String, Object>> references = dubboMvcEndpoint.references();

        Assert.assertTrue(references.isEmpty());

    }

    @Test
    public void testProperties() {

        SortedMap<String, Object> properties = dubboMvcEndpoint.properties();

        Assert.assertEquals("my-application", properties.get("dubbo.application.id"));
        Assert.assertEquals("dubbo-demo-application", properties.get("dubbo.application.name"));
        Assert.assertEquals("my-module", properties.get("dubbo.module.id"));
        Assert.assertEquals("dubbo-demo-module", properties.get("dubbo.module.name"));
        Assert.assertEquals("my-registry", properties.get("dubbo.registry.id"));
        Assert.assertEquals("N/A", properties.get("dubbo.registry.address"));
        Assert.assertEquals("my-protocol", properties.get("dubbo.protocol.id"));
        Assert.assertEquals("dubbo", properties.get("dubbo.protocol.name"));
        Assert.assertEquals("20880", properties.get("dubbo.protocol.port"));
        Assert.assertEquals("my-provider", properties.get("dubbo.provider.id"));
        Assert.assertEquals("127.0.0.1", properties.get("dubbo.provider.host"));
        Assert.assertEquals("com.alibaba.boot.dubbo.actuate.endpoint.mvc", properties.get("dubbo.scan.basePackages"));
    }


    @Service(
            version = "1.0.0",
            application = "${dubbo.application.id}",
            protocol = "${dubbo.protocol.id}",
            registry = "${dubbo.registry.id}"
    )
    static class DefaultDemoService implements DemoService {

        public String sayHello(String name) {
            return "Hello, " + name + " (from Spring Boot)";
        }

    }

    interface DemoService {
        String sayHello(String name);
    }

}
