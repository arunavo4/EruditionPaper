package in.co.erudition.avatar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AvatarPlaceholder extends Drawable {
    public static final String DEFAULT_PLACEHOLDER_STRING = "-";
    private static final String DEFAULT_PLACEHOLDER_COLOR = "#3F51B5";
    private static final String COLOR_FORMAT = "#FF%06X";
    public static final int DEFAULT_TEXT_SIZE_PERCENTAGE = 33;

    private Paint textPaint;
    private Paint backgroundPaint;
    private RectF placeholderBounds;

    int circleRadius;
    int circleCenterXValue;
    int circleCenterYValue;

    private int viewSize;
    private Rect circleRect;

    private String avatarText;
    private int textSizePercentage;
    private String defaultString;

    private float textStartXPoint;
    private float textStartYPoint;

    private Context mContext;

    public AvatarPlaceholder(Context context, String name) {
        this(context, name, DEFAULT_TEXT_SIZE_PERCENTAGE, DEFAULT_PLACEHOLDER_STRING);
    }

    public AvatarPlaceholder(Context context, String name, @IntRange int textSizePercentage) {
        this(context, name, textSizePercentage, DEFAULT_PLACEHOLDER_STRING);
    }

    public AvatarPlaceholder(Context context, String name, @NonNull String defaultString) {
        this(context, name, DEFAULT_TEXT_SIZE_PERCENTAGE, defaultString);
    }

    public AvatarPlaceholder(Context context, String name, @IntRange int textSizePercentage, @NonNull String defaultString) {
        this.defaultString = resolveStringWhenNoName(defaultString);
        this.avatarText = convertNameToAvatarText(name);
        this.textSizePercentage = textSizePercentage;

        mContext = context;

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("white"));
        textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(getMatColor("500"));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        saveBasicValues(canvas);

        if (placeholderBounds == null) {
            placeholderBounds = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            setAvatarTextValues();
        }

        canvas.translate(circleCenterXValue, circleCenterYValue);

        //Draw Border
        canvas.drawCircle(circleRadius, circleRadius, circleRadius, backgroundPaint);

//        canvas.drawRect(placeholderBounds, backgroundPaint);
        canvas.drawText(avatarText, textStartXPoint, textStartYPoint, textPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
        backgroundPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        textPaint.setColorFilter(colorFilter);
        backgroundPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private void saveBasicValues(Canvas canvas) {
        int viewHeight = canvas.getHeight();
        int viewWidth = canvas.getWidth();

        viewSize = Math.min(viewWidth, viewHeight);

        circleCenterXValue = (viewWidth - viewSize) / 2;
        circleCenterYValue = (viewHeight - viewSize) / 2;
        circleRadius = (viewSize) / 2;

        circleRect = new Rect(0, 0, viewSize, viewSize);
    }


    private int getMatColor(String typeColor)
    {
        int returnColor = Color.BLACK;
        int arrayId = mContext.getResources().getIdentifier("mdcolor_" + typeColor, "array", mContext.getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

    private void setAvatarTextValues() {
        textPaint.setTextSize(calculateTextSize());
        textStartXPoint = calculateTextStartXPoint();
        textStartYPoint = calculateTextStartYPoint();
    }

    private float calculateTextStartXPoint() {
        float stringWidth = textPaint.measureText(avatarText);
        return (getBounds().width() / 2f) - (stringWidth / 2f);
    }

    private float calculateTextStartYPoint() {
        return (getBounds().height() / 2f) - ((textPaint.ascent() + textPaint.descent()) / 2f);
    }

    private String resolveStringWhenNoName(String stringWhenNoName) {
        return StringUtils.isNotNullOrEmpty(stringWhenNoName) ? stringWhenNoName : DEFAULT_PLACEHOLDER_STRING;
    }

    private String convertNameToAvatarText(String name) {
        return StringUtils.isNotNullOrEmpty(name) ? name.substring(0, 1).toUpperCase() : defaultString;
    }

    private String convertStringToColor(String text) {
        return StringUtils.isNullOrEmpty(text) ? DEFAULT_PLACEHOLDER_COLOR : String.format(COLOR_FORMAT, (0xFFFFFF & text.hashCode()));
    }

    private float calculateTextSize() {
        if (textSizePercentage < 0 || textSizePercentage > 100) {
            textSizePercentage = DEFAULT_TEXT_SIZE_PERCENTAGE;
        }
        return getBounds().height() * (float) textSizePercentage / 100;
    }
}
