package com.rd.draw.drawer.type;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.rd.animation.data.Value;
import com.rd.animation.data.type.SwapAnimationValue;
import com.rd.draw.data.Indicator;
import com.rd.draw.data.Orientation;

public class SwapDrawer extends BaseDrawer {

    public SwapDrawer(@NonNull Paint paint, @NonNull Indicator indicator) {
        super(paint, indicator);
    }

    public void draw(
            @NonNull Canvas canvas,
            @NonNull Value value,
            int position,
            int coordinateX,
            int coordinateY) {

        if (!(value instanceof SwapAnimationValue)) {
            return;
        }

        SwapAnimationValue v = (SwapAnimationValue) value;
        int selectedColor = indicator.getSelectedColor();
        int unselectedColor = indicator.getUnselectedColor();
        int radius = indicator.getRadius();
        int rectWidth = indicator.getRectWidth();
        int rectHeight = indicator.getRectHeight();

        int selectedPosition = indicator.getSelectedPosition();
        int selectingPosition = indicator.getSelectingPosition();
        int lastSelectedPosition = indicator.getLastSelectedPosition();

        int coordinate = v.getCoordinate();
        int color = unselectedColor;

        if (indicator.isInteractiveAnimation()) {
            if (position == selectingPosition) {
                coordinate = v.getCoordinate();
                color = selectedColor;

            } else if (position == selectedPosition) {
                coordinate = v.getCoordinateReverse();
                color = unselectedColor;
            }

        } else {
            if (position == lastSelectedPosition) {
                coordinate = v.getCoordinate();
                color = selectedColor;

            } else if (position == selectedPosition) {
                coordinate = v.getCoordinateReverse();
                color = unselectedColor;
            }
        }

        paint.setColor(color);
        int swapX = coordinate;
        int swapY = coordinateY;
        if (indicator.getOrientation() == Orientation.VERTICAL) {
            swapX = coordinateX;
            swapY = coordinate;
        }

       drawIndicator(canvas,paint,swapX,swapY);
    }
}
