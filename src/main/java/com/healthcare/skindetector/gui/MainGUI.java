package com.healthcare.skindetector.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.healthcare.skindetector.database.DatabaseManager;
import com.healthcare.skindetector.models.Diagnosis;
import com.healthcare.skindetector.models.Patient;
import com.healthcare.skindetector.utils.ImageUtils;
import com.healthcare.skindetector.vertexai.VertexAIPredictor;

public class MainGUI extends JFrame {
    
    // Hospital Color Theme
    private static final Color HOSPITAL_BLUE = new Color(0, 123, 167);
    private static final Color HOSPITAL_GREEN = new Color(46, 184, 92);
    private static final Color HOSPITAL_RED = new Color(220, 53, 69);
    private static final Color HOSPITAL_LIGHT_BLUE = new Color(232, 244, 248);
    private static final Color HOSPITAL_WHITE = new Color(255, 255, 255);
    private static final Color HOSPITAL_GRAY = new Color(108, 117, 125);
    private static final Color HOSPITAL_DARK_BLUE = new Color(0, 86, 117);
    
    private JTextField nameField, ageField, phoneField, emailField;
    private JLabel imageLabel;
    private JTextArea resultsArea;
    private JButton uploadButton, analyzeButton, saveButton, clearButton, historyButton;

    private File selectedImageFile;
    private BufferedImage selectedImage;
    private String predictionResult;

    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final VertexAIPredictor predictor = new VertexAIPredictor();

    public MainGUI() {
        setSystemLookAndFeel();
        initializeGUI();
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeGUI() {
        setTitle("🏥 MediScan - AI Dermatology Diagnostic System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(HOSPITAL_LIGHT_BLUE);

        // Top Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(HOSPITAL_LIGHT_BLUE);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel leftPanel = createPatientInfoPanel();
        JPanel centerPanel = createImagePanel();
        JPanel rightPanel = createResultsPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HOSPITAL_BLUE);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Left side - Logo and Title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        JLabel logoLabel = new JLabel("🏥");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        logoLabel.setForeground(HOSPITAL_WHITE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("MediScan Dermatology AI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(HOSPITAL_WHITE);

        JLabel subtitleLabel = new JLabel("Advanced AI-Powered Skin Disease Detection System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 230, 240));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        leftHeader.add(logoLabel);
        leftHeader.add(titlePanel);

        // Right side - System Info
        JPanel rightHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        rightHeader.setOpaque(false);

        JLabel dateLabel = new JLabel("📅 " + LocalDateTime.now().toLocalDate().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(HOSPITAL_WHITE);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel statusLabel = new JLabel("● System Active");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(HOSPITAL_GREEN);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        rightHeader.add(dateLabel);
        rightHeader.add(statusLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createPatientInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBackground(HOSPITAL_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HOSPITAL_BLUE, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("👤 Patient Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(HOSPITAL_DARK_BLUE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(HOSPITAL_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel nameLabel = createFormLabel("Full Name:");
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = createFormField();
        formPanel.add(nameField, gbc);

        // Age
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel ageLabel = createFormLabel("Age:");
        formPanel.add(ageLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        ageField = createFormField();
        formPanel.add(ageField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel phoneLabel = createFormLabel("Phone:");
        formPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        phoneField = createFormField();
        formPanel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel emailLabel = createFormLabel("Email:");
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        emailField = createFormField();
        formPanel.add(emailField, gbc);

        // Info Box
        JPanel infoBox = new JPanel();
        infoBox.setBackground(new Color(255, 243, 205));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel infoLabel = new JLabel("<html><b>ℹ️ Note:</b><br>All patient information is<br>encrypted and HIPAA compliant.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(133, 100, 4));
        infoBox.add(infoLabel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(infoBox, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(HOSPITAL_GRAY);
        return label;
    }

    private JTextField createFormField() {
        JTextField field = new JTextField(18);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(HOSPITAL_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HOSPITAL_GREEN, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("📷 Diagnostic Image Viewer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(HOSPITAL_DARK_BLUE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Image Display Area
        imageLabel = new JLabel("No Image Loaded", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        imageLabel.setForeground(HOSPITAL_GRAY);
        imageLabel.setPreferredSize(new Dimension(500, 400));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(248, 249, 250));
        imageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBorder(null);

        // Upload Button
        uploadButton = createStyledButton("📁 Upload Skin Image", HOSPITAL_BLUE);
        uploadButton.addActionListener(e -> uploadImage());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(uploadButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(400, 0));
        panel.setBackground(HOSPITAL_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HOSPITAL_RED, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("📋 Diagnostic Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(HOSPITAL_DARK_BLUE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Results Text Area
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setText("⏳ Awaiting analysis...\n\nPlease upload an image and click 'Analyze' to generate diagnostic report.");
        resultsArea.setBackground(new Color(248, 249, 250));
        resultsArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        resultsArea.setForeground(HOSPITAL_GRAY);

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));

        // Disclaimer
        JPanel disclaimerBox = new JPanel();
        disclaimerBox.setBackground(new Color(248, 215, 218));
        disclaimerBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HOSPITAL_RED, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel disclaimerLabel = new JLabel("<html><b>⚠️ Medical Disclaimer:</b><br>AI predictions are for reference only.<br>Consult a licensed dermatologist.</html>");
        disclaimerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        disclaimerLabel.setForeground(new Color(114, 28, 36));
        disclaimerBox.add(disclaimerLabel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(disclaimerBox, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(HOSPITAL_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(222, 226, 230)),
            new EmptyBorder(10, 20, 10, 20)
        ));

        analyzeButton = createStyledButton("🔬 Analyze Image", HOSPITAL_GREEN);
        analyzeButton.setEnabled(false);
        analyzeButton.addActionListener(e -> analyzeImage());

        saveButton = createStyledButton("💾 Save Patient Record", HOSPITAL_BLUE);
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveRecord());

        historyButton = createStyledButton("📂 View Patient History", HOSPITAL_GRAY);
        historyButton.addActionListener(e -> new PatientHistoryGUI(this).setVisible(true));

        clearButton = createStyledButton("🗑️ Clear Form", HOSPITAL_RED);
        clearButton.addActionListener(e -> clearAll());

        panel.add(analyzeButton);
        panel.add(saveButton);
        panel.add(historyButton);
        panel.add(clearButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(HOSPITAL_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(220, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover Effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "bmp"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                selectedImage = ImageUtils.loadAndResizeImage(selectedImageFile, 500, 400);
                imageLabel.setIcon(new ImageIcon(selectedImage));
                imageLabel.setText("");
                analyzeButton.setEnabled(true);
                resultsArea.setText("✅ Image loaded successfully!\n\nClick 'Analyze Image' to begin AI diagnostic analysis.");
                resultsArea.setForeground(HOSPITAL_GREEN);
                saveButton.setEnabled(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(), "Image Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void analyzeImage() {
        if (selectedImageFile == null) {
            JOptionPane.showMessageDialog(this, "Please select an image first.", "No Image Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultsArea.setText("🔄 Analyzing image with AI...\n\nPlease wait while our advanced neural network processes the diagnostic image...");
        resultsArea.setForeground(HOSPITAL_BLUE);
        analyzeButton.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return predictor.predictSkinDisease(selectedImageFile);
            }

            @Override
            protected void done() {
                try {
                    predictionResult = get();
                    displayResults(predictionResult);
                    saveButton.setEnabled(true);
                } catch (InterruptedException | ExecutionException e) {
                    resultsArea.setText("❌ Analysis Failed\n\n" + e.getMessage());
                    resultsArea.setForeground(HOSPITAL_RED);
                    JOptionPane.showMessageDialog(MainGUI.this, "Analysis failed: " + e.getMessage(), "Analysis Error", JOptionPane.ERROR_MESSAGE);
                }
                analyzeButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void displayResults(String result) {
        resultsArea.setText(result);
        resultsArea.setForeground(Color.BLACK);
    }

    private String extractPrediction(String result) {
        // Extract disease name from formatted result
        if (result == null) return "Unknown";
        if (result.contains("Detected Condition:")) {
            int start = result.indexOf("Detected Condition:") + 20;
            int end = result.indexOf("\n", start);
            if (end > start) {
                return result.substring(start, end).trim();
            }
        }
        return "Unknown";
    }

    private double extractConfidence(String result) {
        // Extract confidence percentage
        if (result == null) return 0.0;
        if (result.contains("Confidence Level:")) {
            try {
                int start = result.indexOf("Confidence Level:") + 18;
                int end = result.indexOf("%", start);
                if (end > start) {
                    String confStr = result.substring(start, end).trim();
                    return Double.parseDouble(confStr);
                }
            } catch (Exception e) {
                // Fallback
            }
        }
        return 85.0;
    }

    private void saveRecord() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter patient name.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Patient patient = new Patient();
            patient.setName(nameField.getText().trim());
            patient.setAge(ageField.getText().trim().isEmpty() ? 0 : Integer.parseInt(ageField.getText().trim()));
            patient.setPhone(phoneField.getText().trim());
            patient.setEmail(emailField.getText().trim());

            int patientId = dbManager.savePatient(patient);

            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setPatientId(patientId);
            diagnosis.setDiseasePrediction(extractPrediction(predictionResult));
            diagnosis.setConfidenceScore(extractConfidence(predictionResult) / 100.0);
            diagnosis.setImagePath(selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null);

            dbManager.saveDiagnosis(diagnosis);

            JOptionPane.showMessageDialog(this, "✅ Patient record saved successfully!\n\nPatient ID: " + patientId, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAll();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving record: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAll() {
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        emailField.setText("");
        imageLabel.setIcon(null);
        imageLabel.setText("No Image Loaded");
        resultsArea.setText("⏳ Awaiting analysis...\n\nPlease upload an image and click 'Analyze' to generate diagnostic report.");
        resultsArea.setForeground(HOSPITAL_GRAY);
        selectedImageFile = null;
        selectedImage = null;
        predictionResult = null;
        analyzeButton.setEnabled(false);
        saveButton.setEnabled(false);
    }
}
