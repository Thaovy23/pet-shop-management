package view.panel;

import controller.user.UserController;
import model.user.Staff;
import model.user.Manager;
import controller.user.AuthController;
import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import view.dialog.StaffFormDialog;

public class StaffPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private final UserController controller = new UserController();

    public StaffPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Modern light gray
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // === Header Panel ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("Staff Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Manage staff members and their access permissions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // === Top Panel with Controls ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // === Left (Add Button) ===
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JButton addBtn = createModernButton("+ Add Staff", new Color(40, 167, 69), Color.WHITE);
        addBtn.setPreferredSize(new Dimension(140, 42));
        addBtn.setEnabled(AuthController.currentUser instanceof Manager); // Manager-only
        leftPanel.add(addBtn);
        
        topPanel.add(leftPanel, BorderLayout.WEST);

        // === Right (Search) ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);

        searchField = createModernTextField("Search by ID...", 220, 42);
        JButton searchBtn = createModernButton("Search", new Color(0, 123, 255), Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(90, 42));

        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(8));
        filterPanel.add(searchBtn);

        topPanel.add(filterPanel, BorderLayout.EAST);

        // === Modern Table Setup ===
        String[] columns = {"ID", "Name", "Email", "Phone", "Username", "Salary", "Edit", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 6;
            }
        };

        staffTable = new JTable(model);
        setupModernTable(staffTable);

        // Set Update button renderer and editor
        staffTable.getColumn("Edit").setCellRenderer(new ButtonCellRenderer("✏️"));
        staffTable.getColumn("Edit").setCellEditor(new ButtonCellEditor<>(
            staffTable,
            "update",
            (tableModel, row) -> {
                try {
                    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    Staff staff = controller.getAllStaff().stream()
                            .filter(s -> s.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (staff == null) {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có ID " + id);
                    }
                    return staff;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "ID không hợp lệ.");
                    return null;
                }
            },
            staff -> {
                if (staff != null) {
                    new StaffFormDialog(this, staff);
                }
            },
            null
        ));

        // Set Delete button renderer and editor
        staffTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));
        staffTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
            staffTable,
            "delete",
            (tableModel, row) -> {
                try {
                    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    Staff staff = controller.getAllStaff().stream()
                            .filter(s -> s.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (staff == null) {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có ID " + id);
                    }
                    return staff;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "ID không hợp lệ.");
                    return null;
                }
            },
            null,
            staff -> {
                if (staff == null) return;

                if (!(AuthController.currentUser instanceof Manager)) {
                    JOptionPane.showMessageDialog(this, "Chỉ quản lý mới có quyền xóa nhân viên.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa nhân viên ID " + staff.getId() + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = controller.deleteStaff(staff.getId());
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.");
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên (có thể do ràng buộc khóa ngoại).");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên.");
                    }
                }
            }
        ));

        // === Modern Table Container ===
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // === Action Listeners ===
        addBtn.addActionListener(e -> new StaffFormDialog(this, null));

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.equals("Search by ID...") || searchText.isEmpty()) {
                loadStaff();
                return;
            }
            try {
                int id = Integer.parseInt(searchText);
                Staff match = controller.getAllStaff().stream()
                        .filter(s -> s.getId() == id)
                        .findFirst()
                        .orElse(null);
                model.setRowCount(0);
                if (match != null) addStaffToTable(match);
                else JOptionPane.showMessageDialog(this, "No staff found with ID " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.");
            }
        });

        loadStaff();

        // === Final Layout ===
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void loadStaff() {
        model.setRowCount(0);
        controller.getAllStaff().forEach(this::addStaffToTable);
        staffTable.clearSelection();
    }

    private void addStaffToTable(Staff s) {
        model.addRow(new Object[]{
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getPhone(),
                s.getUsername(),
                s.getSalary(),
                "✏️",
                "🗑️"
        });
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Hover effects
        Color hoverColor = bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JTextField createModernTextField(String placeholder, int width, int height) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(width, height));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(new Color(108, 117, 125));
        field.setBackground(Color.WHITE);
        field.setCaretColor(new Color(44, 62, 80));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(44, 62, 80));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(new Color(108, 117, 125));
                    field.setText(placeholder);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private void setupModernTable(JTable table) {
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 247, 255));
        table.setSelectionForeground(new Color(44, 62, 80));
        table.setGridColor(new Color(241, 243, 245));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        
        // Cell renderer for better appearance
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return c;
            }
        };
        
        // Apply to all columns except action columns
        for (int i = 0; i < table.getColumnCount() - 2; i++) {
            table.getColumn(table.getColumnName(i)).setCellRenderer(cellRenderer);
        }
    }

    public void refreshTable() {
        loadStaff();
    }

    public JTable getStaffTable() {
        return staffTable;
    }
}
