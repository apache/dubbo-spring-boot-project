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
package org.apache.dubbo.spring.boot.actuate.autoconfigure;

import org.apache.dubbo.spring.boot.actuate.endpoint.DubboConfigsMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboPropertiesEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboReferencesMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboServicesMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboShutdownEndpoint;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Dubbo {@link Endpoint} Auto-{@link Configuration}
 *
 * @see Endpoint
 * @see Configuration
 * @since 0.2.0
 */
@Configuration
@PropertySource(
        name = "Dubbo Endpoints Default Properties",
        value = "classpath:/META-INF/dubbo-endpoins-default.properties")
public class DubboEndpointsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public DubboEndpoint dubboEndpoint() {
        return new DubboEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public DubboConfigsMetadataEndpoint dubboConfigsMetadataEndpoint() {
        return new DubboConfigsMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public DubboPropertiesEndpoint dubboPropertiesEndpoint() {
        return new DubboPropertiesEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public DubboReferencesMetadataEndpoint dubboReferencesMetadataEndpoint() {
        return new DubboReferencesMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public DubboServicesMetadataEndpoint dubboServicesMetadataEndpoint() {
        return new DubboServicesMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public DubboShutdownEndpoint dubboShutdownEndpoint() {
        return new DubboShutdownEndpoint();
    }

}
