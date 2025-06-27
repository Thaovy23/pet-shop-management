package util.ui;

import javax.swing.*;
import java.awt.*;

public class ButtonStyler {
    
    public static void styleDialogButton(JButton btn, ButtonType type) {
        // Clear any existing styling first
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        // Common styling for all buttons
        btn.setForeground(Color.BLACK); // Black text for all buttons
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Gray border for all
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 35));
        
        // Type-specific background colors
        switch (type) {
            case SAVE -> btn.setBackground(new Color(0x007BFF)); // Blue for Save/Add/Update
            case CANCEL -> btn.setBackground(Color.LIGHT_GRAY); // Light gray for Cancel
            case DELETE -> btn.setBackground(new Color(220, 53, 69)); // Red for Delete
        }
        
        // Force repaint
        SwingUtilities.invokeLater(() -> {
            btn.repaint();
        });
    }
    
    public static void styleSaveButton(JButton btn) {
        styleDialogButton(btn, ButtonType.SAVE);
    }
    
    public static void styleCancelButton(JButton btn) {
        styleDialogButton(btn, ButtonType.CANCEL);
    }
    
    public static void styleDeleteButton(JButton btn) {
        styleDialogButton(btn, ButtonType.DELETE);
    }
    
    public enum ButtonType {
        SAVE, CANCEL, DELETE
    }
} 