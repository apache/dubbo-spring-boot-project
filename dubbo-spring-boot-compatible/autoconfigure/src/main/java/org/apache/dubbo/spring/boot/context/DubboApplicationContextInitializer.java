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
package org.apache.dubbo.spring.boot.context;

import org.apache.dubbo.config.AbstractConfig;
import org.apache.dubbo.config.spring.context.config.NamePropertyDefaultValueDubboConfigBeanCustomizer;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

import static org.apache.dubbo.config.spring.context.config.NamePropertyDefaultValueDubboConfigBeanCustomizer.BEAN_NAME;
import static org.apache.dubbo.config.spring.util.BeanRegistrar.registerInfrastructureBean;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

/**
 * Dubbo {@link ApplicationContextInitializer} implementation
 *
 * @see ApplicationContextInitializer
 * @since 2.7.1
 */
public class DubboApplicationContextInitializer implements ApplicationContextInitializer, Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        overrideBeanDefinitions(applicationContext);
    }

    private void overrideBeanDefinitions(ConfigurableApplicationContext applicationContext) {
        applicationContext.addBeanFactoryPostProcessor(new OverrideBeanDefinitionRegistryPostProcessor());
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    /**
     * {@link BeanDefinitionRegistryPostProcessor}
     */
    private static class OverrideBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            registerInfrastructureBean(registry, BEAN_NAME, DubboConfigBeanCustomizer.class);
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }
    }

    /**
     * Current implementation will be replaced {@link NamePropertyDefaultValueDubboConfigBeanCustomizer} in Dubbo 2.7.2
     */
    private static class DubboConfigBeanCustomizer extends NamePropertyDefaultValueDubboConfigBeanCustomizer {
        @Override
        public void customize(String beanName, AbstractConfig dubboConfigBean) {
            boolean valid = isValidPropertyName(dubboConfigBean, beanName);
            if (valid) {
                super.customize(beanName, dubboConfigBean);
            }
        }

        private boolean isValidPropertyName(AbstractConfig dubboConfigBean, String propertyValue) {
            boolean valid = true;
            String propertyName = "name";
            // AbstractConfig.checkName(String,String)
            Method method = findMethod(AbstractConfig.class, "checkName", String.class, String.class);
            try {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (BeanUtils.getPropertyDescriptor(dubboConfigBean.getClass(), propertyName) != null) {
                    invokeMethod(method, null, propertyName, propertyValue);
                }
            } catch (IllegalStateException e) {
                valid = false;
            }

            return valid;
        }
    }
}
