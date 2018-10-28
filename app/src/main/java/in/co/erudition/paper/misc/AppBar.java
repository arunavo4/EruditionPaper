package in.co.erudition.paper.misc;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import com.google.android.material.appbar.AppBarLayout;
import android.util.AttributeSet;
import android.view.WindowInsets;

public class AppBar extends AppBarLayout {
    public AppBar(Context context) {
        super(context);
    }

    public AppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; ++index)
            getChildAt(index).dispatchApplyWindowInsets(insets);
        // let children know about WindowInsets

        return insets;
    }
}
