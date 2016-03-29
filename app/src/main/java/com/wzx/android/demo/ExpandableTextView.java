package com.wzx.android.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.wzx.android.demo.v2.R;

/**
 * Created by wang_zx on 2014/12/29.
 */
public class ExpandableTextView extends TextView{

    private static final String TAG = ExpandableTextView.class.getSimpleName();

    private int mMaxCollapseLine = 1;
    private boolean mIsExpanded = false;

    private Drawable mCollapseButtonDrawable;
    private Drawable mExpandButtonDrawable;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyle, 0);
        mMaxCollapseLine = a.getInt(R.styleable.ExpandableTextView_max_collapse_line, 1);
        mIsExpanded = a.getBoolean(R.styleable.ExpandableTextView_is_expanded, false);
        mCollapseButtonDrawable = a.getDrawable(R.styleable.ExpandableTextView_collapse_button);
        if (mCollapseButtonDrawable != null) {
            mCollapseButtonDrawable.setBounds(0, 0, mCollapseButtonDrawable.getIntrinsicWidth(),
                    mCollapseButtonDrawable.getIntrinsicHeight());
        }
        mExpandButtonDrawable = a.getDrawable(R.styleable.ExpandableTextView_expand_button);
        if (mExpandButtonDrawable != null) {
            mExpandButtonDrawable.setBounds(0, 0, mExpandButtonDrawable.getIntrinsicWidth(),
                    mExpandButtonDrawable.getIntrinsicHeight());
        }
        a.recycle();
    }

    public void setExpanded(boolean expanded) {
        if (mIsExpanded != expanded) {
            mIsExpanded = expanded;
            requestLayout();
            invalidate();
        }
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMaxLines(Integer.MAX_VALUE);
        setCompoundDrawables(null, null, null, null);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int lineCount = getLineCount();
        if (lineCount > mMaxCollapseLine) {
            if (mIsExpanded) {
                setCompoundDrawables(null, null, null, mCollapseButtonDrawable);
            } else {
                setMaxLines(mMaxCollapseLine);
                setCompoundDrawables(null, null, null, mExpandButtonDrawable);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    static class SavedState extends BaseSavedState {
        boolean isExpanded;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isExpanded = (in.readInt() != 0);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isExpanded ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isExpanded = mIsExpanded;

        Log.i(TAG, "onSaveInstanceState");

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mIsExpanded = ss.isExpanded;
        Log.i(TAG, "onRestoreInstanceState");
    }
}
