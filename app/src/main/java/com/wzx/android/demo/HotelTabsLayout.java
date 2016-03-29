package com.wzx.android.demo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by wang_zx on 2015/4/27.
 */
public class HotelTabsLayout extends LinearLayout implements View.OnClickListener{

    private int mSelectedChildIndex = -1;

    public HotelTabsLayout(Context context) {
        this(context, null);
    }

    public HotelTabsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        child.setOnClickListener(this);
        int childIndex = indexOfChild(child);
        child.setSelected(mSelectedChildIndex == childIndex);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedChildIndex = getSelectedChildIndex();
        if (selectedChildIndex >= 0) {
            //最后绘制选中的child
            if (i == childCount - 1) {
                return selectedChildIndex;
            } else if (i >= selectedChildIndex) {
                return i + 1;
            } else if (i < selectedChildIndex) {
                return i;
            }
        }
        return super.getChildDrawingOrder(childCount, i);
    }

    public void setSelected(int index) {
        int childrenCount = getChildCount();
        if (index != mSelectedChildIndex && index < childrenCount) {
            for (int i= 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                child.setSelected(i == index);
            }
            mSelectedChildIndex = index;
            if (mOnSelectedChangeListener != null) {
                mOnSelectedChangeListener.onSelectedChange(this, mSelectedChildIndex);
            }
            invalidate();
        }
    }


    public int getSelectedChildIndex() {
        return mSelectedChildIndex;
    }

    public View getSelectedChild() {
        if (mSelectedChildIndex >= 0 && mSelectedChildIndex < getChildCount()) {
            return getChildAt(mSelectedChildIndex);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        int index = indexOfChild(v);
        setSelected(index);
    }

    private OnSelectedChangeListener mOnSelectedChangeListener;

    public void setOnSelectedChangeListener(OnSelectedChangeListener listener) {
        mOnSelectedChangeListener = listener;
    }


    public static interface OnSelectedChangeListener {
        void onSelectedChange(HotelTabsLayout layout, int selectedIndex);
    }

    static class SavedState extends View.BaseSavedState {
        int selectedChildIndex;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selectedChildIndex = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selectedChildIndex);
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

        ss.selectedChildIndex = mSelectedChildIndex;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setSelected(ss.selectedChildIndex);
    }
}
