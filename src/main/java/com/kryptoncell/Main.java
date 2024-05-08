package com.kryptoncell;

import com.kryptoncell.command.CommandHelper;

public class Main {

    public static void main(String[] args) {

        var options = CommandHelper.buildOptions();
        CommandHelper.printHelp(options);

        var dbInfo = CommandHelper.parseCommand(options, args);

        System.out.println(dbInfo);
    }
}