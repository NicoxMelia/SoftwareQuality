package com.backend.backend.firebase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

@Service // Se inicia apenas arranca el proyecto
@Profile("!k8s")
public class FirebaseInitializer {

    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    private String credentialsJson;

    @PostConstruct
    private void initFirebase() throws IOException {
        if (credentialsJson == null || credentialsJson.isEmpty()) {
            throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS environment variable is not set.");
        }

        InputStream serviceAccount = new ByteArrayInputStream(credentialsJson.getBytes());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://mvc-ecommerce-c56e5.firebaseio.com/")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

    }

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}
