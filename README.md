# 🏥 MediScan - AI Skin Disease Detection System

An intelligent dermatology diagnostic application powered by Google Vertex AI that provides instant skin disease analysis through advanced machine learning models.

![Java](https://img.shields.io


![Maven](https://img.shields.ioods.io/badge/Google-MIT-green [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## 🔍 Overview

MediScan is a comprehensive healthcare application designed to assist in the early detection and diagnosis of skin diseases using artificial intelligence. The system leverages Google's Vertex AI platform to analyze dermatological images and provide accurate predictions with confidence scores, helping healthcare professionals and patients make informed decisions.

**⚠️ Medical Disclaimer:** This application is intended for informational and educational purposes only. It should not be used as a substitute for professional medical advice, diagnosis, or treatment. Always consult with qualified healthcare professionals for medical concerns.

## ✨ Features

- 🤖 **AI-Powered Analysis** - Real-time skin disease detection using trained Vertex AI models
- 🏥 **Professional UI** - Hospital-themed interface designed for medical environments
- 👥 **Patient Management** - Comprehensive patient record system with demographic information
- 📊 **Detailed Reports** - Confidence scores, reliability indicators, and diagnostic timestamps
- 💾 **Database Integration** - Secure MySQL database for patient and diagnosis history
- 📂 **History Tracking** - View complete patient medical history and past diagnoses
- 🔒 **HIPAA Compliant Design** - Patient data encryption and secure storage
- 📷 **Image Preview** - High-quality image viewer for diagnostic images
- 🎨 **Modern Design** - Clean, intuitive interface with responsive layout

## 🛠️ Tech Stack

### Backend
- **Java 17+** - Core application language
- **Maven** - Dependency management and build automation
- **MySQL 8.0** - Relational database for patient records
- **JDBC** - Database connectivity

### AI/ML
- **Google Cloud Vertex AI** - Machine learning model deployment
- **Cloud SDK** - Authentication and API access
- **Prediction Service Client** - Real-time inference

### Frontend
- **Java Swing** - Desktop GUI framework
- **AWT** - Graphics and event handling

### Tools
- **Google Cloud Console** - Cloud resource management
- **Maven Daemon (mvnd)** - Fast build execution
- **Git** - Version control

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- [Java Development Kit (JDK) 17 or higher](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.8+](https://maven.apache.org/download.cgi) or [Maven Daemon](https://github.com/apache/maven-mvnd)
- [MySQL Server 8.0+](https://dev.mysql.com/downloads/mysql/)
- [Google Cloud SDK](https://cloud.google.com/sdk/docs/install)
- Google Cloud account with Vertex AI API enabled

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/nightfury12901/SkinDiseaseDetector.git
cd SkinDiseaseDetector
```

### 2. Set Up MySQL Database

```sql
CREATE DATABASE skin_disease_db;
```

Update database credentials in `DatabaseManager.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/skin_disease_db";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```

### 3. Configure Google Cloud Vertex AI

#### Option 1: Using gcloud CLI (Recommended for Development)

```bash
# Install Google Cloud SDK
# Windows: Download installer from https://cloud.google.com/sdk/docs/install

# Authenticate
gcloud auth application-default login

# Set your project
gcloud config set project YOUR_PROJECT_ID
```

#### Option 2: Using Service Account Key

1. Create a service account in [Google Cloud Console](https://console.cloud.google.com/iam-admin/serviceaccounts)
2. Grant **Vertex AI User** role
3. Create and download JSON key
4. Set environment variable:

```bash
# Windows PowerShell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your-service-account-key.json"

# Linux/Mac
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your-service-account-key.json"
```

### 4. Update Vertex AI Configuration

Edit `VertexAIPredictor.java`:

```java
private static final String PROJECT_ID = "your-project-id";
private static final String LOCATION = "us-central1";
private static final String ENDPOINT_ID = "your-endpoint-id";
```

### 5. Install Dependencies

```bash
mvn clean install
```

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to service account key | `C:\keys\service-account.json` |
| `DB_URL` | MySQL connection string | `jdbc:mysql://localhost:3306/skin_disease_db` |
| `DB_USER` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password123` |

### Application Properties

Key configurations in `VertexAIPredictor.java`:

```java
// Vertex AI Settings
private static final String PROJECT_ID = "your-gcp-project-id";
private static final String LOCATION = "us-central1";  // or your region
private static final String ENDPOINT_ID = "your-vertex-ai-endpoint-id";

// Prediction Parameters
confidenceThreshold: 0.5
maxPredictions: 5
```

## 💻 Usage

### Running the Application

```bash
# Using Maven
mvn exec:java

# Using Maven Daemon (faster)
mvnd exec:java

# Or compile and run
mvn clean compile
java -jar target/skin-disease-detector-1.0.0.jar
```

### Using the Application

1. **Launch Application** - Run the executable and the main window will open
2. **Enter Patient Information** - Fill in patient demographics in the left panel
3. **Upload Image** - Click "Upload Skin Image" and select a dermatological image
4. **Analyze** - Click "Analyze Image" to run AI prediction
5. **Review Results** - View diagnosis in the right panel with confidence scores
6. **Save Record** - Click "Save Patient Record" to store in database
7. **View History** - Click "View Patient History" to see all past records

### Supported Image Formats

- JPEG/JPG
- PNG
- BMP
- GIF

## 📁 Project Structure

```
SkinDiseaseDetector/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/healthcare/skindetector/
│   │   │       ├── gui/
│   │   │       │   ├── MainGUI.java              # Main application window
│   │   │       │   └── PatientHistoryGUI.java    # Patient history viewer
│   │   │       ├── database/
│   │   │       │   └── DatabaseManager.java      # Database operations
│   │   │       ├── models/
│   │   │       │   ├── Patient.java              # Patient model
│   │   │       │   └── Diagnosis.java            # Diagnosis model
│   │   │       ├── vertexai/
│   │   │       │   └── VertexAIPredictor.java    # AI prediction service
│   │   │       ├── utils/
│   │   │       │   └── ImageUtils.java           # Image processing utilities
│   │   │       └── SkinDiseaseDetectorApp.java   # Application entry point
│   │   │
│   │   └── resources/
│   │       └── application.properties            # Configuration file
│   │
│   └── test/
│       └── java/                                  # Unit tests
│
├── pom.xml                                        # Maven configuration
├── .gitignore                                     # Git ignore rules
└── README.md                                      # Project documentation
```

## 📸 Screenshots

### Main Application Window
*Professional hospital-themed interface with patient information, image viewer, and diagnostic results panels*

### Patient History View
*Comprehensive view of patient records and diagnosis history with filtering options*

### AI Analysis Results
*Detailed diagnostic report with confidence scores, reliability indicators, and medical disclaimers*

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please ensure:
- Code follows Java naming conventions
- All tests pass
- Documentation is updated
- No sensitive credentials are committed


## 👨‍💻 Author

**Chinmay Mishra**

- GitHub: [@nightfury12901](https://github.com/nightfury12901)
- Email: cm1372@srmist.edu.in

## 🙏 Acknowledgments

- Google Cloud Platform for Vertex AI services
- Samsung Project Prism collaboration
- SRM Institute of Science and Technology
- Open source community

## 📞 Support

For issues, questions, or suggestions:

- Open an [Issue](https://github.com/nightfury12901/SkinDiseaseDetector/issues)
- Contact: chinmay060606@gmail.com

***

**⚕️ Medical Disclaimer:** This software is provided for educational and informational purposes only. It is not intended to diagnose, treat, cure, or prevent any disease. Always seek the advice of qualified healthcare professionals with questions regarding medical conditions.

**Made with ❤️ for healthcare innovation**

***

Save this as `README.md` in your project root! It's comprehensive, professional, and covers everything someone would need to understand and use your project! 🎉
