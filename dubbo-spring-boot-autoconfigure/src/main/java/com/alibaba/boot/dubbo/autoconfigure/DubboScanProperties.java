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

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.alibaba.boot.dubbo.util.DubboUtils.DUBBO_SCAN_PREFIX;


/**
 * Dubbo Scan {@link ConfigurationProperties Properties} with prefix "dubbo.scan"
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = DUBBO_SCAN_PREFIX)
public class DubboScanProperties {

    /**
     * The basePackages to scan , the multiple-value is delimited by comma
     *
     * @see EnableDubbo#scanBasePackages()
     */
    private Set<String> basePackages = new LinkedHashSet<>();

    public Set<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(Set<String> basePackages) {
        this.basePackages = basePackages;
    }

}
