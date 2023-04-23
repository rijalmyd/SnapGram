package com.rijaldev.snapgram.presentation.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.rijaldev.snapgram.util.EspressoIdlingResource
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.presentation.addstory.AddStoryActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun takePhotoSuccess() {
        Intents.init()
        onView(withId(R.id.btn_add_story)).perform(click())

        intended(hasComponent(AddStoryActivity::class.java.name))
        onView(withId(R.id.cameraFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_capture)).perform(click())
        onView(withId(R.id.uploadFragment)).check(matches(isDisplayed()))

        Intents.release()
    }

    @Test
    fun uploadPhotoSuccess() {
        onView(withId(R.id.btn_add_story)).perform(click())
        onView(withId(R.id.btn_capture)).perform(click())

        onView(withId(R.id.ed_add_description)).perform(typeText("Espresso Test"), closeSoftKeyboard())
        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
    }
}