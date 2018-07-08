package in.co.erudition.paper.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.widget.TextView;

/**
 * Use this class to setup vector Drawables on pre-Lollipop
 *
 * when I used vector drawables on pre-lollipop. Everything works fine but when I use.
 *
 *  android:drawableLeft
 *  android:drawableRight
 *  android:drawableTop
 *  android:drawableBottom
 *
 *  On ImageView, ImageButton, TextView & EditText.
 *
 *  Usage:
 *  VectorUtils.setVectorForPreLollipop(EditText,R.drawable.mic, activity, 0);
 */

public class VectorUtils {

    //TODO: Check for pre-lollipop crashes and make changes

    //region Helper method for PreLollipop TextView & Buttons Vector Images
    public static Drawable setVectorForPreLollipop(int resourceId, Context activity) {
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = VectorDrawableCompat.create(activity.getResources(), resourceId, activity.getTheme());
        } else {
            icon = activity.getResources().getDrawable(resourceId, activity.getTheme());
        }

        return icon;
    }


    public static void setVectorForPreLollipop(TextView textView, int resourceId, Context activity, int position) {
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = VectorDrawableCompat.create(activity.getResources(), resourceId,
                    activity.getTheme());
        } else {
            icon = activity.getResources().getDrawable(resourceId, activity.getTheme());
        }
        switch (position) {
            case 0:
                //Left
                textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null,
                        null);
                break;

            case 1:
                //Right
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon,
                        null);
                break;

            case 2:
                //Top
                textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null,
                        null);
                break;

            case 3:
                //Bottom
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
                        icon);
                break;
        }
    }

    //endregion
}
