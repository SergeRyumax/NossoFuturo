package br.com.sergeryumax.nossofuturo;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
	View view;
	int startH;
	int endH;
	int diffH;
	private int startW;
	private int endW;
	private int diffW;

	public ResizeAnimation(View v, int newh, int newW) {
		view = v;
		startH = v.getLayoutParams().height;
		endH = newh;
		diffH = endH - startH;
		
		startW = v.getLayoutParams().width;
		endW = newW;
		diffW = endW - startW;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		view.getLayoutParams().height = startH
				+ (int) (diffH * interpolatedTime);
		view.getLayoutParams().width = startW
				+ (int) (diffW * interpolatedTime);
		view.requestLayout();
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
}
