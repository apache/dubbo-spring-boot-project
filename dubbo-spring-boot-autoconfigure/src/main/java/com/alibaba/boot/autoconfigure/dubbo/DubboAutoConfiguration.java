package com.alibaba.boot.autoconfigure.dubbo;

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

import static com.alibaba.boot.autoconfigure.dubbo.DubboAutoConfiguration.PREFIX_PROPERTY_NAME;
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
@ConditionalOnProperty(prefix = PREFIX_PROPERTY_NAME, name = "enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnClass(AbstractConfig.class)
@EnableConfigurationProperties(value = {DubboScanProperties.class, DubboConfigProperties.class})
public class DubboAutoConfiguration {

    /**
     * The prefix of property name
     */
    public static final String PREFIX_PROPERTY_NAME = "dubbo";

    /**
     * The property name of packages to scan
     * <p>
     * The value is "dubbo.scan.packages"
     */
    public static final String PACKAGES_TO_SCAN_PROPERTY_NAME = DubboScanProperties.PREFIX + "." + "packages";

    /**
     * The property name of Dubbo Config
     * <p>
     * The value is "dubbo.config.multiple"
     */
    public static final String MULTIPLE_CONFIG_PROPERTY_NAME = DubboConfigProperties.PREFIX + "." + "multiple";

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
    @ConditionalOnProperty(name = PACKAGES_TO_SCAN_PROPERTY_NAME)
    @Autowired
    @Bean
    public ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor(Environment environment) {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment);
        Set<String> packagesToScan = resolver.getProperty(PACKAGES_TO_SCAN_PROPERTY_NAME, Set.class, emptySet());
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
