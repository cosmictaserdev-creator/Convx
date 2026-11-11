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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.convx.music.R
import com.convx.music.account.presentation.onboarding.OnboardingFeature
import com.convx.music.account.presentation.onboarding.OnboardingScreen
import com.convx.music.account.presentation.onboarding.OnboardingStrings
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.ListenTogetherUsernameKey
import com.convx.music.constants.LocalOnlyModeKey
import com.convx.music.constants.OnboardingCompleteKey
import com.convx.music.ui.component.ArtworkMeshBackground
import com.convx.music.ui.component.iosSheetPresentation
import com.convx.music.ui.component.rememberPlaylistAppearance
import com.convx.music.ui.component.rememberSheetPresentation
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.rememberPreference
import com.music.innertube.utils.parseCookieString
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch

/** UIKit's sheet curve: leaves at once, settles long, like a sheet that is let go of. */
private val DismissEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

/** One accent per page, so the mesh keeps changing colour as the welcome moves on. */
private val PageSeeds = intArrayOf(
    0xFFFF1E3C.toInt(), 0xFF7C4DFF.toInt(), 0xFF00B8D4.toInt(), 0xFFFF9100.toInt(), 0xFFE91E63.toInt(),
)

/**
 * The first-run welcome, drawn over the whole app. Runs on the artwork mesh: animated from
 * Android 13, one still frame below it.
 *
 * Tapping "Sign in" opens the app's own login page; the welcome stays composed but unplaced
 * while it is up, so it resumes on the same page.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AndroidOnboardingHost(navController: NavController, onYouTubeLoginRequest: () -> Unit) {
    val context = LocalContext.current
    val dataStore = context.dataStore
    val shown = remember { MutableTransitionState(dataStore[OnboardingCompleteKey] != true) }
    if (!shown.currentState && !shown.targetState) return

    val scope = rememberCoroutineScope()
    val (cookie) = rememberPreference(InnerTubeCookieKey, "")
    val (savedName) = rememberPreference(ListenTogetherUsernameKey, "")
    val signedIn = remember(cookie) { "SAPISID" in parseCookieString(cookie) }
    val notificationsAvailable = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
    }
    val pendingNotificationDone = remember { arrayOfNulls<() -> Unit>(1) }
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        pendingNotificationDone[0]?.invoke()
    }

    val strings = OnboardingStrings(
        welcomeTagline = stringResource(R.string.onboarding_tagline),
        getStarted = stringResource(R.string.get_started),
        features = listOf(
            OnboardingFeature(stringResource(R.string.onboarding_feature_lyrics_title), stringResource(R.string.onboarding_feature_lyrics_body)),
            OnboardingFeature(stringResource(R.string.onboarding_feature_glass_title), stringResource(R.string.onboarding_feature_glass_body)),
            OnboardingFeature(stringResource(R.string.onboarding_feature_offline_title), stringResource(R.string.onboarding_feature_offline_body)),
            OnboardingFeature(stringResource(R.string.onboarding_feature_together_title), stringResource(R.string.onboarding_feature_together_body)),
            OnboardingFeature(stringResource(R.string.onboarding_feature_sound_title), stringResource(R.string.onboarding_feature_sound_body)),
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
    val loginOpen = com.convx.music.ui.component.WebLoginSheets.youtube

    AnimatedVisibility(
        visibleState = shown,
        enter = EnterTransition.None,
        exit = slideOutVertically(tween(560, easing = DismissEasing)) { it },
    ) {
        OnboardingScreen(
            strings = strings,
            signedIn = signedIn,
            initialName = remember { savedName },
            notificationsAvailable = notificationsAvailable,
            onNameChosen = { name -> scope.launch { dataStore.edit { it[ListenTogetherUsernameKey] = name } } },
            onSignIn = {
                onYouTubeLoginRequest()
            },
            onLocalOnlyChosen = { local -> scope.launch { dataStore.edit { it[LocalOnlyModeKey] = local } } },
            onAllowNotifications = { done ->
                pendingNotificationDone[0] = done
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onFinish = {
                shown.targetState = false
                scope.launch { dataStore.edit { it[OnboardingCompleteKey] = true } }
            },
            modifier = Modifier.iosSheetPresentation(rememberSheetPresentation(loginOpen)),
            background = { page ->
                // Still below Android 13: the mesh renders once instead of drifting.
                ArtworkMeshBackground(
                    imageUrl = null,
                    appearance = rememberPlaylistAppearance(null, PageSeeds[page % PageSeeds.size]),
                    animated = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
                )
            },
            wordmarkFont = com.convx.music.ui.theme.ChillaxFontFamily,
            logoFont = com.convx.music.ui.theme.BdConsoleFontFamily,
            imeVisible = { WindowInsets.isImeVisible },
            backHandler = { enabled, onBack -> BackHandler(enabled = enabled && !loginOpen, onBack = onBack) },
        )
    }
}
