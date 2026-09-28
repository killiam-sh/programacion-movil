package com.stocksync.error404

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryListTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testCategoriesListDisplayed() {
        onView(withId(R.id.categoriesHeader)).check(matches(isDisplayed()))
        onView(withId(R.id.optionsListView)).check(matches(isDisplayed()))

        val expectedCategories = listOf("Perfil", "Fotos", "Video", "Web", "Botones")
        expectedCategories.forEachIndexed { index, categoryName ->
            onData(anything())
                .inAdapterView(withId(R.id.optionsListView))
                .atPosition(index)
                .check(matches(withText(categoryName)))
        }
    }

    @Test
    fun testCategorySelectionUpdatesContent() {
        // Select "Fotos"
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Fotos")))
            .inAdapterView(withId(R.id.optionsListView))
            .perform(click())

        // Select "Botones"
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Botones")))
            .inAdapterView(withId(R.id.optionsListView))
            .perform(click())

        onView(withText("Acciones rápidas")).check(matches(isDisplayed()))
    }
}
