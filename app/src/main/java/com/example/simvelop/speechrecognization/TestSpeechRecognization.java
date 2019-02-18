package com.example.simvelop.speechrecognization;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import com.google.cloud.dialogflow.v2.AudioEncoding;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.InputAudioConfig;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;

import com.google.protobuf.ByteString;

import io.grpc.internal.IoUtils;

public class TestSpeechRecognization {


    public static byte[] convertIntoByteArray(String wavFile) {
        byte[] audioBytes = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(wavFile));

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();
            audioBytes = out.toByteArray();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return audioBytes;

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static QueryResult detectIntentAudio(
            String projectId,
            InputStream audioData,
            String sessionId,
            String languageCode, SessionsClient sessionsClient, SessionName session)
            {



        /*Credentials credentials = GoogleCredentials.fromStream(inputJson);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        // Instantiates a client
        String accessToken = "ya29.c.ElquBkxPlubdfT4OVARN_x2DFYrm36stRGt9GEAIOi-zS4RPVYf1NVa3dXiMtF8NH2npvixuTCv0v6GteB2ALs2ysuxe_EhTHIV5_oZizbF7vhcA8YQYQWwcFoM";

        Date expirationTime = new Date()
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.create(new AccessToken(accessToken, expirationTime)))
                .build()
                .getService();*/


        try {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)

            System.out.println("Session Path: " + session.toString());

            // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
            // Audio encoding of the audio content sent in the query request.
            AudioEncoding audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16;
            int sampleRateHertz = 16000;

            // Instructs the speech recognizer how to process the audio content.
            InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
                    .setAudioEncoding(audioEncoding) // audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16
                    .setLanguageCode(languageCode) // languageCode = "en-US"
                    .setSampleRateHertz(sampleRateHertz) // sampleRateHertz = 16000
                    .build();

            // Build the query with the InputAudioConfig
            QueryInput queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();

            // Read the bytes from the audio file
            byte[] inputData = IoUtils.toByteArray(audioData);
            System.out.println("====================");

            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .setInputAudio(ByteString.copyFrom(inputData))
                    .build();



            DetectIntentResponse response = sessionsClient.detectIntent(request);
            System.out.println("====================");
            // Display the query result
            QueryResult queryResult = response.getQueryResult();
            System.out.println("====================");
            System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
            System.out.format("Detected Intent: %s (confidence: %f)\n",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
            System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());

            return queryResult;
        }
        catch(Exception e){
            sessionsClient.shutdownNow();

            Log.i("Exception Occurred", "Here");
            e.printStackTrace();
            return null;
        }


    }


}
