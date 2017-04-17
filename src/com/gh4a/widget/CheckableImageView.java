package com.gh4a.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Checkable;

public class CheckableImageView extends AppCompatImageView implements Checkable {
    private boolean mChecked;

    public CheckableImageView(Context context) {
        super(context);
        setClickable(true);
    }

    public CheckableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
    }

    public CheckableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        if (checked) {
            setColorFilter(0xffff0000);
        } else {
            setColorFilter(null);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public boolean performClick() {
        toggle();
        return true;
    }
}
