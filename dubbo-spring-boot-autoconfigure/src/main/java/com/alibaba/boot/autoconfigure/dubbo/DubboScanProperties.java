package com.alibaba.boot.autoconfigure.dubbo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.alibaba.boot.autoconfigure.dubbo.DubboAutoConfiguration.PREFIX_PROPERTY_NAME;
import static com.alibaba.boot.autoconfigure.dubbo.DubboScanProperties.PREFIX;


/**
 * Dubbo Scan {@link ConfigurationProperties Properties} with prefix "dubbo.scan"
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = PREFIX)
public class DubboScanProperties {

    /**
     * The prefix of property name for Dubbo scan
     */
    public static final String PREFIX = PREFIX_PROPERTY_NAME + "." + "scan";

    /**
     * The packages to scan , the multiple-value is delimited by comma
     *
     * @see EnableDubbo#scanBasePackages()
     */
    private Set<String> packages = new LinkedHashSet<>();

    public Set<String> getPackages() {
        return packages;
    }

    public void setPackages(Set<String> packages) {
        this.packages = packages;
    }

}
