import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.makart.SplashScreenActivty

@RunWith(AndroidJUnit4::class)
@LargeTest
class SplashScreenTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(SplashScreenActivty::class.java)

    @Test
    fun splashScreenDisplays() {
        onView(withText("M.A.K Art")).check(matches(isDisplayed()))
    }
}
