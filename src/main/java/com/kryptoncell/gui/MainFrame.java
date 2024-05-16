package com.kryptoncell.gui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class MainFrame extends JFrame {

    public MainFrame(@Value("${spring.application.name}") String appName,
                     @Value("${spring.application.author}") String author,
                     @Value("${spring.application.version}") String version) {

        setTitle(appName);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
    }

    public void showUI() {
        this.setVisible(true);
    }
}
