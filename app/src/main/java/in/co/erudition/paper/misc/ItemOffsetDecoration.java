package in.co.erudition.paper.misc;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Arunavo Ray on 31-03-2018.
 */

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
            }

    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
            }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if(mItemOffset<=10){
                outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
            }
            else{
                outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
            }
    }
}
