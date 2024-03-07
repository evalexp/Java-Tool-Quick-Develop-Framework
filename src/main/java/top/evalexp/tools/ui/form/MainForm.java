package top.evalexp.tools.ui.form;

import top.evalexp.tools.common.util.Pair;
import top.evalexp.tools.common.util.ResourceUtil;
import top.evalexp.tools.common.util.ZipUtil;
import top.evalexp.tools.entity.plugin.Manifest;
import top.evalexp.tools.ui.GUI;
import top.evalexp.tools.ui.form.model.PluginTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainForm extends JFrame {
    private JTabbedPane tabbedPane;
    private Manifest currentSelect = null;
    private int currentTabIndex = -1;
    private final Map<String, Pair<Integer, PluginPanel>> pluginPanes = new HashMap();

    public MainForm() {
        super();
        // initial size
        this.setSize(900, 600);
        GUI.setHeight(600);
        this.setVisible(true);
        // display on the center
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(ResourceUtil.getResource("images/icon/logo.png")).getImage());
        this.setTitle("JTQDF");
        // prevent window close
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            /**
             * show close confirmation then exit
             * @param event the event to be processed
             */
            @Override
            public void windowClosing(WindowEvent event) {
                int option = JOptionPane.showConfirmDialog(MainForm.this, "Sure to exit?", "Notice", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    GUI.getExecutorService().shutdown();
                    try {
                        GUI.getExecutorService().awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                    } catch (InterruptedException e){
                        System.err.println("[!] Error: InterruptedException");
                        System.exit(-1);
                    }
                    MainForm.this.dispose();
                    System.exit(0);
                }
            }
        });
        // bind size to GUI
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                MainForm.this.pluginPanes.forEach((key, value) -> {
                    value.value().adjustSize();
                    GUI.setHeight(MainForm.this.getSize().height);
                });
                SwingUtilities.updateComponentTreeUI(MainForm.this);
            }
        });
        // setup panel
        this.setupUI();
    }

    /**
     * setup MainForm tab pane UI
     */
    private void setupUI() {
        // add JMenu
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu settings = new JMenu("Settings");
        // set theme menu in settings
        JMenu theme = new JMenu("Theme");
        menuBar.add(settings);
        settings.add(theme);
        String[] themes = new String[] {"FlatLight", "FlatDarcula", "FlatIntelliJ", "FlatDark", "FlatMacDark", "FlatMacLight"};
        for (String t: themes) {
            JMenuItem item = makeThemesMenuItem(t);
            theme.add(item);
        }
        // add JTabbedPane
        this.tabbedPane = new JTabbedPane();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(this.tabbedPane);
        // use JTable to show plugin list
        PluginTableModel pluginTableModel = new PluginTableModel();
        JTable table = new JTable(pluginTableModel);
        // make table scrollable
        JScrollPane pluginPane = new JScrollPane(table);
        this.tabbedPane.addTab("Plugins", pluginPane);
        // set selected
        table.getSelectionModel().addListSelectionListener((event) -> {
            MainForm.this.currentSelect = pluginTableModel.getManifest(table.getSelectedRow());
        });
        // format table size
        this.setColumnLength(table.getColumnModel().getColumn(0), 200, 300);
        this.setColumnLength(table.getColumnModel().getColumn(1), 50, 100);
        this.setColumnLength(table.getColumnModel().getColumn(2), 50);
        // set popup menu
        JPopupMenu popupMenu = this.generateTabPopupMenu();
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                    int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                    if (tabIndex >= 0)  MainForm.this.currentTabIndex = tabIndex;
                    if (tabIndex > 0) popupMenu.show(tabbedPane, e.getX(), e.getY());
                }
            }
        });
        // double click table item, then load plugin and render it
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    GUI.getExecutorService().submit(() -> {
                        // plugin has been loaded, switch tab to it
                        if (MainForm.this.pluginPanes.containsKey(MainForm.this.currentSelect.getName()))
                            tabbedPane.setSelectedIndex(MainForm.this.pluginPanes.get(MainForm.this.currentSelect.getName()).key());
                        else {
                            // plugin not loaded, try to load it
                            try {
                                // use PluginPanel to load plugin and render ui
                                PluginPanel pluginPanel = new PluginPanel(MainForm.this.currentSelect, MainForm.this);
                                MainForm.this.pluginPanes.put(MainForm.this.currentSelect.getName(), new Pair<>(tabbedPane.getTabCount(), pluginPanel));
                                tabbedPane.addTab(MainForm.this.currentSelect.getName(), new JScrollPane(pluginPanel));
                                // switch to new tab
                                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                            } catch (IOException ioException) {
                                JOptionPane.showMessageDialog(MainForm.this, "Load plugin failed, looks like plugin has been removed.");
                            } catch (ClassNotFoundException classNotFoundException) {
                                JOptionPane.showMessageDialog(MainForm.this, "Load plugin failed, cannot load entry class.");
                            } catch (UnsupportedClassVersionError unsupportedClassVersionError) {
                                JOptionPane.showMessageDialog(MainForm.this, "Load plugin failed, plugin class file version is unsupported.");
                            }
                        }
                    });
                }
            }
        });
        // load plugin list and fill into table model
        GUI.getExecutorService().submit(() -> {
            List<Manifest> manifests = ZipUtil.getPluginList();
            if (manifests == null) return;
            pluginTableModel.addPlugins(manifests);
        });
    }

    /**
     * return theme change jmenuitem
     * @param t theme name
     * @return JMenuItem contains event handler
     */
    private JMenuItem makeThemesMenuItem(String t) {
        JMenuItem item = new JMenuItem(t);
        item.addActionListener(e -> {
            Class<?> lafClass = null;
            try {
                lafClass = Class.forName(String.format("com.formdev.flatlaf.%sLaf", t));
            } catch (ClassNotFoundException classNotFoundException) {
                try {
                    lafClass = Class.forName(String.format("com.formdev.flatlaf.themes.%sLaf", t));
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(MainForm.this, "Change theme failed.");
                }
            }
            if (lafClass != null) {
                try {
                    UIManager.setLookAndFeel((LookAndFeel) lafClass.getConstructor().newInstance());
                    SwingUtilities.updateComponentTreeUI(this);
                } catch (UnsupportedLookAndFeelException | InstantiationException | InvocationTargetException |
                         IllegalAccessException | NoSuchMethodException ex) {
                    JOptionPane.showMessageDialog(MainForm.this, "Change theme failed.");
                }
            }

        });
        return item;
    }

    /**
     * return a popup menu for close tab
     * @return JPopupMenu
     */
    private JPopupMenu generateTabPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(e -> {
            if (MainForm.this.currentTabIndex == -1 || MainForm.this.currentTabIndex == 0) return;
            tabbedPane.remove(MainForm.this.currentTabIndex);
            for (Map.Entry<String, Pair<Integer, PluginPanel>> entry : MainForm.this.pluginPanes.entrySet()) {
                if (entry.getValue().key() == MainForm.this.currentTabIndex) {
                    entry.getValue().value().exit();
                    MainForm.this.pluginPanes.remove(entry.getKey());
                    break;
                }
            }
        });
        popupMenu.add(close);
        return popupMenu;
    }

    /**
     * set table column length
     * @param column table column
     * @param minWidth min width
     * @param maxWidth max width
     */
    public void setColumnLength(TableColumn column, int minWidth, int maxWidth) {
        column.setMaxWidth(maxWidth);
        column.setMinWidth(minWidth);
    }

    /**
     * set table column fix length
     * @param column table column
     * @param fixWidth fix width
     */
    public void setColumnLength(TableColumn column, int fixWidth) {
        this.setColumnLength(column, fixWidth, fixWidth);
    }

}
