package util.ui;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;

public class ButtonCellRenderer extends JButton implements TableCellRenderer {
    
    public ButtonCellRenderer(String label)
    {
        setText(label);
        
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column)
    {
        return this;
    }
}
