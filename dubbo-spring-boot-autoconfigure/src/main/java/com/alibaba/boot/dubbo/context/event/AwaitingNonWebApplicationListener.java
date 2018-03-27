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
package com.alibaba.boot.dubbo.context.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Awaiting Non-Web Spring Boot {@link ApplicationListener}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.1.1
 */
public class AwaitingNonWebApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AwaitingNonWebApplicationListener.class);

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final AtomicBoolean shutdownHookRegistered = new AtomicBoolean(false);

    private static final AtomicBoolean awaited = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        final SpringApplication springApplication = event.getSpringApplication();

        if (!WebApplicationType.NONE.equals(springApplication.getWebApplicationType())) {
            return;
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                synchronized (springApplication) {
                    if (logger.isInfoEnabled()) {
                        logger.info(" [Dubbo] Current Spring Boot Application is await...");
                    }
                    while (!awaited.get()) {
                        try {
                            springApplication.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });

        // register ShutdownHook
        if (shutdownHookRegistered.compareAndSet(false, true)) {
            registerShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (springApplication) {
                        if (awaited.compareAndSet(false, true)) {
                            springApplication.notifyAll();
                            if (logger.isInfoEnabled()) {
                                logger.info(" [Dubbo] Current Spring Boot Application is about to shutdown...");
                            }
                            // Shutdown executorService
                            executorService.shutdown();
                        }
                    }
                }
            }));
        }
    }

    private void registerShutdownHook(Thread thread) {
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
