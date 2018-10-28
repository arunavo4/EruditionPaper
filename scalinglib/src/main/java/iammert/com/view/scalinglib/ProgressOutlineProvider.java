package iammert.com.view.scalinglib;


import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * [BUG] All the curves and radius elements are squared in Android 9.0
 *
 * This is due to the intended behavioral changes of Path drawings and how the system handles the drawing of XFerModes paints in Android Pie.
 * More information can be found in this bug tracker: https://issuetracker.google.com/issues/111819103
 *
 * Current solution
 */

@TargetApi(Build.VERSION_CODES.P)
public class ProgressOutlineProvider extends ViewOutlineProvider {

    private float radius = -1;

    @Override
    public void getOutline(View view, Outline outline) {
        if(radius != -1) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
        }
    }

    public void updateProgress(float maxRadius, float progress) {
        this.radius = maxRadius - maxRadius * (1 - progress);
    }

}
