package com.aviad.chordstv.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.MotionEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.domain.model.WebBookmark
import com.aviad.chordstv.ui.components.TvPillButton
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface
import com.aviad.chordstv.ui.theme.FavoriteColor
import com.aviad.chordstv.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * TV-friendly browser for external chord sites.
 *
 * The D-pad moves an on-screen pointer; OK clicks; when the pointer reaches the
 * top/bottom edge the page scrolls. Media keys drive auto-scroll like the song
 * screen: Play-Pause toggles, RW/FF change speed. Toolbar: back, reload, zoom,
 * bookmark. UP at the very top of the page moves focus to the toolbar.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebScreen(
    container: AppContainer,
    initialUrl: String,
    onExit: () -> Unit
) {
    val viewModel: WebViewModel = viewModel(
        factory = viewModelFactory { initializer { WebViewModel(container) } }
    )
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    var webView by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("") }
    var progress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var areaSize by remember { mutableStateOf(IntSize.Zero) }
    var cursor by remember { mutableStateOf(Offset.Zero) }
    var cursorFocused by remember { mutableStateOf(false) }
    var autoScroll by remember { mutableStateOf(false) }
    var speed by remember { mutableIntStateOf(prefs.defaultScrollSpeed) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val cursorFocus = remember { FocusRequester() }
    val isBookmarked = prefs.webBookmarks.any { it.url == currentUrl }

    // Start the pointer in the middle of the page once we know the size.
    LaunchedEffect(areaSize) {
        if (areaSize != IntSize.Zero && cursor == Offset.Zero) {
            cursor = Offset(areaSize.width / 2f, areaSize.height / 2f)
            runCatching { cursorFocus.requestFocus() }
        }
    }

    // Apply zoom preference to the WebView
    LaunchedEffect(webView, prefs.webTextZoom) {
        webView?.settings?.textZoom = prefs.webTextZoom
    }

    // Auto-scroll engine
    LaunchedEffect(autoScroll, speed, webView) {
        val wv = webView ?: return@LaunchedEffect
        if (!autoScroll) return@LaunchedEffect
        val pxPerSecond = with(density) { (speed * 14).dp.toPx() }
        var last = 0L
        var acc = 0f
        while (true) {
            val now = withFrameNanos { it }
            if (last != 0L) {
                acc += pxPerSecond * (now - last) / 1_000_000_000f
                val whole = acc.toInt()
                if (whole > 0) {
                    wv.scrollBy(0, whole); acc -= whole
                }
            }
            last = now
            if (!wv.canScrollVertically(1)) { autoScroll = false; break }
        }
    }

    BackHandler {
        val wv = webView
        if (wv != null && wv.canGoBack()) wv.goBack() else onExit()
    }

    fun click(x: Float, y: Float) {
        val wv = webView ?: return
        scope.launch {
            val down = SystemClock.uptimeMillis()
            wv.dispatchTouchEvent(MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, x, y, 0))
            delay(60)
            val up = SystemClock.uptimeMillis()
            wv.dispatchTouchEvent(MotionEvent.obtain(down, up, MotionEvent.ACTION_UP, x, y, 0))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.MediaPlayPause, Key.MediaPlay, Key.MediaPause -> { autoScroll = !autoScroll; true }
                    Key.MediaFastForward -> { speed = (speed + 1).coerceIn(1, 10); true }
                    Key.MediaRewind -> { speed = (speed - 1).coerceIn(1, 10); true }
                    else -> false
                }
            }
    ) {
        // ---- Toolbar ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TvPillButton(text = stringResource(R.string.back), icon = Icons.Default.ArrowBack, onClick = {
                val wv = webView
                if (wv != null && wv.canGoBack()) wv.goBack() else onExit()
            })
            TvPillButton(text = "", icon = Icons.Default.Refresh, onClick = { webView?.reload() })
            TvPillButton(
                text = (if (autoScroll) "⏸ " else "▶ ") + stringResource(R.string.song_autoscroll) + " $speed",
                selected = autoScroll,
                onClick = { autoScroll = !autoScroll }
            )
            TvPillButton(text = "A−", onClick = { viewModel.setZoom(prefs.webTextZoom - 10) })
            TvPillButton(text = "A+", onClick = { viewModel.setZoom(prefs.webTextZoom + 10) })
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pageTitle.ifBlank { currentUrl },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentUrl,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TvPillButton(
                text = "★",
                selected = isBookmarked,
                contentColor = if (isBookmarked) FavoriteColor else TextSecondary,
                onClick = { viewModel.toggleBookmark(WebBookmark(currentUrl, pageTitle.ifBlank { currentUrl })) }
            )
        }

        Spacer(Modifier.height(10.dp))

        // Thin progress bar
        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(BgSurface)) {
            if (progress in 1..99) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress / 100f)
                        .background(AccentCyan)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ---- Page + pointer overlay ----
        val step = with(density) { 28.dp.toPx() }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = if (cursorFocused) 3.dp else 1.dp,
                    color = if (cursorFocused) AccentCyan else TextSecondary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                )
                .onSizeChanged { areaSize = it }
                .focusRequester(cursorFocus)
                .onFocusChanged { cursorFocused = it.isFocused }
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    val wv = webView
                    val repeat = event.nativeKeyEvent.repeatCount
                    val move = step * (1f + (repeat / 4f).coerceAtMost(2.5f))
                    val w = areaSize.width.toFloat()
                    val h = areaSize.height.toFloat()
                    val edge = step * 2
                    when (event.key) {
                        Key.DirectionUp -> {
                            if (cursor.y - move < edge) {
                                if (wv != null && wv.canScrollVertically(-1)) {
                                    wv.scrollBy(0, -move.toInt()); true
                                } else if (cursor.y > 4f) {
                                    cursor = cursor.copy(y = 4f); true
                                } else false                // at the very top → let focus go to the toolbar
                            } else { cursor = cursor.copy(y = cursor.y - move); true }
                        }
                        Key.DirectionDown -> {
                            if (cursor.y + move > h - edge) {
                                if (wv != null && wv.canScrollVertically(1)) wv.scrollBy(0, move.toInt())
                                cursor = cursor.copy(y = (cursor.y + move).coerceAtMost(h - 4f))
                            } else cursor = cursor.copy(y = cursor.y + move)
                            true
                        }
                        Key.DirectionLeft -> {
                            if (cursor.x - move < 0f && wv != null && wv.canScrollHorizontally(-1)) {
                                wv.scrollBy(-move.toInt(), 0)
                            }
                            cursor = cursor.copy(x = (cursor.x - move).coerceAtLeast(2f)); true
                        }
                        Key.DirectionRight -> {
                            if (cursor.x + move > w && wv != null && wv.canScrollHorizontally(1)) {
                                wv.scrollBy(move.toInt(), 0)
                            }
                            cursor = cursor.copy(x = (cursor.x + move).coerceAtMost(w - 2f)); true
                        }
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                            if (repeat == 0) click(cursor.x, cursor.y); true
                        }
                        Key.PageDown, Key.ChannelDown -> { wv?.scrollBy(0, (h * 0.8f).toInt()); true }
                        Key.PageUp, Key.ChannelUp -> { wv?.scrollBy(0, -(h * 0.8f).toInt()); true }
                        else -> false
                    }
                }
                .focusable()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = false
                        settings.textZoom = prefs.webTextZoom
                        // The pointer overlay owns the D-pad; the WebView must not steal focus.
                        isFocusable = false
                        isFocusableInTouchMode = false
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                                currentUrl = url
                                canGoBack = view.canGoBack()
                            }
                            override fun onPageFinished(view: WebView, url: String) {
                                currentUrl = url
                                pageTitle = view.title.orEmpty()
                                canGoBack = view.canGoBack()
                                progress = 100
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView, newProgress: Int) {
                                progress = newProgress
                            }
                            override fun onReceivedTitle(view: WebView, title: String?) {
                                pageTitle = title.orEmpty()
                            }
                        }
                        loadUrl(initialUrl)
                        webView = this
                    }
                }
            )

            // Pointer
            Canvas(modifier = Modifier.fillMaxSize()) {
                val p = Path().apply {
                    moveTo(cursor.x, cursor.y)
                    lineTo(cursor.x, cursor.y + 34f)
                    lineTo(cursor.x + 9f, cursor.y + 26f)
                    lineTo(cursor.x + 15f, cursor.y + 38f)
                    lineTo(cursor.x + 20f, cursor.y + 35f)
                    lineTo(cursor.x + 14f, cursor.y + 24f)
                    lineTo(cursor.x + 25f, cursor.y + 24f)
                    close()
                }
                drawPath(p, color = if (cursorFocused) AccentCyan else AccentCyan.copy(alpha = 0.5f))
                drawPath(p, color = Color.Black, style = Stroke(width = 2f))
            }
        }

        Spacer(Modifier.height(8.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = stringResource(R.string.web_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
