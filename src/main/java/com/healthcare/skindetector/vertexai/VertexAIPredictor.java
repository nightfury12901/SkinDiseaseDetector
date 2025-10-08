package com.healthcare.skindetector.vertexai;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

public class VertexAIPredictor {

    // Set your actual Vertex AI details here
    private static final String PROJECT_ID = "ace-bucksaw-469016-v2";
    private static final String LOCATION = "us-central1";
    private static final String ENDPOINT_ID = "7314543984733323264";

    private final PredictionServiceClient predictionServiceClient;
    private final EndpointName endpointName;

    public VertexAIPredictor() {
        try {
            predictionServiceClient = PredictionServiceClient.create();
            endpointName = EndpointName.of(PROJECT_ID, LOCATION, ENDPOINT_ID);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize Vertex AI client: " + e.getMessage(), e);
        }
    }

    public String predictSkinDisease(File imageFile) throws Exception {
        // Read image and encode as base64
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        // Build instance: {"content": "base64_string"}
        Map<String, Value> instanceFields = new HashMap<>();
        instanceFields.put("content", Value.newBuilder().setStringValue(encodedImage).build());
        
        Value instance = Value.newBuilder()
                .setStructValue(Struct.newBuilder().putAllFields(instanceFields))
                .build();

        List<Value> instances = new ArrayList<>();
        instances.add(instance);

        // Build parameters
        Map<String, Value> parametersFields = new HashMap<>();
        parametersFields.put("confidenceThreshold", Value.newBuilder().setNumberValue(0.5).build());
        parametersFields.put("maxPredictions", Value.newBuilder().setNumberValue(5).build());
        
        Value parameters = Value.newBuilder()
                .setStructValue(Struct.newBuilder().putAllFields(parametersFields))
                .build();

        // Build prediction request
        PredictRequest request = PredictRequest.newBuilder()
                .setEndpoint(endpointName.toString())
                .addAllInstances(instances)
                .setParameters(parameters)
                .build();

        // Get prediction response
        PredictResponse response = predictionServiceClient.predict(request);

        // Parse the response properly with safety checks
        StringBuilder result = new StringBuilder();
        result.append("SKIN DISEASE ANALYSIS RESULTS\n");
        result.append("=====================================\n\n");

        if (response.getPredictionsCount() == 0) {
            result.append("⚠️ No predictions returned from the model.\n");
            result.append("Please check if the endpoint is properly deployed.\n");
            return result.toString();
        }

        for (Value prediction : response.getPredictionsList()) {
            if (prediction.hasStructValue()) {
                Struct struct = prediction.getStructValue();
                
                String diseaseName = "Unknown";
                double confidence = 0.0;
                
                // Extract display names (disease names) with safety check
                if (struct.containsFields("displayNames")) {
                    Value displayNames = struct.getFieldsMap().get("displayNames");
                    if (displayNames.hasListValue() && displayNames.getListValue().getValuesCount() > 0) {
                        diseaseName = displayNames.getListValue().getValues(0).getStringValue();
                    }
                }
                
                // Extract confidence scores with safety check
                if (struct.containsFields("confidences")) {
                    Value confidences = struct.getFieldsMap().get("confidences");
                    if (confidences.hasListValue() && confidences.getListValue().getValuesCount() > 0) {
                        confidence = confidences.getListValue().getValues(0).getNumberValue();
                    }
                }
                
                // Only display if we got valid results
                if (!diseaseName.equals("Unknown")) {
                    result.append("🔍 Detected Condition: ").append(diseaseName).append("\n\n");
                    
                    int confidencePercent = (int) Math.round(confidence * 100);
                    result.append("📊 Confidence Level: ").append(confidencePercent).append("%\n\n");
                    
                    // Add confidence interpretation
                    if (confidencePercent >= 90) {
                        result.append("✅ Reliability: Very High\n\n");
                    } else if (confidencePercent >= 75) {
                        result.append("✅ Reliability: High\n\n");
                    } else if (confidencePercent >= 60) {
                        result.append("⚠️ Reliability: Moderate\n\n");
                    } else {
                        result.append("⚠️ Reliability: Low - Consider retaking image\n\n");
                    }
                } else {
                    result.append("⚠️ Unable to extract prediction from response.\n\n");
                    result.append("Raw response:\n").append(prediction.toString()).append("\n\n");
                }
            }
        }
        
        // Add professional disclaimer
        result.append("⚕️ IMPORTANT MEDICAL DISCLAIMER:\n");
        result.append("This AI analysis is for informational purposes only.\n");
        result.append("Please consult with a qualified dermatologist or\n");
        result.append("healthcare professional for proper medical diagnosis\n");
        result.append("and treatment recommendations.\n\n");
        
        result.append("🕒 Analysis Time: ").append(java.time.LocalDateTime.now()).append("\n");
        
        return result.toString();
    }

    public void close() {
        if (predictionServiceClient != null) {
            predictionServiceClient.close();
        }
    }
}
