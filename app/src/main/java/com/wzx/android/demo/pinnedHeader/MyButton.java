package com.wzx.android.demo.pinnedHeader;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button{
	
	private static final String TAG = MyButton.class.getSimpleName();

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	/*/*


	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		ViewParent parent = getParent();
		while (parent instanceof ViewGroup) {
			Log.i(TAG, parent.toString());
			if (parent instanceof ListView) {
				mAncestorListView = new WeakReference<ListView>((ListView) parent);
				break;
			}
			parent = parent.getParent();
		}
	}*//*
	
	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		Log.i(TAG, "setPressed:" + pressed);
		super.setPressed(pressed);
	}
	
	@Override
	protected void drawableStateChanged() {
		// TODO Auto-generated method stub
		super.drawableStateChanged();
		Log.i(TAG, "drawableStateChanged:" + isPressed());
		*//*if (mAncestorListView != null) {
			View v = mAncestorListView.get();
			if (v != null) {
				v.invalidate();
			}
		}*//*
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "getBackground(): " + getBackground() + "  " + toString());
		Log.i(TAG, "draw: " + isPressed() + "  " + toString());
		
*//*
		try {
			Class<?> clazz = View.class;
			Field field = clazz.getDeclaredField("PFLAG_DIRTY_OPAQUE");
			field.setAccessible(true);
			Log.i(TAG, "PFLAG_DIRTY_OPAQUE: " + field.getInt(this));
			field.setAccessible(false);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Class<?> clazz = View.class;
			Field field = clazz.getDeclaredField("mPrivateFlags");
			field.setAccessible(true);
			Log.i(TAG, "mPrivateFlags: " + field.getInt(this));
			field.setAccessible(false);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Class<?> clazz = View.class;
			Field field = clazz.getDeclaredField("mAttachInfo");
			field.setAccessible(true);
			Object attachInfo = field.get(this);
			if (attachInfo == null) {

				Log.i(TAG, "attachInfo == null");
			} else {
				Class<?> clz = field.getClass();
				Field _field = clz.getDeclaredField("mIgnoreDirtyState");
				_field.setAccessible(true);
				boolean ignoreDirtyState = _field.getBoolean(attachInfo);
				Log.i(TAG, "ignoreDirtyState: " + ignoreDirtyState);
				_field.setAccessible(false);
			}
			field.setAccessible(false);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*//*
		super.draw(canvas);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDraw: " + isPressed() + "  " + toString());
		super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		Log.i(TAG, String.format("[%d, %f, %f]%s", action, x, y, hashCode()));
		return super.onTouchEvent(event);
	}
	
	*//*@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		// TODO Auto-generated method stub
		int[] state = super.onCreateDrawableState(extraSpace);
		
		int [] _state = new int[state.length + 1];
		for (int i = 0; i < state.length; i++) {
			_state[i] = state[i];
		}
		_state[state.length] = android.R.attr.state_window_focused;
		return _state;
	}*//*
*/
}
