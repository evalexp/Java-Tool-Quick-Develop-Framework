package top.evalexp.tools.ui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import top.evalexp.tools.ui.form.MainForm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static int height = 0;

    public static void setHeight(int height) {
        GUI.height = height;
    }

    public static int getHeight() {
        return height;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public GUI() {
        try {
            // setup flat laf
            FlatMacLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize LAF");
            System.exit(-1);
        }
    }

    public void ui() {
        // show main form
        MainForm form = new MainForm();
    }
}
