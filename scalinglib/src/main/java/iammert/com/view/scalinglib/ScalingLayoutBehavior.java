package iammert.com.view.scalinglib;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by mertsimsek on 30/09/2017.
 *
 * Made some changes to fit the needs
 */

public class ScalingLayoutBehavior extends CoordinatorLayout.Behavior<ScalingLayout> {

    private float toolbarHeightInPixel;
    private Context mContext;

    public ScalingLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ScalingLayout,0,0);
        /*
            toolbarHeightInPixel = context.getResources().getDimensionPixelSize(R.dimen.sl_toolbar_size);
            needed app_bar_plus_status_bar height
        */
        toolbarHeightInPixel = styledAttributes.getDimensionPixelSize(R.styleable.ScalingLayout_toolbarHeight, context.getResources().getDimensionPixelSize(R.dimen.app_bar_plus_status_bar));
        styledAttributes.recycle();
    }

    public ScalingLayoutBehavior(Context context, AttributeSet attrs, float toolbarHeight){
        super(context,attrs);
        mContext = context;
        //  Changes been made to support notches
        toolbarHeightInPixel = toolbarHeight;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ScalingLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ScalingLayout child, View dependency) {
        int totalScrollRange = ((AppBarLayout) dependency).getTotalScrollRange();
        child.setProgress((-dependency.getY()) / totalScrollRange);
        if (totalScrollRange + dependency.getY() > (float) child.getMeasuredHeight() / 2) {
            child.setTranslationY(totalScrollRange + dependency.getY() + toolbarHeightInPixel - (float) child.getMeasuredHeight() / 2);
        } else {
            child.setTranslationY(toolbarHeightInPixel);
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
