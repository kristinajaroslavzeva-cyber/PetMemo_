

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.name.petmemo.R

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlViewerScreen(
    navController: NavController,
    assetFileName: String,
    title: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.apply {
                            javaScriptEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
                            setSupportZoom(false)
                            builtInZoomControls = false
                            displayZoomControls = false
                            domStorageEnabled = false
                            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
                            isScrollbarFadingEnabled = true
                        }

                        loadUrl("file:///android_asset/$assetFileName")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun TermsOfUseScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val termsOfUseUrl = "https://your-website.com/privacy"

    LaunchedEffect(key1 = termsOfUseUrl) {
        uriHandler.openUri(termsOfUseUrl)
        navController.popBackStack()
    }
}

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = "https://your-website.com/privacy"

    LaunchedEffect(key1 = privacyPolicyUrl) {
        uriHandler.openUri(privacyPolicyUrl)
        navController.popBackStack()
    }
}

@Composable
fun AppDescriptionScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val appDescriptionUrl = "https://your-website.com/privacy"

    LaunchedEffect(key1 = appDescriptionUrl) {
        uriHandler.openUri(appDescriptionUrl)
        navController.popBackStack()
    }
}