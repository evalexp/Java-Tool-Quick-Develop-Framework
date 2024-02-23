package top.evalexp.tools.ui.form.model;

import top.evalexp.tools.entity.plugin.Manifest;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PluginTableModel extends AbstractTableModel {
    private String[] columns = new String[] { "Plugin Name", "Author", "Version", "Description" };
    private List<Manifest> manifests = new ArrayList<>();

    public void addPlugin(Manifest manifest) {
        this.manifests.add(manifest);
        this.fireTableDataChanged();
    }

    public void addPlugins(List<Manifest> manifests) {
        for (Manifest manifest : manifests) this.addPlugin(manifest);
    }

    public Manifest getManifest(int row) {
        return manifests.get(row);
    }

    @Override
    public int getRowCount() {
        return manifests.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return manifests.get(rowIndex).getByIndex(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }
}
