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
import com.alibaba.boot.dubbo.actuate.endpoint.mvc.DubboMvcEndpoint;
import com.alibaba.dubbo.config.annotation.Service;

import org.springframework.boot.actuate.autoconfigure.ManagementContextConfiguration;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Dubbo {@link MvcEndpoint} {@link ManagementContextConfiguration}
 *

 * @see ManagementContextConfiguration
 * @since 1.0.0
 */
@ManagementContextConfiguration
@ConditionalOnClass({Service.class, EndpointMvcAdapter.class})
@ConditionalOnWebApplication
public class DubboMvcEndpointManagementContextConfiguration {

    @Bean
    @ConditionalOnBean(DubboEndpoint.class)
    @ConditionalOnMissingBean
    public DubboMvcEndpoint dubboMvcEndpoint(DubboEndpoint dubboEndpoint) {
        return new DubboMvcEndpoint(dubboEndpoint);
    }

}
