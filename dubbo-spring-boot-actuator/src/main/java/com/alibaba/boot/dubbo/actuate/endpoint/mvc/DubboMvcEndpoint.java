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
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static com.alibaba.boot.dubbo.actuate.endpoint.DubboEndpoint.*;
import static com.alibaba.boot.dubbo.util.DubboUtils.filterDubboProperties;
import static com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor.BEAN_NAME;
import static com.alibaba.dubbo.registry.support.AbstractRegistryFactory.getRegistries;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    public DubboMvcEndpoint(DubboEndpoint dubboEndpoint) {
        super(dubboEndpoint);
    }


    @RequestMapping(value = DUBBO_SHUTDOWN_ENDPOINT_URI, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeferredResult<Map<String, Object>> shutdown() throws Exception {

        DeferredResult<Map<String, Object>> result = new DeferredResult<Map<String, Object>>();

        Map<String, Object> shutdownCountData = new LinkedHashMap<>();

        // registries
        int registriesCount = getRegistries().size();

        // protocols
        int protocolsCount = getProtocolConfigsBeanMap().size();

        ProtocolConfig.destroyAll();
        shutdownCountData.put("registries", registriesCount);
        shutdownCountData.put("protocols", protocolsCount);

        // Service Beans
        Map<String, ServiceBean> serviceBeansMap = getServiceBeansMap();
        if (!serviceBeansMap.isEmpty()) {
            for (ServiceBean<?> serviceBean : serviceBeansMap.values()) {
                serviceBean.destroy();
            }
        }
        shutdownCountData.put("services", serviceBeansMap.size());

        // Reference Beans
        ReferenceAnnotationBeanPostProcessor beanPostProcessor = getReferenceAnnotationBeanPostProcessor();

        int referencesCount = beanPostProcessor.getOrder();

        beanPostProcessor.destroy();

        shutdownCountData.put("references", referencesCount);

        // Set Result to complete
        Map<String, Object> shutdownData = new TreeMap<>();
        shutdownData.put("shutdown.count", shutdownCountData);
        result.setResult(shutdownData);

        return result;

    }

    @RequestMapping(value = DUBBO_CONFIGS_ENDPOINT_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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


    @RequestMapping(value = DUBBO_SERVICES_ENDPOINT_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Map<String, Object>> services() {

        Map<String, ServiceBean> serviceBeansMap = getServiceBeansMap();

        Map<String, Map<String, Object>> servicesMetadata = new LinkedHashMap<>(serviceBeansMap.size());

        for (Map.Entry<String, ServiceBean> entry : serviceBeansMap.entrySet()) {

            String serviceBeanName = entry.getKey();

            ServiceBean<?> serviceBean = entry.getValue();

            Map<String, Object> serviceBeanMetadata = resolveBeanMetadata(serviceBean);

            Object service = resolveServiceBean(serviceBeanName, serviceBean);

            if (service != null) {
                // Add Service implementation class
                serviceBeanMetadata.put("serviceClass", service.getClass().getName());
            }

            servicesMetadata.put(serviceBeanName, serviceBeanMetadata);

        }

        return servicesMetadata;

    }

    @RequestMapping(value = DUBBO_REFERENCES_ENDPOINT_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Map<String, Object>> references() {

        Map<String, Map<String, Object>> referencesMetadata = new LinkedHashMap<>();

        Map<InjectionMetadata.InjectedElement, ReferenceBean<?>> injectedElementReferenceBeanMap
                = resolveInjectedElementReferenceBeanMap();

        for (Map.Entry<InjectionMetadata.InjectedElement, ReferenceBean<?>> entry :
                injectedElementReferenceBeanMap.entrySet()) {

            InjectionMetadata.InjectedElement injectedElement = entry.getKey();

            ReferenceBean<?> referenceBean = entry.getValue();

            Map<String, Object> beanMetadata = resolveBeanMetadata(referenceBean);
            beanMetadata.put("invoker", resolveBeanMetadata(referenceBean.get()));

            referencesMetadata.put(String.valueOf(injectedElement.getMember()), beanMetadata);

        }

        return referencesMetadata;

    }

    @RequestMapping(value = DUBBO_PROPERTIES_ENDPOINT_URI, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SortedMap<String, Object> properties() {

        return filterDubboProperties(environment);

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

    private Map<String, ServiceBean> getServiceBeansMap() {
        return beansOfTypeIncludingAncestors(applicationContext, ServiceBean.class);
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

    private ReferenceAnnotationBeanPostProcessor getReferenceAnnotationBeanPostProcessor() {
        return applicationContext.getBean(BEAN_NAME, ReferenceAnnotationBeanPostProcessor.class);
    }

    /**
     * Resolves the {@link Collection} of {@link InjectionMetadata.InjectedElement} that were annotated by {@link Reference}
     * from all Spring Beans.
     *
     * @return non-null {@link Collection}
     * TODO Reactors ReferenceAnnotationBeanPostProcessor to expose those info
     */
    private Map<InjectionMetadata.InjectedElement, ReferenceBean<?>> resolveInjectedElementReferenceBeanMap() {

        Map<InjectionMetadata.InjectedElement, ReferenceBean<?>> injectedElementReferenceBeanMap = new LinkedHashMap<>();

        final ReferenceAnnotationBeanPostProcessor processor = getReferenceAnnotationBeanPostProcessor();

        ConcurrentMap<String, InjectionMetadata> injectionMetadataCache =
                getFieldValue(processor, "injectionMetadataCache", ConcurrentMap.class);

        ConcurrentMap<String, ReferenceBean<?>> referenceBeansCache =
                getFieldValue(processor, "referenceBeansCache", ConcurrentMap.class);

        for (InjectionMetadata metadata : injectionMetadataCache.values()) {

            Set<InjectionMetadata.InjectedElement> checkedElements =
                    getFieldValue(metadata, "checkedElements", Set.class);

            Collection<InjectionMetadata.InjectedElement> injectedElements =
                    getFieldValue(metadata, "injectedElements", Collection.class);

            Collection<InjectionMetadata.InjectedElement> actualInjectedElements =
                    checkedElements != null ? checkedElements : injectedElements;

            for (InjectionMetadata.InjectedElement injectedElement : actualInjectedElements) {

                ReferenceBean<?> referenceBean = resolveReferenceBean(injectedElement, referenceBeansCache);

                injectedElementReferenceBeanMap.put(injectedElement, referenceBean);

            }
        }

        return injectedElementReferenceBeanMap;

    }

    private ReferenceBean<?> resolveReferenceBean(InjectionMetadata.InjectedElement injectedElement,
                                                  ConcurrentMap<String, ReferenceBean<?>> referenceBeansCache) {

        // Member is Field or Method annotated @Reference
        Member member = injectedElement.getMember();

        Class<?> beanClass = null;

        Reference reference = getFieldValue(injectedElement, "reference", Reference.class);

        if (member instanceof Field) {

            Field field = (Field) member;

            beanClass = field.getType();

        } else if (member instanceof Method) {

            Method method = (Method) member;

            beanClass = ((Method) member).getReturnType();

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn("What's wrong with Member? Member should not be Field or Method");
            }

            throw new IllegalStateException("What's wrong with Member? Member should not be Field or Method");

        }

        String referenceBeanCacheKey = generateReferenceBeanCacheKey(reference, beanClass);

        return referenceBeansCache.get(referenceBeanCacheKey);

    }


    /**
     * Original implementation :
     *
     * @see ReferenceAnnotationBeanPostProcessor#generateReferenceBeanCacheKey(Reference, java.lang.Class)
     */
    private static String generateReferenceBeanCacheKey(Reference reference, Class<?> beanClass) {

        String interfaceName = resolveInterfaceName(reference, beanClass);

        String key = reference.group() + "/" + interfaceName + ":" + reference.version();

        return key;

    }

    /**
     * Original implementation:
     *
     * @see ReferenceAnnotationBeanPostProcessor#resolveInterfaceName(Reference, java.lang.Class)
     */
    private static String resolveInterfaceName(Reference reference, Class<?> beanClass)
            throws IllegalStateException {

        String interfaceName;
        if (!"".equals(reference.interfaceName())) {
            interfaceName = reference.interfaceName();
        } else if (!void.class.equals(reference.interfaceClass())) {
            interfaceName = reference.interfaceClass().getName();
        } else if (beanClass.isInterface()) {
            interfaceName = beanClass.getName();
        } else {
            throw new IllegalStateException(
                    "The @Reference undefined interfaceClass or interfaceName, and the property type "
                            + beanClass.getName() + " is not a interface.");
        }

        return interfaceName;

    }


    private <T> T getFieldValue(Object object, String fieldName, Class<T> fieldType) {

        Field field = ReflectionUtils.findField(object.getClass(), fieldName, fieldType);

        ReflectionUtils.makeAccessible(field);

        return (T) ReflectionUtils.getField(field, object);

    }

    private Map<String, Object> resolveBeanMetadata(final Object bean) {

        final Map<String, Object> beanMetadata = new LinkedHashMap<>();

        try {

            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

                Method readMethod = propertyDescriptor.getReadMethod();

                if (readMethod != null && isSimpleType(propertyDescriptor.getPropertyType())) {

                    String name = Introspector.decapitalize(propertyDescriptor.getName());
                    Object value = readMethod.invoke(bean);

                    beanMetadata.put(name, value);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return beanMetadata;

    }

    private Object resolveServiceBean(String serviceBeanName, ServiceBean<?> serviceBean) {

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


    private static boolean isSimpleType(Class<?> type) {
        return isPrimitiveOrWrapper(type)
                || type == String.class
                || type == BigDecimal.class
                || type == BigInteger.class
                || type == Date.class
                || type == URL.class
                || type == Class.class
                ;
    }


    private Map<String, ProtocolConfig> getProtocolConfigsBeanMap() {
        return beansOfTypeIncludingAncestors(applicationContext, ProtocolConfig.class);
    }


}
