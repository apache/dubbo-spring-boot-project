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

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.context.annotation.DubboConfigConfiguration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.apache.dubbo.spring.boot.util.DubboUtils.BASE_PACKAGES_PROPERTY_NAME;
import static org.apache.dubbo.spring.boot.util.DubboUtils.BASE_PACKAGES_PROPERTY_RESOLVER_BEAN_NAME;
import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_CONFIG_PREFIX;
import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_PREFIX;
import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_SCAN_PREFIX;
import static org.apache.dubbo.spring.boot.util.DubboUtils.MULTIPLE_CONFIG_PROPERTY_NAME;

/**
 * Dubbo Auto {@link Configuration}
 *
 * @see Reference
 * @see Service
 * @see ServiceAnnotationBeanPostProcessor
 * @see ReferenceAnnotationBeanPostProcessor
 * @since 2.7.0
 */
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true)
@Configuration
@AutoConfigureAfter(DubboRelaxedBindingAutoConfiguration.class)
@EnableConfigurationProperties(DubboConfigurationProperties.class)
public class DubboAutoConfiguration {

    /**
     * Creates {@link ServiceAnnotationBeanPostProcessor} Bean
     *
     * @param propertyResolver {@link PropertyResolver} Bean
     * @return {@link ServiceAnnotationBeanPostProcessor}
     */
    @ConditionalOnProperty(prefix = DUBBO_SCAN_PREFIX, name = BASE_PACKAGES_PROPERTY_NAME)
    @ConditionalOnBean(name = BASE_PACKAGES_PROPERTY_RESOLVER_BEAN_NAME)
    @Bean
    public ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor(
            @Qualifier(BASE_PACKAGES_PROPERTY_RESOLVER_BEAN_NAME) PropertyResolver propertyResolver) {
        Set<String> packagesToScan = propertyResolver.getProperty(BASE_PACKAGES_PROPERTY_NAME, Set.class, emptySet());
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

    /**
     * Single Dubbo Config Configuration
     *
     * @see EnableDubboConfig
     * @see DubboConfigConfiguration.Single
     */
    @Import(DubboConfigConfiguration.Single.class)
    protected static class SingleDubboConfigConfiguration {
    }

    /**
     * Multiple Dubbo Config Configuration , equals @EnableDubboConfig.multiple() == <code>true</code>
     *
     * @see EnableDubboConfig
     * @see DubboConfigConfiguration.Multiple
     */
    @ConditionalOnProperty(prefix = DUBBO_CONFIG_PREFIX, name = MULTIPLE_CONFIG_PROPERTY_NAME, matchIfMissing = true)
    @Import(DubboConfigConfiguration.Multiple.class)
    protected static class MultipleDubboConfigConfiguration {
    }

    /**
     * Build a primary {@link PropertyResolver} bean to {@link Autowired @Autowired}
     *
     * @param environment {@link Environment}
     * @return alias bean for {@link Environment}
     * @since 2.7.3
     */
    @Bean
    @Primary
    public PropertyResolver primaryPropertyResolver(Environment environment) {
        return environment;
    }
}
