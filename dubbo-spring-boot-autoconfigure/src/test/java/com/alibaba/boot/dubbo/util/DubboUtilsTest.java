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

import static com.alibaba.boot.dubbo.util.DubboUtils.BASE_PACKAGES_PROPERTY_NAME;
import static com.alibaba.boot.dubbo.util.DubboUtils.DEFAULT_MULTIPLE_CONFIG_PROPERTY_VALUE;
import static com.alibaba.boot.dubbo.util.DubboUtils.DEFAULT_OVERRIDE_CONFIG_PROPERTY_VALUE;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_CONFIG_PREFIX;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_GITHUB_URL;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_MAILING_LIST;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_PREFIX;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_SCAN_PREFIX;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_SPRING_BOOT_GITHUB_URL;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_SPRING_BOOT_GIT_URL;
import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_SPRING_BOOT_ISSUES_URL;
import static com.alibaba.boot.dubbo.util.DubboUtils.MULTIPLE_CONFIG_PROPERTY_NAME;
import static com.alibaba.boot.dubbo.util.DubboUtils.OVERRIDE_CONFIG_PROPERTY_NAME;
import static com.alibaba.boot.dubbo.util.DubboUtils.filterDubboProperties;

/**
 * {@link DubboUtils} Test
 * @see DubboUtils
 * @since 1.0.0
 */
public class DubboUtilsTest {

    @Test
    public void testConstants() {

        Assert.assertEquals("dubbo.", DUBBO_PREFIX);

        Assert.assertEquals("dubbo.scan.", DUBBO_SCAN_PREFIX);

        Assert.assertEquals("dubbo.scan.base-packages", DUBBO_SCAN_PREFIX + BASE_PACKAGES_PROPERTY_NAME);

        Assert.assertEquals("dubbo.config.", DUBBO_CONFIG_PREFIX);

        Assert.assertEquals("dubbo.config.multiple", DUBBO_CONFIG_PREFIX + MULTIPLE_CONFIG_PROPERTY_NAME);

        Assert.assertEquals("dubbo.config.override", DUBBO_CONFIG_PREFIX + OVERRIDE_CONFIG_PROPERTY_NAME);

        Assert.assertEquals("https://github.com/apache/incubator-dubbo-spring-boot-project", DUBBO_SPRING_BOOT_GITHUB_URL);
        Assert.assertEquals("https://github.com/apache/incubator-dubbo-spring-boot-project.git", DUBBO_SPRING_BOOT_GIT_URL);
        Assert.assertEquals("https://github.com/apache/incubator-dubbo-spring-boot-project/issues", DUBBO_SPRING_BOOT_ISSUES_URL);

        Assert.assertEquals("https://github.com/apache/incubator-dubbo", DUBBO_GITHUB_URL);

        Assert.assertEquals("dev@dubbo.incubator.apache.org", DUBBO_MAILING_LIST);

        Assert.assertFalse(DEFAULT_MULTIPLE_CONFIG_PROPERTY_VALUE);

        Assert.assertTrue(DEFAULT_OVERRIDE_CONFIG_PROPERTY_VALUE);

    }


    @Test
    public void testFilterDubboProperties() {

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("message", "Hello,World");
        environment.setProperty(DUBBO_CONFIG_PREFIX + MULTIPLE_CONFIG_PROPERTY_NAME, "true");
        environment.setProperty(DUBBO_CONFIG_PREFIX + OVERRIDE_CONFIG_PROPERTY_NAME, "true");

        SortedMap<String, Object> dubboProperties = filterDubboProperties(environment);

        Assert.assertEquals("true", dubboProperties.get(DUBBO_CONFIG_PREFIX + MULTIPLE_CONFIG_PROPERTY_NAME));
        Assert.assertEquals("true", dubboProperties.get(DUBBO_CONFIG_PREFIX + OVERRIDE_CONFIG_PROPERTY_NAME));

    }

}
