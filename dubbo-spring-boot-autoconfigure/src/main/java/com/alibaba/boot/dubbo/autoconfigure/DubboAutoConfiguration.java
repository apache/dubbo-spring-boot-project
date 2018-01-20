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

import com.alibaba.dubbo.config.AbstractConfig;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.config.spring.context.annotation.DubboConfigConfiguration;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Set;

import static com.alibaba.boot.dubbo.util.DubboUtils.*;
import static java.util.Collections.emptySet;

/**
 * Dubbo Auto {@link Configuration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ApplicationConfig
 * @see Service
 * @see Reference
 * @see DubboComponentScan
 * @see EnableDubboConfig
 * @see EnableDubbo
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnClass(AbstractConfig.class)
@EnableConfigurationProperties(value = {DubboScanProperties.class, DubboConfigProperties.class})
public class DubboAutoConfiguration {

    /**
     * Single Dubbo Config Configuration
     *
     * @see EnableDubboConfig
     * @see DubboConfigConfiguration.Single
     */
    @ConditionalOnProperty(name = MULTIPLE_CONFIG_PROPERTY_NAME, havingValue = "false", matchIfMissing = true)
    @EnableDubboConfig
    @EnableConfigurationProperties(SingleDubboConfigBindingProperties.class)
    protected static class SingleDubboConfigConfiguration {
    }

    /**
     * Multiple Dubbo Config Configuration , equals {@link EnableDubboConfig#multiple()} == <code>true</code>
     *
     * @see EnableDubboConfig#multiple()
     * @see DubboConfigConfiguration.Multiple
     */
    @ConditionalOnProperty(name = MULTIPLE_CONFIG_PROPERTY_NAME, havingValue = "true")
    @EnableDubboConfig(multiple = true)
    @EnableConfigurationProperties(MultipleDubboConfigBindingProperties.class)
    protected static class MultipleDubboConfigConfiguration {
    }

    /**
     * Creates {@link ServiceAnnotationBeanPostProcessor} Bean
     *
     * @return {@link ServiceAnnotationBeanPostProcessor}
     */
    @ConditionalOnProperty(name = BASE_PACKAGES_PROPERTY_NAME)
    @Autowired
    @Bean
    public ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor(Environment environment) {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment);
        Set<String> packagesToScan = resolver.getProperty(BASE_PACKAGES_PROPERTY_NAME, Set.class, emptySet());
        return new ServiceAnnotationBeanPostProcessor(packagesToScan);
    }

    /**
     * Creates {@link ReferenceAnnotationBeanPostProcessor} Bean if Absent
     *
     * @return {@link ReferenceAnnotationBeanPostProcessor}
     */
    @ConditionalOnMissingBean
    @Bean(name = ReferenceAnnotationBeanPostProcessor.BEAN_NAME)
    public ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor() {
        return new ReferenceAnnotationBeanPostProcessor();
    }

}
