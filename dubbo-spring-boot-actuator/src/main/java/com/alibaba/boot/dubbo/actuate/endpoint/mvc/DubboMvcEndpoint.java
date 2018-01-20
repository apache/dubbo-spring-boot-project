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
package com.alibaba.boot.dubbo.actuate.endpoint.mvc;

import com.alibaba.boot.dubbo.actuate.endpoint.DubboEndpoint;
import com.alibaba.dubbo.config.spring.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.HypermediaDisabled;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.alibaba.boot.dubbo.util.DubboUtils.filterDubboProperties;
import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;
import static org.springframework.util.ClassUtils.isPrimitiveOrWrapper;

/**
 * {@link MvcEndpoint} to expose Dubbo Metadata
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see
 * @since 1.0.0
 */
public class DubboMvcEndpoint extends EndpointMvcAdapter implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    public DubboMvcEndpoint(DubboEndpoint dubboEndpoint) {
        super(dubboEndpoint);
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @HypermediaDisabled
    public Object services() {

        Map<String, ServiceBean> serviceBeansMap = beansOfTypeIncludingAncestors(applicationContext, ServiceBean.class);

        Map<String, Map<String, Object>> servicesMetadata = new LinkedHashMap<>(serviceBeansMap.size());

        for (Map.Entry<String, ServiceBean> entry : serviceBeansMap.entrySet()) {

            String serviceBeanName = entry.getKey();

            ServiceBean serviceBean = entry.getValue();

            Map<String, Object> serviceBeanMetadata = resolveServiceBeanMetadata(serviceBean);

            Object service = resolveServiceBean(serviceBeanName, serviceBean);

            if (service != null) {
                // Add Service implementation class
                serviceBeanMetadata.put("serviceClass", service.getClass().getName());
            }

            servicesMetadata.put(serviceBeanName, serviceBeanMetadata);

        }

        return servicesMetadata;

    }

    private Object resolveServiceBean(String serviceBeanName, ServiceBean serviceBean) {

        int index = serviceBeanName.indexOf("#");

        if (index > -1) {

            Class<?> interfaceClass = serviceBean.getInterfaceClass();

            String serviceName = serviceBeanName.substring(index + 1);

            if (applicationContext.containsBean(serviceName)) {
                return applicationContext.getBean(serviceName, interfaceClass);
            }

        }

        return null;

    }


    @RequestMapping(value = "/properties", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @HypermediaDisabled
    public Object properties() {

        return filterDubboProperties(environment);

    }

    private Map<String, Object> resolveServiceBeanMetadata(final ServiceBean serviceBean) {

        final Map<String, Object> serviceBeanMetadata = new LinkedHashMap<>();

        try {

            BeanInfo beanInfo = Introspector.getBeanInfo(serviceBean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

                Method readMethod = propertyDescriptor.getReadMethod();

                if (readMethod != null && isSimpleType(propertyDescriptor.getPropertyType())) {

                    String name = Introspector.decapitalize(propertyDescriptor.getName());
                    Object value = readMethod.invoke(serviceBean);

                    serviceBeanMetadata.put(name, value);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return serviceBeanMetadata;

    }

    private static boolean isSimpleType(Class<?> type) {
        return isPrimitiveOrWrapper(type)
                || type == String.class
                || type == BigDecimal.class
                || type == BigInteger.class
                || type == Date.class
                ;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            this.environment = (ConfigurableEnvironment) environment;
        }
    }
}
