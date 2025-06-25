import view.frame.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PetshopApp {
    public static void main(String[] args) {
        // Set system look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // If system L&F is not available, continue with default
            System.out.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Start the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🚀 Starting Pet Shop Management System...");
                LoginFrame loginFrame = new LoginFrame();
                System.out.println("✅ LoginFrame created successfully!");
                System.out.println("📱 Window size: " + loginFrame.getSize());
                System.out.println("\n🔍 KIỂM TRA SIGN UP:");
                System.out.println("   1. Tìm nút 'Create New Account' màu xanh lá");
                System.out.println("   2. Nút này nằm dưới dòng '── OR ──'");
                System.out.println("   3. Click vào để mở Sign Up form");
                System.out.println("\n💡 Console sẽ hiển thị debug khi button được tạo...");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Error starting Pet Shop Management System: " + e.getMessage());
            }
        });
    }
}
