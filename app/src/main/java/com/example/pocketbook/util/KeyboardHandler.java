package com.example.pocketbook.util;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class KeyboardHandler {

    View rootView;
    Activity activity;

    /**
     * Empty constructor for Keyboard Handler
     */
    public KeyboardHandler() {}

    /**
     * Constructor for Keyboard Handler
     * @param rootView root view
     * @param activity activity
     */
    public KeyboardHandler(View rootView, Activity activity) {
        this.rootView = rootView;
        this.activity = activity;
    }

    /**
     * hides view when keyboard is visible
     * @param viewId id of view to hide
     */
    public void hideViewOnKeyboardUp(int viewId) {
        /*
          KEYBOARD CODE SOURCE: https://stackoverflow.com/questions/4745988/
          how-do-i-detect-if-software-keyboard-is-visible-on-android-device-or-not
         */

        // rootView is the root view of the layout of this activity
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {

                    Rect r = new Rect();
                    rootView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = rootView.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    Log.d("KEYBOARD_HANDLER", "keypadHeight = " + keypadHeight);

                    if (keypadHeight > screenHeight * 0.15) {
                        // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        Objects.requireNonNull(activity)
                                .findViewById(viewId).setVisibility(View.GONE);
                    }
                    else {
                        // keyboard is closed
                        Objects.requireNonNull(activity)
                                .findViewById(viewId).setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * hides the keyboard
     * @param activity activity
     */
    public static void hideKeyboard(Activity activity) {
        /*
          KEYBOARD CODE SOURCE: https://stackoverflow.com/questions/1109022/
          how-do-you-close-hide-the-android-soft-keyboard-using-java
         */

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
