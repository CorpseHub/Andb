package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class YouTubePlayerController {
    var webView: WebView? = null

    fun play() {
        webView?.post { webView?.evaluateJavascript("playVideo();", null) }
    }

    fun pause() {
        webView?.post { webView?.evaluateJavascript("pauseVideo();", null) }
    }

    fun seekTo(seconds: Int) {
        webView?.post { webView?.evaluateJavascript("seekTo($seconds);", null) }
    }

    fun setVolume(volumePercent: Int) {
        webView?.post { webView?.evaluateJavascript("setVolume($volumePercent);", null) }
    }

    fun loadVideo(videoId: String) {
        webView?.post { webView?.evaluateJavascript("loadVideo('$videoId');", null) }
    }
}

class YouTubeJsBridge(
    private val onReady: () -> Unit,
    private val onStateChange: (Int) -> Unit,
    private val onError: (Int) -> Unit,
    private val onProgress: (Int, Int) -> Unit
) {
    @JavascriptInterface
    fun onReady() {
        onReady.invoke()
    }

    @JavascriptInterface
    fun onStateChange(state: Int) {
        onStateChange.invoke(state)
    }

    @JavascriptInterface
    fun onError(errorCode: Int) {
        onError.invoke(errorCode)
    }

    @JavascriptInterface
    fun onProgress(curSec: Int, durSec: Int) {
        onProgress.invoke(curSec, durSec)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerView(
    videoId: String,
    isPlaying: Boolean,
    controller: YouTubePlayerController,
    onStateChanged: (Int) -> Unit,
    onError: (Int) -> Unit,
    onProgressUpdate: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val bridge = remember(videoId) {
        YouTubeJsBridge(
            onReady = {
                if (isPlaying) {
                    controller.play()
                }
            },
            onStateChange = onStateChanged,
            onError = onError,
            onProgress = onProgressUpdate
        )
    }

    val htmlContent = remember(videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <style>
                * { box-sizing: border-box; }
                body, html { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #000; overflow: hidden; }
                #player { width: 100%; height: 100%; min-width: 200px; min-height: 200px; }
            </style>
        </head>
        <body>
            <div id="player"></div>
            <script src="https://www.youtube.com/iframe_api"></script>
            <script>
                var player;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        height: '100%',
                        width: '100%',
                        videoId: '$videoId',
                        playerVars: {
                            'playsinline': 1,
                            'autoplay': 1,
                            'controls': 1,
                            'rel': 0,
                            'modestbranding': 1,
                            'fs': 1,
                            'origin': 'https://larpify.app'
                        },
                        events: {
                            'onReady': onPlayerReady,
                            'onStateChange': onPlayerStateChange,
                            'onError': onPlayerError
                        }
                    });
                }
                function onPlayerReady(event) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.onReady();
                    }
                    event.target.playVideo();
                    setInterval(function() {
                        if (player && typeof player.getCurrentTime === 'function' && typeof player.getDuration === 'function') {
                            var cur = Math.floor(player.getCurrentTime());
                            var dur = Math.floor(player.getDuration());
                            if (window.AndroidBridge) {
                                window.AndroidBridge.onProgress(cur, dur);
                            }
                        }
                    }, 1000);
                }
                function onPlayerStateChange(event) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.onStateChange(event.data);
                    }
                }
                function onPlayerError(event) {
                    if (window.AndroidBridge) {
                        window.AndroidBridge.onError(event.data);
                    }
                }
                function playVideo() { if (player && player.playVideo) player.playVideo(); }
                function pauseVideo() { if (player && player.pauseVideo) player.pauseVideo(); }
                function seekTo(sec) { if (player && player.seekTo) player.seekTo(sec, true); }
                function setVolume(vol) { if (player && player.setVolume) player.setVolume(vol); }
                function loadVideo(vid) { if (player && player.loadVideoById) player.loadVideoById(vid); }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            controller.play()
        } else {
            controller.pause()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minWidth = 200.dp, minHeight = 200.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(androidx.compose.ui.graphics.Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.BLACK)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {}
                    addJavascriptInterface(bridge, "AndroidBridge")
                    loadDataWithBaseURL("https://larpify.app", htmlContent, "text/html", "UTF-8", null)
                    controller.webView = this
                }
            },
            update = { view ->
                controller.webView = view
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.webView?.stopLoading()
            controller.webView = null
        }
    }
}
