package com.healthcare.skindetector.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.healthcare.skindetector.models.Diagnosis;
import com.healthcare.skindetector.models.Patient;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/skin_disease_db";
    private static final String DB_USER = "root";     // Your username
    private static final String DB_PASSWORD = "root"; // Your password

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeConnection();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to database.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            String createPatients = "CREATE TABLE IF NOT EXISTS patients (" +
                    "patient_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT, " +
                    "phone VARCHAR(15), " +
                    "email VARCHAR(100), " +
                    "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            stmt.execute(createPatients);

            String createDiagnoses = "CREATE TABLE IF NOT EXISTS diagnoses (" +
                    "diagnosis_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "patient_id INT, " +
                    "disease_prediction VARCHAR(200), " +
                    "confidence_score DECIMAL(5,4), " +
                    "analysis_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "image_path VARCHAR(500), " +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id)" +
                    ")";

            stmt.execute(createDiagnoses);

            System.out.println("Tables created or verified.");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables: " + e.getMessage(), e);
        }
    }

    public int savePatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, age, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.setString(3, patient.getPhone());
            stmt.setString(4, patient.getEmail());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("No generated key returned.");
            }
        }
    }

    public void saveDiagnosis(Diagnosis diagnosis) throws SQLException {
        String sql = "INSERT INTO diagnoses (patient_id, disease_prediction, confidence_score, image_path) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, diagnosis.getPatientId());
            stmt.setString(2, diagnosis.getDiseasePrediction());
            stmt.setDouble(3, diagnosis.getConfidenceScore());
            stmt.setString(4, diagnosis.getImagePath());
            stmt.executeUpdate();
        }
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY created_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Patient p = new Patient();
                p.setPatientId(rs.getInt("patient_id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                p.setPhone(rs.getString("phone"));
                p.setEmail(rs.getString("email"));
                patients.add(p);
            }
        }
        return patients;
    }

    public List<Diagnosis> getDiagnosesByPatient(int patientId) throws SQLException {
        List<Diagnosis> diagnoses = new ArrayList<>();
        String sql = "SELECT * FROM diagnoses WHERE patient_id = ? ORDER BY analysis_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Diagnosis d = new Diagnosis();
                    d.setDiagnosisId(rs.getInt("diagnosis_id"));
                    d.setPatientId(rs.getInt("patient_id"));
                    d.setDiseasePrediction(rs.getString("disease_prediction"));
                    d.setConfidenceScore(rs.getDouble("confidence_score"));
                    d.setImagePath(rs.getString("image_path"));
                    diagnoses.add(d);
                }
            }
        }
        return diagnoses;
    }
}
