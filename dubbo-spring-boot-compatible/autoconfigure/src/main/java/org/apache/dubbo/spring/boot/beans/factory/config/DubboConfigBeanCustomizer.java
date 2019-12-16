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

import org.apache.dubbo.config.AbstractConfig;
import org.apache.dubbo.config.spring.context.config.NamePropertyDefaultValueDubboConfigBeanCustomizer;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;

import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;


/**
 * Current implementation will be replaced {@link NamePropertyDefaultValueDubboConfigBeanCustomizer} in Dubbo 2.7.2
 *
 * @since 2.7.1
 * @deprecated
 */
@Deprecated
class DubboConfigBeanCustomizer extends NamePropertyDefaultValueDubboConfigBeanCustomizer {

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
            if (method != null) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if (BeanUtils.getPropertyDescriptor(dubboConfigBean.getClass(), propertyName) != null) {
                    invokeMethod(method, null, propertyName, propertyValue);
                }
            }
        } catch (IllegalStateException e) {
            valid = false;
        }

        return valid;
    }
}
