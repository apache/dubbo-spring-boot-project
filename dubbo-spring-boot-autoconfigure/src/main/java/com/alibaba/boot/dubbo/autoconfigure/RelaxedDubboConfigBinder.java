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

import com.alibaba.dubbo.config.AbstractConfig;
import com.alibaba.dubbo.config.spring.context.properties.AbstractDubboConfigBinder;
import com.alibaba.dubbo.config.spring.context.properties.DubboConfigBinder;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;

import java.util.Map;

import static com.alibaba.spring.util.PropertySourcesUtils.getSubProperties;

/**
 * Spring Boot Relaxed {@link DubboConfigBinder} implementation
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.1.1
 */
public class RelaxedDubboConfigBinder extends AbstractDubboConfigBinder {

    @Override
    public <C extends AbstractConfig> void bind(String prefix, C dubboConfig) {
        RelaxedDataBinder relaxedDataBinder = new RelaxedDataBinder(dubboConfig);
        // Set ignored*
        relaxedDataBinder.setIgnoreInvalidFields(isIgnoreInvalidFields());
        relaxedDataBinder.setIgnoreUnknownFields(isIgnoreUnknownFields());
        // Get properties under specified prefix from PropertySources
        Map<String, Object> properties = getSubProperties(this.getPropertySources(), prefix);
        // Convert Map to MutablePropertyValues
        MutablePropertyValues propertyValues = new MutablePropertyValues(properties);
        // Bind
        relaxedDataBinder.bind(propertyValues);
    }
}
