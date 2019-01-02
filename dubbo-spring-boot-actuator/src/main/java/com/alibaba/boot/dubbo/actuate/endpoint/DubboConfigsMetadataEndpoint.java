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

import com.alibaba.dubbo.config.AbstractConfig;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

/**
 * Dubbo Configs Metadata {@link Endpoint}
 *
 *
 * @since 0.2.0
 */
@Endpoint(id = "dubboconfigs")
public class DubboConfigsMetadataEndpoint extends AbstractDubboEndpoint {

    @ReadOperation
    public Map<String, Map<String, Map<String, Object>>> configs() {

        Map<String, Map<String, Map<String, Object>>> configsMap = new LinkedHashMap<>();

        addDubboConfigBeans(ApplicationConfig.class, configsMap);
        addDubboConfigBeans(ConsumerConfig.class, configsMap);
        addDubboConfigBeans(MethodConfig.class, configsMap);
        addDubboConfigBeans(ModuleConfig.class, configsMap);
        addDubboConfigBeans(MonitorConfig.class, configsMap);
        addDubboConfigBeans(ProtocolConfig.class, configsMap);
        addDubboConfigBeans(ProviderConfig.class, configsMap);
        addDubboConfigBeans(ReferenceConfig.class, configsMap);
        addDubboConfigBeans(RegistryConfig.class, configsMap);
        addDubboConfigBeans(ServiceConfig.class, configsMap);

        return configsMap;

    }

    private void addDubboConfigBeans(Class<? extends AbstractConfig> dubboConfigClass,
                                     Map<String, Map<String, Map<String, Object>>> configsMap) {

        Map<String, ? extends AbstractConfig> dubboConfigBeans = beansOfTypeIncludingAncestors(applicationContext, dubboConfigClass);

        String name = dubboConfigClass.getSimpleName();

        Map<String, Map<String, Object>> beansMetadata = new TreeMap<>();

        for (Map.Entry<String, ? extends AbstractConfig> entry : dubboConfigBeans.entrySet()) {

            String beanName = entry.getKey();
            AbstractConfig configBean = entry.getValue();
            Map<String, Object> configBeanMeta = resolveBeanMetadata(configBean);
            beansMetadata.put(beanName, configBeanMeta);

        }

        configsMap.put(name, beansMetadata);

    }
}
