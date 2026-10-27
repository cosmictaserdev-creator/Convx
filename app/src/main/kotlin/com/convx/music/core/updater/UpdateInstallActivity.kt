package com.convx.music.core.updater

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.convx.music.R
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.theme.ConvxTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class UpdateInstallActivity : ComponentActivity() {
    private val installer: UpdateInstaller by inject()
    private val permission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (packageManager.canRequestPackageInstalls()) startInstall() else finish()
    }
    private val approval = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConvxTheme {
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(progress = { 0.6f })
                    Text(stringResource(R.string.updater_install_notice))
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    installer.confirmation.collect { intent ->
                        if (intent != null) {
                            approval.launch(intent)
                            installer.confirmationLaunched()
                        }
                    }
                }
                launch {
                    installer.outcome.collect { result ->
                        if (result?.error != null || result?.phase == UpdatePhase.INSTALLED) finish()
                    }
                }
            }
        }
        if (savedInstanceState == null) {
            if (packageManager.canRequestPackageInstalls()) startInstall()
            else permission.launch(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
        }
    }
    private fun startInstall() { lifecycleScope.launch { withContext(Dispatchers.IO) { installer.install() } } }
}
