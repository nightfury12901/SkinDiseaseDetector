package com.healthcare.skindetector.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.healthcare.skindetector.database.DatabaseManager;
import com.healthcare.skindetector.models.Diagnosis;
import com.healthcare.skindetector.models.Patient;

public class PatientHistoryGUI extends JDialog {
    private JTable patientTable;
    private JTable diagnosisTable;
    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public PatientHistoryGUI(Frame parent) {
        super(parent, "Patient History", true);
        initializeGUI();
        loadPatientData();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());

        String[] patientColumns = {"ID", "Name", "Age", "Phone", "Email"};
        DefaultTableModel patientModel = new DefaultTableModel(patientColumns, 0);
        patientTable = new JTable(patientModel);
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadDiagnosisData();
            }
        });

        String[] diagnosisColumns = {"Date", "Prediction", "Confidence", "Image"};
        DefaultTableModel diagnosisModel = new DefaultTableModel(diagnosisColumns, 0);
        diagnosisTable = new JTable(diagnosisModel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(patientTable),
            new JScrollPane(diagnosisTable));
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(getParent());
    }

    private void loadPatientData() {
        try {
            List<Patient> patients = dbManager.getAllPatients();
            DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
            model.setRowCount(0);
            for (Patient p : patients) {
                model.addRow(new Object[]{
                    p.getPatientId(),
                    p.getName(),
                    p.getAge(),
                    p.getPhone(),
                    p.getEmail()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void loadDiagnosisData() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            int patientId = (Integer) patientTable.getValueAt(row, 0);
            try {
                List<Diagnosis> diagnoses = dbManager.getDiagnosesByPatient(patientId);
                DefaultTableModel model = (DefaultTableModel) diagnosisTable.getModel();
                model.setRowCount(0);
                for (Diagnosis d : diagnoses) {
                    model.addRow(new Object[] {
                        "Recent", d.getDiseasePrediction(),
                        String.format("%.2f%%", d.getConfidenceScore() * 100),
                        d.getImagePath() != null ? "Yes" : "No"
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading diagnoses: " + e.getMessage());
            }
        }
    }
}
