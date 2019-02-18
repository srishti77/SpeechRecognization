package com.example.simvelop.speechrecognization


import android.Manifest
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings

import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.util.*
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.util.Log


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS = 1

    var permissions = arrayOf<String>(
        Manifest.permission.INTERNET
    )

    private val uuid = UUID.randomUUID().toString()
    var projectId = ""

    private var sessionsClient: SessionsClient? = null
    private var session: SessionName? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sessionId = "Id77"
        val languageCode = "en-US"

        val audioFile =
            resources.openRawResource(R.raw.book_a_room)

        askPermissions(permissions)

        initDialogFlow()
        btnStart.setOnClickListener() {


            val queryResult = TestSpeechRecognization.detectIntentAudio(
                projectId,
                audioFile,
                sessionId,
                languageCode,
                sessionsClient,
                session
            )

            if (queryResult != null) {
                tvSpokenText.text = queryResult.queryText
                tvIntention.text = queryResult.fulfillmentText
            }
            sessionsClient?.shutdownNow()
        }

    }

    override fun onStart() {
        super.onStart()


    }


    private fun initDialogFlow() {
        val stream: InputStream = resources.openRawResource(R.raw.test_speech)
        val googleCredentials = GoogleCredentials.fromStream(stream)
        projectId = (googleCredentials as ServiceAccountCredentials).projectId
        val settingBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
        val sessionsSettings = settingBuilder.setCredentialsProvider(
            FixedCredentialsProvider.create(googleCredentials)
        ).build()
        sessionsClient = SessionsClient.create(sessionsSettings)
        session = SessionName.of(projectId, uuid)
    }


    fun askPermissions(permissions: Array<String>) {

        if (!hasPermissions(permissions)) {

            ActivityCompat.requestPermissions(
                this,
                permissions, MY_PERMISSIONS
            )
        }

    }

    fun hasPermissions(permissions: Array<String>?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS -> for (i in permissions.indices) {
                if (grantResults.size > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permission Granted", "Permission Granted")
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {


                } else {


                }
            }
        }

    }

}
