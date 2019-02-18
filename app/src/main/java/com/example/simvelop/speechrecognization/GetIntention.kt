package com.example.simvelop.speechrecognization

import android.util.Log
import com.google.cloud.dialogflow.v2.*
import com.google.protobuf.ByteString

class GetIntention {

    @Throws(Exception::class)
    fun detectIntentAudio(
        projectId: String,
        audioData: ByteArray,
        sessionId: String,
        languageCode: String
    ): QueryResult {


        Log.i("Test::", "Detection of Intent started")
        // Instantiates a client

        SessionsClient.create().use { sessionsClient ->
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            val session = SessionName.of(projectId, sessionId)
            println("Session Path: " + session.toString())

            // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
            // Audio encoding of the audio content sent in the query request.
            val audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16
            val sampleRateHertz = 44100

            // Instructs the speech recognizer how to process the audio content.
            val inputAudioConfig = InputAudioConfig.newBuilder()
                .setAudioEncoding(audioEncoding) // audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16
                .setLanguageCode(languageCode) // languageCode = "en-US"
                .setSampleRateHertz(sampleRateHertz) // sampleRateHertz = 16000
                .build()

            // Build the query with the InputAudioConfig
            val queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build()

            // Read the bytes from the audio file


            //System.out.println("Path is:"+Paths.get(audioFilePath));
            // Build the DetectIntentRequest
            val request = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .setInputAudio(ByteString.copyFrom(audioData))
                .build()

            // Performs the detect intent request
            val response = sessionsClient.detectIntent(request)

            // Display the query result
            val queryResult = response.queryResult
            println("====================")
            System.out.format("Query Text: '%s'\n", queryResult.queryText)
            System.out.format(
                "Detected Intent: %s (confidence: %f)\n",
                queryResult.intent.displayName, queryResult.intentDetectionConfidence
            )
            System.out.format("Fulfillment Text: '%s'\n", queryResult.fulfillmentText)

            return queryResult
        }

    }
}