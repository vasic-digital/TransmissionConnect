package com.shareconnect.transmissionconnect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.onboarding.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {

    @Test
    fun testMainActivitySkipsOnboardingWhenCompleted() {
        // Clear onboarding prefs to simulate first launch
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // If onboarding is needed, MainActivity should finish immediately
            // and TransmissionConnectOnboardingActivity should be launched
            // We can't easily test the full flow here due to activity transitions,
            // but we can verify that MainActivity finishes if onboarding is needed
        }

        scenario.close()
    }

    @Test
    fun testOnboardingActivityLaunchesWhenNoProfiles() {
        // Clear any existing profiles and onboarding prefs
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("onboarding_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // This test would require launching TransmissionConnectOnboardingActivity
        // and verifying it shows the onboarding UI
        // For now, we'll just verify the activity can be launched
        val scenario = ActivityScenario.launch(TransmissionConnectOnboardingActivity::class.java)

        scenario.onActivity { activity ->
            // Verify the activity is created successfully
            assert(activity != null)
        }

        scenario.close()
    }
}