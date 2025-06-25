import view.frame.LoginFrame;
import view.frame.SignupFrame;
import javax.swing.*;
import java.awt.*;

public class TestLoginUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== TESTING LOGIN UI ===");
            
            // Test LoginFrame
            LoginFrame loginFrame = new LoginFrame();
            System.out.println("LoginFrame created");
            
            // Print frame info
            System.out.println("Frame size: " + loginFrame.getSize());
            System.out.println("Frame location: " + loginFrame.getLocation());
            System.out.println("Frame visible: " + loginFrame.isVisible());
            
            // Add a timer to test SignupFrame after 3 seconds
            Timer timer = new Timer(3000, e -> {
                System.out.println("\n=== TESTING SIGNUP UI ===");
                try {
                    new SignupFrame();
                    System.out.println(" SignupFrame created and opened");
                } catch (Exception ex) {
                    System.err.println(" Error creating SignupFrame: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            timer.setRepeats(false);
            timer.start();
            
            System.out.println("\n=== HƯỚNG DẪN KIỂM TRA ===");
            System.out.println("1. Màn hình Login sẽ mở ngay bây giờ");
            System.out.println("2. Trong Login form, bạn phải thấy:");
            System.out.println("    Email address field");
            System.out.println("    Password field");
            System.out.println("    SIGN IN button (màu xanh)");
            System.out.println("   ── OR ── (dòng phân cách)");
            System.out.println("    Create New Account button (màu xanh lá)");
            System.out.println("\n3. Sau 3 giây, Sign Up form sẽ mở tự động");
            System.out.println("\n NẾU KHÔNG THẤY NÚT 'Create New Account':");
            System.out.println("   - Thử resize cửa sổ");
            System.out.println("   - Scroll xuống trong form");
            System.out.println("   - Kiểm tra console có error không");
        });
    }
} 