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
package com.alibaba.boot.dubbo.context.event;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

/**
 * {@link OverrideDubboConfigApplicationListener} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see OverrideDubboConfigApplicationListener
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(
        properties = {
                "dubbo.application.name = dubbo-demo-application",
                "dubbo.module.name = dubbo-demo-module",
                "dubbo.registry.address = zookeeper://192.168.99.100:32770"
        }
)
@EnableConfigurationProperties(
        value = {OverrideDubboConfigApplicationListener.class}
)
@SpringBootTest
public class OverrideDubboConfigApplicationListenerTest {

    @BeforeClass
    public static void init() {
        ConfigUtils.getProperties().clear();
    }

    @Test
    public void testOnApplicationEvent() {

        Properties properties = ConfigUtils.getProperties();

        Assert.assertEquals("dubbo-demo-application",properties.get("dubbo.application.name"));
        Assert.assertEquals("dubbo-demo-module",properties.get("dubbo.module.name"));
        Assert.assertEquals("zookeeper://192.168.99.100:32770",properties.get("dubbo.registry.address"));

    }

}
