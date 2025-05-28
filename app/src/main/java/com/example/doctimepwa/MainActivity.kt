package com.example.doctimepwa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.doctimepwa.R

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var webView: WebView
    private var pendingPermissionRequest: PermissionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        //initializeWebView()
        setupWebViewWithHeaders()
    }

    private fun initializeWebView() {
        when {
            checkPermissions() -> setupWebViewWithHeaders()
            else -> requestPermissions()
        }
    }

    private fun setupWebViewWithHeaders() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = true
                allowContentAccess = true
            }

            webChromeClient = createWebChromeClient()
            webViewClient = createWebViewClient()

            loadUrl("https://hishabeeuat.doctime.com.bd/", createHeaders())
        }
    }

    private fun createWebChromeClient(): WebChromeClient? = object : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                handlePermissionRequest(request)
            }
        }
    }

    private fun createWebViewClient(): WebViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            // Add loading indicator if needed
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Hide loading indicator if needed
        }
    }

    private fun handlePermissionRequest(request: PermissionRequest?) {
        runOnUiThread {
            request?.let {
                if (it.resources.any { resource ->
                        resource == PermissionRequest.RESOURCE_VIDEO_CAPTURE ||
                                resource == PermissionRequest.RESOURCE_AUDIO_CAPTURE
                    }) {
                    when {
                        checkPermissions() -> it.grant(it.resources)
                        else -> {
                            pendingPermissionRequest = it
                            requestPermissions()
                        }
                    }
                }
            }
        }
    }

    // Extension functions for cleaner code
    private fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions(): Boolean {
        return hasPermission(Manifest.permission.CAMERA) &&
                hasPermission(Manifest.permission.RECORD_AUDIO)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun createHeaders(): Map<String, String> {
        return buildMap {
            put("Authorization", "Bearer ${getAuthToken()}")

            put("Content-Type", "application/json")
            put("Accept", "application/json")
        }
    }

    private fun getAuthToken(): String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI0IiwianRpIjoiMzU4ZDdkMGI0ZDAzMmYwMjQ4Zjk4N2NlNmUzZDkzNTY3MzRhODdlYTRiNDJiZWJkYzBkZTJhZTA4MTk5OTZhYWE4ZjBmYmU0NzE3ZGFlYmIiLCJpYXQiOjE3NDg0MDcxODQuNDcyOTI4LCJuYmYiOjE3NDg0MDcxODQuNDcyOTMzLCJleHAiOjE3NDg0NTUxOTkuMDg4NjYyLCJzdWIiOiI1MTc3MDMiLCJzY29wZXMiOlsiYWNjZXNzLXBhcnRuZXIiLCJwYXJ0bmVyLWlkLTYiXX0.ZEr0OSQ74nd4Au8deER9c_cmlwJ7XsX_of1p_ZEeNnvbxuHfDD9h1kwdbCHckYMQflPUjAOFmLyGdHVrW5O7CW2rMwWdC8Rkk5y02gnescIQDZPk7J69vw13l702oBK2tbcjHMehDF92STUFt2FLHuFP__LkTrqXnPXub_hMQnK5Tw_eD-G85mp2lUpN_2cifFaErvN1a0LnxUD9qRPzCV-XI_HFKZmCyCllQ6C9IhCJ23yu1452Lo8vMIZlfUDRI3HyYpzmk-5P_2VFuLszXcEKsYeyasvs01_u-NUvfhZ-PsALeMNSw-WrdhWCErGZK5uDnNQbdPVKn_KnUpBa1zlFRlw8Df-mOZ5P0X_b5YKIhUBnKREy4SwrW0FCJP3Y0YLWcTvxoAroqw2U29kWlcLXb65h-FZT-_XAHxDXadB6OVIxwwnI9n7EB8OGTcwNXwvLLdcPGTXwOro2E3cBTIjC4QYTfAoOCWuxTiL0FYsk2QSFaH4m1FN6KYxLiFKAs5q33XIz3_ze3kZUfmDP5FJOjczI2Be5JBUT14-Hk6bWFOuHmWrS4vuR2rPltpQRO-dfieSiK4n0JywtUm3XngLnmcJLPapbnEy9OY2kSBDdseCCOe8Eg434CNG9YteNOi_dGhYQHJV5ZktpzYqGfJZQKjQ69DqbG8OIfRAwUA0"


    private fun getApiKey(): String = "your-api-key-here"

    private fun getUserId(): String =
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("user_id", "") ?: ""

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                pendingPermissionRequest?.grant(pendingPermissionRequest?.resources)
                if (webView.url == null) setupWebViewWithHeaders()
            } else {
                pendingPermissionRequest?.deny()
                showPermissionDeniedMessage()
            }

            pendingPermissionRequest = null
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Camera and microphone permissions are required for video calls",
            Toast.LENGTH_LONG
        ).show()
    }
}