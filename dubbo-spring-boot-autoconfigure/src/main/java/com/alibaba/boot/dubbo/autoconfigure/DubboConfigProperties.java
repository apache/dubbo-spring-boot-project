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

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.alibaba.boot.dubbo.autoconfigure.DubboConfigProperties.PREFIX;


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
    public static final String PREFIX = DubboAutoConfiguration.PREFIX_PROPERTY_NAME + "." + "config";

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
