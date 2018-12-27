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
package com.alibaba.boot.dubbo.actuate.autoconfigure;

import com.alibaba.boot.dubbo.actuate.health.DubboHealthIndicator;
import com.alibaba.boot.dubbo.actuate.health.DubboHealthIndicatorProperties;
import com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration;

import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dubbo {@link DubboHealthIndicator} Auto Configuration
 *

 * @see HealthIndicator
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({HealthIndicator.class})
@AutoConfigureBefore({EndpointAutoConfiguration.class})
@AutoConfigureAfter(DubboAutoConfiguration.class)
@ConditionalOnEnabledHealthIndicator("dubbo")
@EnableConfigurationProperties(DubboHealthIndicatorProperties.class)
public class DubboHealthIndicatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DubboHealthIndicator dubboHealthIndicator() {
        return new DubboHealthIndicator();
    }

}
