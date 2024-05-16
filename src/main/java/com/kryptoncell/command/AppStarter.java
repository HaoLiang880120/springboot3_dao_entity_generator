package com.kryptoncell.command;

import com.kryptoncell.gui.MainFrame;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class AppStarter implements CommandLineRunner {

    private final MainFrame mainFrame;

    public AppStarter(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void run(String... args) throws Exception {
        SwingUtilities.invokeLater(this.mainFrame::showUI);
    }
}
