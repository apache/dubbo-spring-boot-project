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
package com.alibaba.boot.dubbo.autoconfigure;

import com.alibaba.dubbo.config.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

/**
 * {@link DubboAutoConfiguration} Test On multiple Dubbo Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(
        properties = {
                "dubbo.applications.application1.name = dubbo-demo-application",
                "dubbo.applications.application2.name = dubbo-demo-application2",
                "dubbo.modules.module1.name = dubbo-demo-module",
                "dubbo.registries.registry1.address = zookeeper://192.168.99.100:32770",
                "dubbo.protocols.protocol1.name=dubbo",
                "dubbo.protocols.protocol1.port=20880",
                "dubbo.monitors.monitor1.address=zookeeper://127.0.0.1:32770",
                "dubbo.providers.provider1.host=127.0.0.1",
                "dubbo.consumers.consumer1.client=netty",
                "dubbo.config.multiple=true",
                "dubbo.scan.basePackages=com.alibaba.boot.dubbo, com.alibaba.boot.dubbo.condition"
        }
)
@EnableConfigurationProperties(
        value = {DubboAutoConfiguration.class}
)
@SpringBootTest
public class DubboAutoConfigurationOnMultipleConfigTest {

    @Autowired
    private DubboAutoConfiguration.MultipleDubboConfigConfiguration multipleDubboConfigConfiguration;

    @Autowired(required = false)
    private DubboAutoConfiguration.SingleDubboConfigConfiguration singleDubboConfigConfiguration;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * {@link ApplicationConfig}
     */
    @Autowired
    @Qualifier("application1")
    private ApplicationConfig application;

    /**
     * {@link ModuleConfig}
     */
    @Autowired
    @Qualifier("module1")
    private ModuleConfig module;

    /**
     * {@link RegistryConfig}
     */
    @Autowired
    @Qualifier("registry1")
    private RegistryConfig registry;

    /**
     * {@link ProtocolConfig}
     */
    @Autowired
    @Qualifier("protocol1")
    private ProtocolConfig protocol;

    /**
     * {@link MonitorConfig}
     */
    @Autowired
    @Qualifier("monitor1")
    private MonitorConfig monitor;

    /**
     * {@link ProviderConfig}
     */
    @Autowired
    @Qualifier("provider1")
    private ProviderConfig provider;

    /**
     * {@link ConsumerConfig}
     */
    @Autowired
    @Qualifier("consumer1")
    private ConsumerConfig consumer;

    @Autowired
    private DubboScanProperties dubboScanProperties;

    @Autowired
    private DubboConfigProperties dubboConfigProperties;

    @Autowired
    private MultipleDubboConfigBindingProperties multipleDubboConfigBindingProperties;

    @Test
    public void testDubboScanProperties() {

        Assert.assertFalse(dubboScanProperties.getBasePackages().isEmpty());

    }

    @Test
    public void testDubboConfigProperties() {

        Assert.assertTrue(dubboConfigProperties.isMultiple());

    }

    @Test
    public void testMultipleDubboConfigBindingProperties() {

        /**
         * Multiple {@link ApplicationConfig}
         */
        Map<String, ApplicationConfig> applications = multipleDubboConfigBindingProperties.getApplications();

        Assert.assertEquals(2, applications.size());

        applications = multipleDubboConfigBindingProperties.getApplications();
        Assert.assertEquals(2, applications.size());

        /**
         * Multiple {@link ModuleConfig}
         */
        Map<String, ModuleConfig> modules = multipleDubboConfigBindingProperties.getModules();

        Assert.assertEquals(1, modules.size());

        /**
         * Multiple {@link RegistryConfig}
         */
        Map<String, RegistryConfig> registries = multipleDubboConfigBindingProperties.getRegistries();

        Assert.assertEquals(1, registries.size());

        /**
         * Multiple {@link ProtocolConfig}
         */
        Map<String, ProtocolConfig> protocols = multipleDubboConfigBindingProperties.getProtocols();

        Assert.assertEquals(1, protocols.size());

        /**
         * Multiple {@link MonitorConfig}
         */
        Map<String, MonitorConfig> monitors = multipleDubboConfigBindingProperties.getMonitors();

        Assert.assertEquals(1, monitors.size());

        /**
         * Multiple {@link ProviderConfig}
         */
        Map<String, ProviderConfig> providers = multipleDubboConfigBindingProperties.getProviders();

        Assert.assertEquals(1, providers.size());

        /**
         * Multiple {@link ConsumerConfig}
         */
        Map<String, ConsumerConfig> consumers = multipleDubboConfigBindingProperties.getConsumers();

        Assert.assertEquals(1, consumers.size());

    }

    @Test
    public void testApplicationContext() {

        /**
         * Multiple {@link ApplicationConfig}
         */
        Map<String, ApplicationConfig> applications = beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class);

        Assert.assertEquals(2, applications.size());

        applications = multipleDubboConfigBindingProperties.getApplications();
        Assert.assertEquals(2, applications.size());

        /**
         * Multiple {@link ModuleConfig}
         */
        Map<String, ModuleConfig> modules = beansOfTypeIncludingAncestors(applicationContext, ModuleConfig.class);

        Assert.assertEquals(1, modules.size());

        /**
         * Multiple {@link RegistryConfig}
         */
        Map<String, RegistryConfig> registries = beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class);

        Assert.assertEquals(1, registries.size());

        /**
         * Multiple {@link ProtocolConfig}
         */
        Map<String, ProtocolConfig> protocols = beansOfTypeIncludingAncestors(applicationContext, ProtocolConfig.class);

        Assert.assertEquals(1, protocols.size());

        /**
         * Multiple {@link MonitorConfig}
         */
        Map<String, MonitorConfig> monitors = beansOfTypeIncludingAncestors(applicationContext, MonitorConfig.class);

        Assert.assertEquals(1, monitors.size());

        /**
         * Multiple {@link ProviderConfig}
         */
        Map<String, ProviderConfig> providers = beansOfTypeIncludingAncestors(applicationContext, ProviderConfig.class);

        Assert.assertEquals(1, providers.size());

        /**
         * Multiple {@link ConsumerConfig}
         */
        Map<String, ConsumerConfig> consumers = beansOfTypeIncludingAncestors(applicationContext, ConsumerConfig.class);

        Assert.assertEquals(1, consumers.size());

    }

    @Test
    public void testApplicationConfig() {

        Assert.assertEquals("dubbo-demo-application", application.getName());

    }

    @Test
    public void testModuleConfig() {

        Assert.assertEquals("dubbo-demo-module", module.getName());

    }

    @Test
    public void testRegistryConfig() {

        Assert.assertEquals("zookeeper://192.168.99.100:32770", registry.getAddress());

    }

    @Test
    public void testMonitorConfig() {

        Assert.assertEquals("zookeeper://127.0.0.1:32770", monitor.getAddress());

    }

    @Test
    public void testProtocolConfig() {

        Assert.assertEquals("dubbo", protocol.getName());
        Assert.assertEquals(Integer.valueOf(20880), protocol.getPort());

    }

    @Test
    public void testConsumerConfig() {

        Assert.assertEquals("netty", consumer.getClient());

    }

    @Test
    public void testMultipleDubboConfigConfiguration() {
        Assert.assertNotNull(multipleDubboConfigConfiguration);
    }

    @Test
    public void testSingleDubboConfigConfiguration() {
        Assert.assertNull(singleDubboConfigConfiguration);
    }


}
