package com.example.chatsy;

import android.util.Log;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class AccessToken {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"chatsy-android-app\",\n" +
                    "  \"private_key_id\": \"a9d3a4290a128a7b6835897d2909283c3e50adb8\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCv20rt6FEva6ph\\n9G9tapVmXU/JVkel0qQWgH0OK2rlCjh97hMCWpbFUmsdPZL4IbDaZFb6oZUShdfQ\\n8ESdpkmJW2nx34fZUsgbwEUbn1j6soM/DZpJ7fhonmTMFNPeZYwrmwoKMZPgY2mL\\npvbY5FjDNlAluDK3ay4hU6gWyx6NJa82ziV/7lP+mVEg6j6eGKp6L2y4tLbzNVmf\\nq2p1UWWUGwKlovd62VJVofbPnA9geHpIzLK23hEwRph6USdzmpLJl4HoVifRrFBn\\nqDfznnWHnS4PxCGvag+3hoss9VPkbQ8M74wYbIM4G0F7xKRJNvsRt7v7nvn21N0J\\n0sD6yYYHAgMBAAECggEAAU1JIP8JNb0eOPrhSKpb+PirVNFuy0Qd28dVX9vAyr9L\\nQn/FhvuR+lvQHevv9T3EEMbuTNMR9V33BmwlTqAFthMRcxYj6/ZpvzRNd2acsTT8\\nNWUFBarpEigdrI4zSBHCY0LNB8W+Xvj9cAtT8FNOQ9RvvjB40dRwe/0KpuQu+lrh\\nptMnKEVvV/z9hKba04UziXqyd5HmKr82wto5GsV0YSvlosAYx9advo6+qS/S5s9b\\nSqy31LmMyP515oX8dKMzJ1ba0MrHs2TKMonWuT1oCbLrHFC1M5SpScIvcf5QzNNh\\no3hkzYH/t5pKvanSSkOFtR2mlqVsPwpiP85sgqz+AQKBgQDVs7lrIYhY2qNP5rFG\\nxhx0/8f/3E23oPIo119scSLd+UjFCwzeUlNxUD2wbEIMBVw13PipW8xLSrirwgcC\\nhhxFEnSL/kHbPag4wlp66x3Rq6+YMLZO9/3zUDoq0c82KxHc/Y7MRtICQA8nBrqm\\nmJqZx7no2nP99DhJdJoXV/n/AQKBgQDSqfEAgzZcbyQS4FPROWREX172oA75CFYP\\nsrhTEJkUrmPHQ21uC9I5C3cbTUVXNB7ynvS5HPFp/iC2JFK9ziktJiz/+15vmS1q\\ndJkZqfyh1j44+tOnfWgaW/fYzYhRT1lqZ1UX15uuH86exhgqcjV1PgzyS5zd7iIP\\nSRQKYYCNBwKBgQCdllL9NVUecNxNZ4kC78S8YoJRo1uMNhtdErVDeoqDtfh7lZJ/\\nv6XYwZs8JhjuVGWTMsgL263jN87GmeYhblQae2mGcpCN5AHRGUEvs4HhxAFQ8Vr/\\nENEsQ9UhDmcHQuuBU1miOfdTwXlrBgNOJuqazDXECJPQie+X+2xGGpI4AQKBgGCZ\\nlO3LPU49mKkfPNAgJR5FLWKiGruPWwdvyJ4uakPVg+OHw9JGkWufGkrmr/lA6UAQ\\nK+AGqZ0U7yMWnOp0cuVFMDVhqvg5oV3DgcEG9dWwTHAMdKnFE8uiiJBeDzhZzoLA\\nR4DbWYQLCdA8vjAYlvLgNUeV7KTH4c5okEwCUdRZAoGBALS+Qzms5G2Tpu4mcK/M\\nszQRM14RIWcZR508TFD301dEy6ENCjN71jIooHipZCH8M3s239SlnOSgQwDylkNF\\nv7ob/PlKQkUnZDoxuGinAjWsUiiG8qAQoguhc2kfBFUtELW5YwlHm+bKkbPvZavw\\nMDLgo4KoWsgmbCHZSuoiyWTR\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-brm9u@chatsy-android-app.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"103423715762707169151\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-brm9u%40chatsy-android-app.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Lists.
                    newArrayList(Collections.singletonList(firebaseMessagingScope)));

            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch(IOException e) {
            Log.e("error", "" + e.getMessage());
            return null;
        }



    }
}
