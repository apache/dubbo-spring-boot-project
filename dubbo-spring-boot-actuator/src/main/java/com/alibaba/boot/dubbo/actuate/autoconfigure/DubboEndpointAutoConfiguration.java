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

import com.alibaba.boot.dubbo.actuate.endpoint.DubboEndpoint;
import com.alibaba.dubbo.config.annotation.Service;

import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dubbo {@link Endpoint} Auto Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DubboEndpoint
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({Service.class, Endpoint.class})
public class DubboEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint(value = "dubbo", enabledByDefault = false)
    public DubboEndpoint dubboEndpoint() {
        return new DubboEndpoint();
    }

}
