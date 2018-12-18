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
package com.alibaba.boot.dubbo.actuate.endpoint;

import com.alibaba.boot.dubbo.actuate.autoconfigure.DubboEndpointsAutoConfiguration;
import com.alibaba.dubbo.config.annotation.Service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.SortedMap;

/**
 * {@link DubboEndpointsAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                DubboEndpointsAutoConfiguration.class,
                DubboEndpointsAutoConfigurationTest.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "dubbo.service.version = 1.0.0",
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
                "dubbo.scan.basePackages=com.alibaba.boot.dubbo.actuate.endpoint",
                "management.endpoint.dubbo.enabled = true",
                "management.endpoint.dubboShutdown.enabled = true",
                "management.endpoint.dubboConfigs.enabled = true",
                "management.endpoint.dubboServices.enabled = true",
                "management.endpoint.dubboReferences.enabled = true",
                "management.endpoint.dubboProperties.enabled = true",
        })
@EnableAutoConfiguration
public class DubboEndpointsAutoConfigurationTest {

    @Autowired
    private DubboEndpoint dubboEndpoint;

    @Autowired
    private DubboConfigsMetadataEndpoint dubboConfigsMetadataEndpoint;

    @Autowired
    private DubboPropertiesEndpoint dubboPropertiesEndpoint;

    @Autowired
    private DubboReferencesMetadataEndpoint dubboReferencesMetadataEndpoint;

    @Autowired
    private DubboServicesMetadataEndpoint dubboServicesMetadataEndpoint;

    @Autowired
    private DubboShutdownEndpoint dubboShutdownEndpoint;


    @Test
    public void testShutdown() throws Exception {

        Map<String, Object> value = dubboShutdownEndpoint.shutdown();

        Map<String, Object> shutdownCounts = (Map<String, Object>) value.get("shutdown.count");

        Assert.assertEquals(0, shutdownCounts.get("registries"));
        Assert.assertEquals(1, shutdownCounts.get("protocols"));
        Assert.assertEquals(1, shutdownCounts.get("services"));
        Assert.assertEquals(0, shutdownCounts.get("references"));

    }

    @Test
    public void testConfigs() {

        Map<String, Map<String, Map<String, Object>>> configsMap = dubboConfigsMetadataEndpoint.configs();

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

        Map<String, Map<String, Object>> services = dubboServicesMetadataEndpoint.services();

        Assert.assertEquals(1, services.size());

        Map<String, Object> demoServiceMeta = services.get("ServiceBean:com.alibaba.boot.dubbo.actuate.endpoint.DubboEndpointsAutoConfigurationTest$DemoService:1.0.0");

        Assert.assertEquals("1.0.0", demoServiceMeta.get("version"));

    }

    @Test
    public void testReferences() {

        Map<String, Map<String, Object>> references = dubboReferencesMetadataEndpoint.references();

        Assert.assertTrue(references.isEmpty());

    }

    @Test
    public void testProperties() {

        SortedMap<String, Object> properties = dubboPropertiesEndpoint.properties();

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
        Assert.assertEquals("com.alibaba.boot.dubbo.actuate.endpoint", properties.get("dubbo.scan.basePackages"));
    }


@Service(
        version = "${dubbo.service.version}",
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
