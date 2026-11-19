package com.convx.music.ui.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import com.convx.music.R
import com.convx.music.account.presentation.onboarding.OnboardingFeature
import com.convx.music.account.presentation.onboarding.OnboardingScreen
import com.convx.music.account.presentation.onboarding.OnboardingStrings
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.ListenTogetherUsernameKey
import com.convx.music.constants.LocalOnlyModeKey
import com.convx.music.ui.component.ArtworkMeshBackground
import com.convx.music.ui.component.iosSheetPresentation
import com.convx.music.ui.component.rememberPlaylistAppearance
import com.convx.music.ui.screens.hasYouTubeAccountSession
import com.convx.music.ui.theme.ChillaxFontFamily
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** UIKit's sheet curve: leaves at once, settles long, like a sheet that is let go of. */
private val DismissEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

/** One accent per page, so the mesh keeps changing colour as the welcome moves on. */
private val PageSeeds = intArrayOf(
    0xFFFF1E3C.toInt(), 0xFF7C4DFF.toInt(), 0xFF00B8D4.toInt(), 0xFFFF9100.toInt(), 0xFFE91E63.toInt(),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AndroidOnboardingHost(
    loginOpen: Boolean,
    loginProgress: () -> Float,
    onYouTubeLoginRequest: () -> Unit,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    val dataStore = context.dataStore
    val shown = remember { MutableTransitionState(true) }
    val scope = rememberCoroutineScope()
    val savedName = remember(dataStore) { dataStore.get(ListenTogetherUsernameKey, "") }
    // The login validates the account and restarts the app. Ignore provisional cookies
    // written by the WebView during this run of onboarding.
    val signedIn = remember(dataStore) { dataStore.get(InnerTubeCookieKey, "").hasYouTubeAccountSession() }
    val animateMesh = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val notificationsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
    var pendingNotificationDone by remember { mutableStateOf<(() -> Unit)?>(null) }
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        val done = pendingNotificationDone
        pendingNotificationDone = null
        done?.invoke()
    }
    var preferenceWrite by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(shown.isIdle, shown.currentState) {
        if (shown.isIdle && !shown.currentState) {
            preferenceWrite?.join()
            onFinish()
        }
    }

    val strings = OnboardingStrings(
        welcomeTagline = stringResource(R.string.onboarding_tagline),
        getStarted = stringResource(R.string.get_started),
        features = listOf(
            OnboardingFeature(
                stringResource(R.string.onboarding_feature_lyrics_title),
                stringResource(R.string.onboarding_feature_lyrics_body),
            ),
            OnboardingFeature(
                stringResource(R.string.onboarding_feature_glass_title),
                stringResource(R.string.onboarding_feature_glass_body),
            ),
            OnboardingFeature(
                stringResource(R.string.onboarding_feature_offline_title),
                stringResource(R.string.onboarding_feature_offline_body),
            ),
            OnboardingFeature(
                stringResource(R.string.onboarding_feature_together_title),
                stringResource(R.string.onboarding_feature_together_body),
            ),
            OnboardingFeature(
                stringResource(R.string.onboarding_feature_sound_title),
                stringResource(R.string.onboarding_feature_sound_body),
            ),
        ),
        nameTitle = stringResource(R.string.onboarding_title),
        namePrompt = stringResource(R.string.onboarding_name_prompt),
        nameHint = stringResource(R.string.onboarding_name_hint),
        nameFooter = stringResource(R.string.onboarding_name_footer),
        next = stringResource(R.string.onboarding_next),
        connectTitle = stringResource(R.string.onboarding_connect_title),
        connectBody = stringResource(R.string.onboarding_connect_body),
        signIn = stringResource(R.string.onboarding_sign_in_google),
        skipSignIn = stringResource(R.string.onboarding_skip_sign_in),
        modeTitle = stringResource(R.string.onboarding_mode_title),
        modeBody = stringResource(R.string.onboarding_mode_body),
        modeOnline = stringResource(R.string.onboarding_mode_online),
        modeLocal = stringResource(R.string.onboarding_mode_local),
        notificationsTitle = stringResource(R.string.onboarding_notifications_title),
        notificationsBody = stringResource(R.string.onboarding_notifications_body),
        notificationsAllow = stringResource(R.string.onboarding_notifications_allow),
        notificationsSkip = stringResource(R.string.onboarding_notifications_skip),
    )

    AnimatedVisibility(
        visibleState = shown,
        enter = EnterTransition.None,
        exit = slideOutVertically(tween(280, easing = DismissEasing)) { it },
    ) {
        OnboardingScreen(
            strings = strings,
            signedIn = signedIn,
            initialName = savedName,
            notificationsAvailable = notificationsAvailable,
            onNameChosen = { name ->
                preferenceWrite = scope.launch { dataStore.edit { it[ListenTogetherUsernameKey] = name } }
            },
            onSignIn = onYouTubeLoginRequest,
            onLocalOnlyChosen = { local ->
                val previousWrite = preferenceWrite
                preferenceWrite = scope.launch {
                    previousWrite?.join()
                    dataStore.edit { it[LocalOnlyModeKey] = local }
                }
            },
            onAllowNotifications = { done ->
                pendingNotificationDone = done
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onFinish = { shown.targetState = false },
            modifier = Modifier.iosSheetPresentation(loginProgress),
            background = { page ->
                // Render a fresh still for each page on older Android versions.
                key(if (animateMesh) 0 else page) {
                    ArtworkMeshBackground(
                        imageUrl = null,
                        appearance = rememberPlaylistAppearance(null, PageSeeds[page % PageSeeds.size]),
                        paused = { loginOpen || !animateMesh },
                    )
                }
            },
            wordmarkFont = ChillaxFontFamily,
            imeVisible = { WindowInsets.isImeVisible },
            backHandler = { enabled, onBack ->
                BackHandler(enabled = enabled && !loginOpen && shown.targetState, onBack = onBack)
            },
        )
    }
}
