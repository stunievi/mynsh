package com.beeasy.hzdata.command;


import com.beeasy.hzdata.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CheckCommand {

    @Autowired
    CheckService checkService;

    @ShellMethod("clear trigger logs")
    public void clearLogs(){
        checkService.clearTriggerLogs();
    }

    @ShellMethod("check rule1 ~ rule16")
    public void checkRule(
           @ShellOption() String n
    ){
        checkService.checkMany(n);
    }

    @ShellMethod("generate auto-check tasks")
    public void generateTask(){
        checkService.generateAutoTask();
    }

//
//    @ShellMethod("run autocheck task manually")
//    public void task (
//            @ShellOption(help = "clear trigger logs", defaultValue= NO_ARG, valueProvider = ShellOption.NoValueProvider.class) String c,
//            @ShellOption(help = "the number which task you want to check, use comma to split", defaultValue = NO_ARG) String n,
//            @ShellOption(help = "generate auto checked task", defaultValue = NO_ARG, valueProvider = ShellOption.NoValueProvider.class) String g
//            ){
//        if(S.notEqual(c,NO_ARG)){
//            checkService.clearTriggerLogs();
//        }
//        if(S.neq(n,NO_ARG)){
//            checkService.checkMany(n);
//        }
//        if(S.neq(g,NO_ARG)){
//            checkService.generateAutoTask();
//        }
//    }
}
