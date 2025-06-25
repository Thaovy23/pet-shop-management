import view.frame.LoginFrame;
import view.frame.SignupFrame;

import javax.swing.SwingUtilities;

/**
 * Test class to verify Sign Up functionality
 */
public class TestSignup {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Testing Pet Shop Management Sign Up...");
            
            // Test 1: Open Login Frame
            System.out.println("1. Opening Login Frame...");
            new LoginFrame();
            
            // Test 2: You can also directly open Signup Frame
            System.out.println("2. You can also open SignUp directly by uncommenting the line below:");
            System.out.println("   // new SignupFrame();");
            
            System.out.println("\n=== HƯỚNG DẪN SỬ DỤNG ===");
            System.out.println("1. Trong màn hình Login, bạn sẽ thấy:");
            System.out.println("   - Nút 'SIGN IN' màu xanh");
            System.out.println("   - Dòng 'OR' ở giữa");
            System.out.println("   - Nút 'Create New Account' màu xanh lá");
            System.out.println("\n2. Click vào nút 'Create New Account' để mở Sign Up");
            System.out.println("\n3. Trong Sign Up form, điền đầy đủ thông tin:");
            System.out.println("   - Full Name");
            System.out.println("   - Email Address");
            System.out.println("   - Phone Number");
            System.out.println("   - Username");
            System.out.println("   - Password (ít nhất 6 ký tự)");
            System.out.println("   - Confirm Password");
            System.out.println("\n4. Click 'CREATE ACCOUNT' để tạo tài khoản");
            System.out.println("\n5. Sau khi tạo thành công, bạn sẽ quay lại màn hình Login");
        });
    }
} 