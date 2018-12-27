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
package com.alibaba.boot.dubbo.util;

import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The utilities class for Dubbo
 *
 * @since 1.0.0
 */
public abstract class DubboUtils {

    /**
     * line separator
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");


    /**
     * The separator of property name
     */
    public static final String PROPERTY_NAME_SEPARATOR = ".";

    /**
     * The prefix of property name of Dubbo
     */
    public static final String DUBBO_PREFIX = "dubbo" + PROPERTY_NAME_SEPARATOR;

    /**
     * The prefix of property name for Dubbo scan
     */
    public static final String DUBBO_SCAN_PREFIX = DUBBO_PREFIX + "scan" + PROPERTY_NAME_SEPARATOR;

    /**
     * The prefix of property name for Dubbo Config.ØØ
     */
    public static final String DUBBO_CONFIG_PREFIX = DUBBO_PREFIX + "config" + PROPERTY_NAME_SEPARATOR;

    /**
     * The property name of base packages to scan
     * <p>
     * The default value is empty set.
     */
    public static final String BASE_PACKAGES_PROPERTY_NAME = "base-packages";

    /**
     * The property name of multiple properties binding from externalized configuration
     * <p>
     * The default value is {@link #DEFAULT_MULTIPLE_CONFIG_PROPERTY_VALUE}
     */
    public static final String MULTIPLE_CONFIG_PROPERTY_NAME = "multiple";

    /**
     * The default value of multiple properties binding from externalized configuration
     */
    public static final boolean DEFAULT_MULTIPLE_CONFIG_PROPERTY_VALUE = false;

    /**
     * The property name of override Dubbo config
     * <p>
     * The default value is {@link #DEFAULT_OVERRIDE_CONFIG_PROPERTY_VALUE}
     */
    public static final String OVERRIDE_CONFIG_PROPERTY_NAME = "override";

    /**
     * The default property value of  override Dubbo config
     */
    public static final boolean DEFAULT_OVERRIDE_CONFIG_PROPERTY_VALUE = true;


    /**
     * The github URL of Dubbo Spring Boot
     */
    public static final String DUBBO_SPRING_BOOT_GITHUB_URL = "https://github.com/apache/incubator-dubbo-spring-boot-project";

    /**
     * The git URL of Dubbo Spring Boot
     */
    public static final String DUBBO_SPRING_BOOT_GIT_URL = "https://github.com/apache/incubator-dubbo-spring-boot-project.git";

    /**
     * The issues of Dubbo Spring Boot
     */
    public static final String DUBBO_SPRING_BOOT_ISSUES_URL = "https://github.com/apache/incubator-dubbo-spring-boot-project/issues";

    /**
     * The github URL of Dubbo
     */
    public static final String DUBBO_GITHUB_URL = "https://github.com/apache/incubator-dubbo";

    /**
     * The dubbo mailing List
     */
    public static final String DUBBO_MAILING_LIST = "dev@dubbo.incubator.apache.org";

    /**
     * Filters Dubbo Properties from {@link ConfigurableEnvironment}
     *
     * @param environment {@link ConfigurableEnvironment}
     * @return Read-only SortedMap
     */
    public static SortedMap<String, Object> filterDubboProperties(ConfigurableEnvironment environment) {

        SortedMap<String, Object> dubboProperties = new TreeMap<>();

        Map<String, Object> properties = EnvironmentUtils.extractProperties(environment);

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyName = entry.getKey();

            if (propertyName.startsWith(DUBBO_PREFIX)) {
                dubboProperties.put(propertyName, entry.getValue());
            }

        }

        return Collections.unmodifiableSortedMap(dubboProperties);

    }

}
