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

import com.alibaba.boot.dubbo.util.DubboUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static com.alibaba.dubbo.common.Version.getVersion;

/**
 * {@link DubboEndpoint} Test
 *

 * @see DubboEndpoint
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = {
                DubboEndpoint.class
        }
)
public class DubboEndpointTest {


    @Autowired
    private DubboEndpoint dubboEndpoint;

    @Test
    public void testDefaultValue() {

        Assert.assertTrue(dubboEndpoint.isSensitive());
        Assert.assertFalse(dubboEndpoint.isEnabled());
        Assert.assertEquals("dubbo", dubboEndpoint.getId());

    }

    @Test
    public void testInvoke() {

        Map<String, Object> metadata = dubboEndpoint.invoke();

        Assert.assertNotNull(metadata.get("timestamp"));

        Map<String, String> versions = (Map<String, String>) metadata.get("versions");
        Map<String, String> urls = (Map<String, String>) metadata.get("urls");

        Assert.assertFalse(versions.isEmpty());
        Assert.assertFalse(urls.isEmpty());

        Assert.assertEquals(getVersion(DubboUtils.class, "1.0.0"), versions.get("dubbo-spring-boot"));
        Assert.assertEquals(getVersion(), versions.get("dubbo"));

        Assert.assertEquals("https://github.com/apache/incubator-dubbo", urls.get("dubbo"));
        Assert.assertEquals("dev@dubbo.incubator.apache.org", urls.get("mailing-list"));
        Assert.assertEquals("https://github.com/apache/incubator-dubbo-spring-boot-project", urls.get("github"));
        Assert.assertEquals("https://github.com/apache/incubator-dubbo-spring-boot-project/issues", urls.get("issues"));
        Assert.assertEquals("https://github.com/apache/incubator-dubbo-spring-boot-project.git", urls.get("git"));

    }




}
