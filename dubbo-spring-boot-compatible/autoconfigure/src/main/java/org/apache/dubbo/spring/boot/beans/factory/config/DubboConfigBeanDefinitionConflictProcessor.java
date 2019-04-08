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
package org.apache.dubbo.spring.boot.beans.factory.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import static org.springframework.context.ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME;

/**
 * Dubbo Config {@link BeanDefinition Bean Definition} {@link BeanDefinitionRegistryPostProcessor processor}
 * to resolve conflict
 *
 * @see BeanDefinition
 * @see BeanDefinitionRegistryPostProcessor
 * @since 2.7.1
 */
public class DubboConfigBeanDefinitionConflictProcessor implements BeanDefinitionRegistryPostProcessor, Ordered {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BeanDefinitionRegistry registry;

    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        resolveUniqueApplicationConfigBean(registry, beanFactory);
    }

    /**
     * Resolve the unique {@link ApplicationConfig} Bean
     *
     * @param registry    {@link BeanDefinitionRegistry} instance
     * @param beanFactory {@link ConfigurableListableBeanFactory} instance
     * @see EnableDubboConfig
     */
    private void resolveUniqueApplicationConfigBean(BeanDefinitionRegistry registry,
                                                    ConfigurableListableBeanFactory beanFactory) {

        this.environment = beanFactory.getBean(ENVIRONMENT_BEAN_NAME, Environment.class);

        String[] beansNames = beanNamesForTypeIncludingAncestors(beanFactory, ApplicationConfig.class);

        if (beansNames.length < 2) { // If the number of ApplicationConfig beans is less than two, return immediately.
            return;
        }

        // Remove ApplicationConfig Beans that are configured by "dubbo.application.*"
        Stream.of(beansNames)
                .filter(this::isConfiguredApplicationConfigBeanName)
                .forEach(registry::removeBeanDefinition);

        beansNames = beanNamesForTypeIncludingAncestors(beanFactory, ApplicationConfig.class);

        if (beansNames.length > 1) {
            throw new IllegalStateException(String.format("There are more than one instances of %s, whose bean definitions : %s",
                    ApplicationConfig.class.getSimpleName(),
                    Stream.of(beansNames)
                            .map(registry::getBeanDefinition)
                            .collect(Collectors.toList()))
            );
        }
    }

    private boolean isConfiguredApplicationConfigBeanName(String beanName) {
        boolean removed = BeanFactoryUtils.isGeneratedBeanName(beanName)
                // Dubbo ApplicationConfig id as bean name
                || Objects.equals(beanName, environment.getProperty("dubbo.application.id"));

        if (removed) {
            if (logger.isWarnEnabled()) {
                logger.warn("The {} bean [ name : {} ] has been removed!", ApplicationConfig.class.getSimpleName(), beanName);
            }
        }

        return removed;
    }


    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
