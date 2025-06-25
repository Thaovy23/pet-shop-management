/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util.ui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;

/**
 *
 * @author ADMIN
 */
public class PasswordFieldUtil{
    
    public static JPanel createPasswordFieldWithToggle(JPasswordField passwordField, String placeholder) 
    {
        passwordField.setText(placeholder);
        
        passwordField.setForeground(Color.GRAY);
        
        passwordField.setEchoChar((char) 0); // Default: show placeholder

        JButton toggleBtn = new JButton("👁"); // or use an icon
        
        toggleBtn.setPreferredSize(new Dimension(50, 40));
        
        toggleBtn.setFocusPainted(false);
        
        toggleBtn.setContentAreaFilled(false);

        JPanel panel = new JPanel(new BorderLayout());
        
        panel.setMaximumSize(new Dimension(300, 40));
        
        panel.setBackground(Color.LIGHT_GRAY);
        
        panel.setBorder(BorderFactory.createEmptyBorder());

        panel.add(passwordField, BorderLayout.CENTER);
        
        panel.add(toggleBtn, BorderLayout.EAST);

        // Track visibility
        final boolean[] isVisible = {false};

        toggleBtn.addActionListener(e -> 
        {
            isVisible[0] = !isVisible[0];
            
            passwordField.setEchoChar(isVisible[0] ? (char) 0 : '•');
        });

        // Placeholder logic
        passwordField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e) 
            {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) 
                {
                    passwordField.setText("");
                    
                    passwordField.setForeground(Color.BLACK);
                    
                    passwordField.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) 
            {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) 
                {
                    passwordField.setText(placeholder);
                    
                    passwordField.setForeground(Color.GRAY);
                    
                    passwordField.setEchoChar((char) 0);
                }
            }
        });

        return panel;
    }

}
