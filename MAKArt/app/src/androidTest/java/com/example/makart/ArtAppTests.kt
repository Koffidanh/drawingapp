package com.example.makart

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.makart.dialog.StrokeWidthComponent
import com.example.makart.utility.DrawingEntity
import com.example.makart.utility.StrokeProperties
import org.junit.Before


//------------------------------------SPLASH SCREEN-----------------------------//
@RunWith(AndroidJUnit4::class)
@LargeTest
class SplashScreenTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splashScreenDisplays() {
        composeTestRule.setContent {
            SplashScreen(onNavigateToMenu = {})
        }
        // Assert that the text "M.A.K Art" is displayed
        // checking if a node or composable contains the text
        composeTestRule.onNodeWithText("M.A.K Art").assertIsDisplayed()
    }
}

//------------------------------------MAIN MENU SCREEN-----------------------------//
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainMenuScreenDisplaysDrawButton() {
        // Initialize the NavController
        composeTestRule.setContent {
            val navController = rememberNavController()
            MainMenuScreen(navController = navController)
        }
        //draw is displayed
        composeTestRule.onNodeWithText("Draw").assertIsDisplayed()
    }

    @Test
    fun testLazyList(){
        return
    }

    @Test
    fun clickingDrawButtonNavigatesToDrawEditor() {
        lateinit var navController: androidx.navigation.NavController
        // Create a NavController instance
        composeTestRule.setContent {
            navController = rememberNavController()

            // Set up navigation graph and MainMenuScreen
            AppNavigation(navController = navController as NavHostController)
            MainMenuScreen(navController = navController)
        }


        composeTestRule.onNodeWithText("Draw").assertIsDisplayed()
        // Click the "Draw" button
        composeTestRule.onNodeWithText("Draw").performClick()
        composeTestRule.waitForIdle()
        // Check if the NavController navigated to the DrawEditor screen
        assert(navController.currentBackStackEntry?.destination?.route == Screen.DrawEditorNew.route)
    }
}

//-------------------------------Drawing list on the main menu tests------------------------
class DrawingListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var drawingEntity: DrawingEntity

    @Before
    fun setUp() {
        // Set up a sample DrawingEntity
        drawingEntity = DrawingEntity(
            id = 1,
            name = "Test Drawing",
            lastModified = "2024-10-04",
            thumbnail = null, // Testing without a thumbnail
            lines = ""
        )
    }

    @Test
    fun testDrawingListItemDisplaysCorrectName() {
        composeTestRule.setContent {
            DrawingListItem(
                drawingEntity = drawingEntity,
                onClick = {},
                onDeleteClick = {}
            )
        }

        // Assert that the drawing name is displayed
        composeTestRule.onNodeWithText(drawingEntity.name).assertIsDisplayed()
    }
    @Test
    fun testDeleteButtonShowsDialog() {
        composeTestRule.setContent {
            DrawingListItem(
                drawingEntity = drawingEntity,
                onClick = {},
                onDeleteClick = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete").isDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        composeTestRule.onNodeWithText("Are you sure you want to delete this drawing?").assertIsDisplayed()
    }


    //------------------------------------DRAW EDITOR SCREEN-----------------------------//
    @RunWith(AndroidJUnit4::class)
    @LargeTest
    class DrawEditorScreenTest {

        lateinit var navController: NavController

        @get:Rule
        val composeTestRule = createComposeRule()

        //SET-UP for the draw editor screen tests
        @Before
        fun setUp() {
            composeTestRule.setContent {
                navController = rememberNavController()
                AppNavigation(navController = navController as NavHostController)
                DrawEditorScreen(
                    drawingId = 1,
                    onBack = { navController.navigate(Screen.MainMenu.route) })
            }
        }

        //Color
        @Test
        fun testColorPickerDialogIsDisplayed() {
            composeTestRule.onNodeWithContentDescription("Color").performClick()
            composeTestRule.onNodeWithTag("ColorPickerDialog").assertIsDisplayed()
        }

        @Test
        fun testCloseColorPickerDialog() {
            composeTestRule.onNodeWithContentDescription("Color").performClick()
            composeTestRule.onNodeWithTag("ColorPickerDialog").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Close dialog").performClick()
            composeTestRule.onNodeWithTag("ColorPickerDialog").assertDoesNotExist()
        }

        //Save
        @Test
        fun testSaveDialogIsDisplayed() {
            composeTestRule.onNodeWithContentDescription("Save").performClick()
            composeTestRule.onNodeWithText("Save Drawing").assertIsDisplayed()
        }

        @Test
        fun testTypingNameForDrawing() {
            composeTestRule.onNodeWithContentDescription("Save").performClick()
            composeTestRule.onNodeWithText("Save Drawing").assertIsDisplayed()
            val drawingName = "My Drawing"
            composeTestRule.onNodeWithText("Enter a name for your drawing:").assertIsDisplayed()
            //trouble with inputting text and saving it
//        composeTestRule.onNodeWithText("Drawing name").performTextInput(drawingName)
//        composeTestRule.onNodeWithText(drawingName).assertIsDisplayed()
        }

        @Test
        fun testCloseSaveDialog() {
            composeTestRule.onNodeWithContentDescription("Save").performClick()
            composeTestRule.onNodeWithText("Save Drawing").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Close dialog").performClick()

        }

        //Erase
        @Test
        fun testEraseModeToggle() {
            composeTestRule.onNodeWithContentDescription("Erase").performClick()
            composeTestRule.onNodeWithContentDescription("Erase").assertExists()
        }

        //Back Button
        @Test
        fun testBackButtonNavigatesMainMenu() {
            composeTestRule.onNodeWithContentDescription("Back").performClick()
            composeTestRule.waitForIdle()
            // Assert that after the 'Back' button click, the user navigates to the MainMenuScreen
            assert(navController.currentBackStackEntry?.destination?.route == Screen.MainMenu.route)
        }

        //Stroke
        //TODO test stroke and shape selection
        @Test
        fun testStrokeButtonDialogIsDisplayed() {
            composeTestRule.onNodeWithContentDescription("Stroke").performClick()
            composeTestRule.onNodeWithTag("StrokePickerDialog").assertIsDisplayed()
        }

        @Test
        fun testStrokeButtonClose() {
            composeTestRule.onNodeWithContentDescription("Stroke").performClick()
            composeTestRule.onNodeWithTag("StrokePickerDialog").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Close dialog").assertIsDisplayed()
            composeTestRule.onNodeWithContentDescription("Close dialog").performClick()
        }

    }
}

//stroke width

class StrokeWidthComponentTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var currentStrokeProperty: StrokeProperties

    @Before
    fun setUp() {
        currentStrokeProperty = StrokeProperties(strokeWidth = 5f)
    }

    @Test
    fun testCloseButtonDismissesDialog() {
        var isDismissed = false

        composeTestRule.setContent {
            StrokeWidthComponent(
                currentStrokeProperty = currentStrokeProperty,
                updateCurrentStrokeProperty = { _, _ -> /* No-op */ },
                onDismiss = { isDismissed = true }
            )
        }
        composeTestRule.onNodeWithContentDescription("Close dialog").performClick()
        assert(isDismissed)
    }
}



