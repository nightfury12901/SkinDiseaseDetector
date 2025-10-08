package com.healthcare.skindetector;

import com.healthcare.skindetector.gui.MainGUI;
import com.healthcare.skindetector.database.DatabaseManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SkinDiseaseDetectorApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseManager.getInstance().initializeDatabase();

        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
 