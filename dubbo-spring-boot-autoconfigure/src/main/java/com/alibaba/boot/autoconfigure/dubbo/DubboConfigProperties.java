package com.alibaba.boot.autoconfigure.dubbo;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.alibaba.boot.autoconfigure.dubbo.DubboAutoConfiguration.PREFIX_PROPERTY_NAME;
import static com.alibaba.boot.autoconfigure.dubbo.DubboConfigProperties.PREFIX;


/**
 * Dubbo Config {@link ConfigurationProperties Properties} with prefix "dubbo.config"
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = PREFIX)
public class DubboConfigProperties {

    /**
     * The prefix of property name for Dubbo Config.ØØ
     */
    public static final String PREFIX = PREFIX_PROPERTY_NAME + "." + "config";

    /**
     * The property name of Dubbo Config that indicates multiple properties binding or not.
     */
    private boolean multiple = false;

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
}
