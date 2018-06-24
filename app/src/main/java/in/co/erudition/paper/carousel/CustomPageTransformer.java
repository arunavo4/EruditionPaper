package in.co.erudition.paper.carousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * Created by Arunavo Ray on 14-06-2018.
 */

public class CustomPageTransformer implements ViewPager.PageTransformer {

    private ViewPager viewPager;
    private float bias = 0.1f;

    public CustomPageTransformer(Context context) {
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }
        view.setScaleY(1-Math.abs(position));
        view.setScaleX(1-Math.abs(position));
    }
}
