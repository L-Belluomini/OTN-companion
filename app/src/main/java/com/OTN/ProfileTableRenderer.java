package com.OTN;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

/** 
 * ProfileTableRenderer is just like TableDemo, except that it
 * explicitly initializes column sizes and it uses a combo box
 * as an editor for the Sport column.
 */
public class ProfileTableRenderer extends JPanel {
    private boolean DEBUG = false;

    final public ProfilesTableDataModel profilesTableData;
    public JTable profilesTable;

    public ProfileTableRenderer() {
        super(new GridLayout(1,0));

        profilesTableData = new ProfilesTableDataModel();
        JTable profilesTable = new JTable(profilesTableData);

        profilesTableData.addProfile("car", "car", "fastest", false, false, true);
        profilesTableData.addProfile("foot", "foot", "fastest", false, false, true);

        profilesTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        profilesTable.setFillsViewportHeight(true);

        //Create the scroll pane and add the profilesTable to it.
        JScrollPane profilesScrollPane = new JScrollPane(profilesTable);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpWeightingColumn(profilesTable, profilesTable.getColumnModel().getColumn(2));

        //Add the scroll pane to this panel.
        add(profilesScrollPane);
    }

    public void setUpWeightingColumn(JTable profilesTable,
                                 TableColumn weightingColumn) {
        //Set up the editor for the weighting cells.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("shortest");
        comboBox.addItem("fastest");
        comboBox.addItem("short_fastest");
        weightingColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        weightingColumn.setCellRenderer(renderer);
    }
}