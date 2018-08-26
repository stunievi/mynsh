package p;
/*-
 * #%L
 * actframework app demo - hello world
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

import act.Act;
import act.handler.NonBlock;
import act.inject.DefaultValue;
import act.job.OnAppStart;
import act.util.Output;
import act.view.NoImplicitTemplateVariable;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.SessionFree;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * The simple hello world app.
 * <p>Run this app, try to update some of the code, then
 * press F5 in the browser to watch the immediate change
 * in the browser!</p>
 */
@SuppressWarnings("unused")
@ImportResource("dubbo-consumer.xml")
@Configuration
public class HelloWorldApp2 {

    @GetAction
    //@NonBlock
    //@NoImplicitTemplateVariable
    //@SessionFree
    public void home(@DefaultValue("World") @Output String who) {
    }

    @GetAction("/txt")
    //@NonBlock
    //@NoImplicitTemplateVariable
    //@SessionFree
    public String text() {
        return "Hello World";
    }

    public static void main(String[] args) throws Exception {
        Act.start();
    }

    @OnAppStart
    public void onStart(){
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//        ctx.scan("p");
//        ctx.refresh();
    }

}
