package com.shareconnect.transmissionconnect

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.shareconnect.onboarding.ui.OnboardingActivity
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel
import com.shareconnect.profilesync.models.ProfileData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransmissionConnectOnboardingActivity : OnboardingActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // Set app-specific information
        appName = "TransmissionConnect"
        appDescription = "Connect to your Transmission torrent client and manage your downloads seamlessly across devices"

        // Initialize viewModel with sync managers from TransmissionRemote
        val app = application as TransmissionRemote
        viewModel.initializeSyncManagers(
            app.themeSyncManager,
            app.profileSyncManager,
            app.languageSyncManager
        )

        // Check if profiles are already available and skip onboarding if so
        lifecycleScope.launch {
            // First check if we already have profiles
            val existingProfiles = app.profileSyncManager.getAllProfiles()
            existingProfiles.collect { profilesList ->
                val transmissionProfiles = profilesList.filter { profile ->
                    profile.torrentClientType == ProfileData.TORRENT_CLIENT_TRANSMISSION
                }
                if (transmissionProfiles.isNotEmpty()) {
                    // Profiles are available, skip/dismiss onboarding
                    viewModel.markOnboardingComplete()
                    launchMainApp()
                }
                // If no profiles exist, continue showing onboarding until user creates one
            }
        }
    }

    override fun launchMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}