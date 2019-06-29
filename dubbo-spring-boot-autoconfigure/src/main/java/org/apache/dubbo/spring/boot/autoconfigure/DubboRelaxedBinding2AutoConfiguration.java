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

import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.spring.context.properties.DubboConfigBinder;
import org.apache.dubbo.config.spring.util.PropertySourcesUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.dubbo.spring.boot.util.DubboUtils.BASE_PACKAGES_PROPERTY_NAME;
import static org.apache.dubbo.spring.boot.util.DubboUtils.BASE_PACKAGES_PROPERTY_RESOLVER_BEAN_NAME;
import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_PREFIX;
import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_SCAN_PREFIX;
import static org.apache.dubbo.spring.boot.util.DubboUtils.RELAXED_DUBBO_CONFIG_BINDER_BEAN_NAME;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Dubbo Relaxed Binding Auto-{@link Configuration} for Spring Boot 2.0
 *
 * @see DubboRelaxedBindingAutoConfiguration
 * @since 2.7.0
 */
@Configuration
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.boot.context.properties.bind.Binder")
@AutoConfigureBefore(DubboRelaxedBindingAutoConfiguration.class)
public class DubboRelaxedBinding2AutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean(name = BASE_PACKAGES_PROPERTY_RESOLVER_BEAN_NAME)
    public PropertyResolver dubboScanBasePackagesPropertyResolver(ConfigurableEnvironment environment) {
        ConfigurableEnvironment propertyResolver = new AbstractEnvironment() {
            protected void customizePropertySources(MutablePropertySources propertySources) {
                Map<String, Object> dubboScanProperties = PropertySourcesUtils.getSubProperties(environment, DUBBO_SCAN_PREFIX);
                // get base packages from spring boot when dubbo scan base packages is not set
                if (!dubboScanProperties.containsKey(BASE_PACKAGES_PROPERTY_NAME)) {
                    String properties = getDubboScanPropertiesFromSpringBootAnnotation();
                    if (properties != null) {
                        dubboScanProperties.put(BASE_PACKAGES_PROPERTY_NAME, properties);
                    }
                }
                propertySources.addLast(new MapPropertySource("dubboScanProperties", dubboScanProperties));
            }
        };
        ConfigurationPropertySources.attach(propertyResolver);
        return new DelegatingPropertyResolver(propertyResolver);
    }

    @ConditionalOnMissingBean(name = RELAXED_DUBBO_CONFIG_BINDER_BEAN_NAME, value = DubboConfigBinder.class)
    @Bean(RELAXED_DUBBO_CONFIG_BINDER_BEAN_NAME)
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public DubboConfigBinder relaxedDubboConfigBinder() {
        return new BinderDubboConfigBinder();
    }

    /**
     * get base package from spring boot base package
     *
     * @return base package
     */
    private String getDubboScanPropertiesFromSpringBootAnnotation() {
        Set<String> basePackages = new HashSet<>();
        // get base package from `ComponentScans` annotation
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(ComponentScans.class);
        beansWithAnnotation.forEach((name, instance) -> {
            ComponentScans componentScans = AnnotatedElementUtils.getMergedAnnotation(instance.getClass(), ComponentScans.class);
            if (componentScans != null) {
                Arrays.stream(componentScans.value()).forEach(scan -> {
                    basePackages.addAll(Arrays.stream(scan.basePackageClasses()).map(c -> c.getPackage().getName()).collect(Collectors.toSet()));
                    basePackages.addAll(Arrays.stream(scan.basePackages()).collect(Collectors.toSet()));
                });
            }
        });
        // get base package from `ComponentScan` annotation
        applicationContext.getBeansWithAnnotation(ComponentScan.class).forEach((name, instance) -> {
            AnnotatedElementUtils.getMergedRepeatableAnnotations(instance.getClass(), ComponentScan.class).forEach(scan -> {
                basePackages.addAll(Arrays.stream(scan.basePackageClasses()).map(c -> c.getPackage().getName()).collect(Collectors.toSet()));
                basePackages.addAll(Arrays.stream(scan.basePackages()).collect(Collectors.toSet()));
            });
        });
        // get base package from `SpringBootApplication` annotation
        applicationContext.getBeansWithAnnotation(SpringBootApplication.class).forEach((name, instance) -> {
            basePackages.add(instance.getClass().getPackage().getName());
        });

        if (!CollectionUtils.isEmpty(basePackages)) {
            return StringUtils.join(basePackages, ",");
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
