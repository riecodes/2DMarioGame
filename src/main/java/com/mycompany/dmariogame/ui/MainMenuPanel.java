package com.mycompany.dmariogame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel {
    private JButton startButton;
    private JButton exitButton;

    public MainMenuPanel(ActionListener startListener, ActionListener exitListener) {
        setLayout(new GridBagLayout());
        setBackground(new Color(100, 149, 237));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;

        JLabel title = new JLabel("Zeus");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(title, gbc);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.addActionListener(startListener);
        gbc.gridy = 1;
        add(startButton, gbc);

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.addActionListener(exitListener);
        gbc.gridy = 2;
        add(exitButton, gbc);
    }
} 