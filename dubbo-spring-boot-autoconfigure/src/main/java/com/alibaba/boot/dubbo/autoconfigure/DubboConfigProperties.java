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
package com.alibaba.boot.dubbo.autoconfigure;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.alibaba.boot.dubbo.util.DubboUtils.*;


/**
 * Dubbo Config {@link ConfigurationProperties Properties} with prefix "dubbo.config"
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = DUBBO_CONFIG_PREFIX)
public class DubboConfigProperties {

    /**
     * Indicates multiple properties binding from externalized configuration or not.
     */
    private boolean multiple = DEFAULT_MULTIPLE_CONFIG_PROPERTY_VALUE;

    /**
     * Indicates override {@link ConfigUtils#getProperties() Dubbo config properties} from externalized configuration
     * or not.
     */
    private boolean override = DEFAULT_OVERRIDE_CONFIG_PROPERTY_VALUE;

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }
}
