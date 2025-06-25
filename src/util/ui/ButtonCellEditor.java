package util.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

public class ButtonCellEditor<T> extends DefaultCellEditor {
    private final JButton button;
    private final JTable table;
    private final Consumer<T> onEdit;
    private final Consumer<T> onDelete;
    private final String action;
    private final RowMapper<T> mapper;

    public ButtonCellEditor(JTable table, String action, RowMapper<T> mapper,
                             Consumer<T> onEdit, Consumer<T> onDelete) {
        super(new JCheckBox());
        this.table = table;
        this.action = action;
        this.mapper = mapper;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        this.button = new JButton();

        switch (action) {
            case "update" -> button.setText("✏️");
            case "delete" -> button.setText("🗑️");
            case "choose" -> button.setText("🛒");
            default -> button.setText("?");
        }

        button.setOpaque(true);
        button.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0 || row >= table.getRowCount()) return;

            T entity = mapper.mapRow((DefaultTableModel) table.getModel(), row);

            switch (action) {
                case "update" -> {
                    if (onEdit != null) onEdit.accept(entity);
                }
                case "delete" -> {
                    if (onDelete != null) onDelete.accept(entity);
                }
                case "choose" -> {
                    if (onEdit != null) onEdit.accept(entity);
                }
                default -> System.err.println("Unsupported action: " + action);
            }

            fireEditingStopped();
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return null; // Không xử lý logic ở đây nữa
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }
}