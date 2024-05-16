package com.kryptoncell;

import com.kryptoncell.utils.CommandHelper;
import com.kryptoncell.utils.JDBCHelper;

public class Main {

    public static void main(String[] args) {
        try {
            var options = CommandHelper.buildOptions();
            CommandHelper.printHelp(options);

            CommandHelper.parseCommand(options, args);

            System.out.println(JDBCHelper.toPrintString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCHelper.closeConnection();
        }
    }
}