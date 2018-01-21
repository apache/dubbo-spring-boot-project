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
package com.alibaba.boot.dubbo.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.SortedMap;

import static com.alibaba.boot.dubbo.util.DubboUtils.*;

/**
 * {@link DubboUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DubboUtils
 * @since 1.0.0
 */
public class DubboUtilsTest {

    @Test
    public void testConstants() {

        Assert.assertEquals("dubbo", DUBBO_PREFIX);

        Assert.assertEquals("dubbo.scan", DUBBO_SCAN_PREFIX);

        Assert.assertEquals("dubbo.scan.basePackages", BASE_PACKAGES_PROPERTY_NAME);

        Assert.assertEquals("dubbo.config", DUBBO_CONFIG_PREFIX);

        Assert.assertEquals("dubbo.config.multiple", MULTIPLE_CONFIG_PROPERTY_NAME);

        Assert.assertEquals("dubbo.config.override", OVERRIDE_CONFIG_PROPERTY_NAME);

        Assert.assertEquals("https://github.com/dubbo/dubbo-spring-boot-project", DUBBO_SPRING_BOOT_GITHUB_URL);
        Assert.assertEquals("https://github.com/dubbo/dubbo-spring-boot-project.git", DUBBO_SPRING_BOOT_GIT_URL);
        Assert.assertEquals("https://github.com/dubbo/dubbo-spring-boot-project/issues", DUBBO_SPRING_BOOT_ISSUES_URL);

        Assert.assertEquals("https://github.com/alibaba/dubbo", DUBBO_GITHUB_URL);

        Assert.assertEquals("http://groups.google.com/group/dubbo", DUBBO_GOOGLE_GROUP_URL);

        Assert.assertFalse(DEFAULT_MULTIPLE_CONFIG_PROPERTY_VALUE);

        Assert.assertTrue(DEFAULT_OVERRIDE_CONFIG_PROPERTY_VALUE);

    }


    @Test
    public void testFilterDubboProperties() {

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("message", "Hello,World");
        environment.setProperty(MULTIPLE_CONFIG_PROPERTY_NAME, "true");
        environment.setProperty(OVERRIDE_CONFIG_PROPERTY_NAME, "true");

        SortedMap<String, Object> dubboProperties = filterDubboProperties(environment);

        Assert.assertEquals("true",dubboProperties.get(MULTIPLE_CONFIG_PROPERTY_NAME));
        Assert.assertEquals("true",dubboProperties.get(OVERRIDE_CONFIG_PROPERTY_NAME));

    }

}
