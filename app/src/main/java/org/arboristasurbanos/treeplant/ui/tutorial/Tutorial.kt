package org.arboristasurbanos.treeplant.ui.tutorial

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle

class Tutorial : Activity() {

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        /*
        addSlide(
            AppIntroFragment.newInstance(
                title = "Welcome...",
                description = "This is the first slide of the example"
            ))
        addSlide(AppIntroFragment.newInstance(
            title = "...Let's get started!",
            description = "This is the last slide, I won't annoy you more :)"
        ))
        addSlide(AppIntroFragment.newInstance(
            title = "The title of your slide",
            description = "A description that will be shown on the bottom",
            imageDrawable = R.drawable.ic_menu_slideshow,
            backgroundDrawable = R.drawable.common_google_signin_btn_icon_dark_focused,
            titleColor = R.color.yellow,
            descriptionColor = R.color.red,
            backgroundColor = R.color.blue,

        ))
        // Toggle Indicator Visibility
        isIndicatorEnabled = true

// Change Indicator Color
        setIndicatorColor(
            selectedIndicatorColor = getColor(R.color.red),
            unselectedIndicatorColor = getColor(R.color.blue),
        )

// Switch from Dotted Indicator to Progress Indicator
        setProgressIndicator()

// Supply your custom `IndicatorController` implementation
      //  indicatorController = MyCustomIndicator(/* initialize me */)
      */

    }
    /*
    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }*/

   /*override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        finish()
    }*/
}