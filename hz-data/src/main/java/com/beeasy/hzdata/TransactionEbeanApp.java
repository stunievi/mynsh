package com.beeasy.hzdata;

/*-
 * #%L
 * actframework app demo - Transaction (ebean)
 * %%
 * Copyright (C) 2018 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.beeasy.hzdata.utils.Utils;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.beetl.ext.fn.IsNotNullFunction;

/**
 * A Simple Todo application controller
 */
@EnableAsync
@SpringBootApplication
public class TransactionEbeanApp {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(TransactionEbeanApp.class, args);
    }


}
