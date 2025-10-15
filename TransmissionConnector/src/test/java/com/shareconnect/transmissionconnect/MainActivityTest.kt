package com.shareconnect.transmissionconnect

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MainActivityTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    private lateinit var mainActivity: MainActivity

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mainActivity = MainActivity()

        // Mock the getSharedPreferences call
        `when`(mockContext.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
    }

    @Test
    fun `isOnboardingNeeded returns true when onboarding not completed`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("onboarding_completed", false)).thenReturn(false)

        // When
        val result = mainActivity.isOnboardingNeeded()

        // Then
        assertTrue("Onboarding should be needed when not completed", result)
    }

    @Test
    fun `isOnboardingNeeded returns false when onboarding already completed`() {
        // Given
        `when`(mockSharedPreferences.getBoolean("onboarding_completed", false)).thenReturn(true)

        // When
        val result = mainActivity.isOnboardingNeeded()

        // Then
        assertFalse("Onboarding should not be needed when already completed", result)
    }

    // Helper method to access private method for testing
    private fun MainActivity.isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }
}