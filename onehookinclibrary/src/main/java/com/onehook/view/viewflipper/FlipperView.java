package com.onehook.view.viewflipper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.onehook.util.animator.AnimationEndListener;
import com.onehookinc.androidlib.R;

/**
 * Created by EagleDiao on 2016-11-15.
 */

public class FlipperView extends FrameLayout {

    private static final long ANIMATION_DURATION = 150;

    public interface FlipperViewCallback {

        /**
         * Called when next bottom page will be presented.
         *
         * @param nextPage next page
         * @return true if next page should be presented, false otherwise
         */
        boolean onWillPresentNextBottomPage(final View nextPage);
    }

    public FlipperView(Context context, final int resID) {
        super(context);
        commonInit(resID);
    }

    public FlipperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonInit(context, attrs);
    }

    public FlipperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit(context, attrs);
    }

    @TargetApi(21)
    public FlipperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        commonInit(context, attrs);
    }

    private void commonInit(final Context context, final AttributeSet attrs) {
        TypedArray customAttr = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FlipperView,
                0, 0);

        int viewRes = customAttr.getResourceId(R.styleable.FlipperView_viewRes, 0);
        mBottomTranslationY = customAttr.getDimension(R.styleable.FlipperView_bottomPageTranslationY, 0);
        mBottomScale = customAttr.getFloat(R.styleable.FlipperView_bottomPageScale, 1);
        commonInit(viewRes);
    }

    private float mBottomTranslationY;

    private float mBottomScale;

    private View mFrontPage;

    private View mBottomPage;

    private FlipperViewCallback mCallback;

    private void commonInit(final int res) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        mFrontPage = inflater.inflate(res, this, false);
        mBottomPage = inflater.inflate(res, this, false);
        addView(mBottomPage);
        addView(mFrontPage);

        mBottomPage.setTranslationY(mBottomTranslationY);
        mBottomPage.setScaleX(mBottomScale);
        mBottomPage.setScaleY(mBottomScale);
        mBottomPage.setVisibility(View.GONE);
    }

    public void setCallback(final FlipperViewCallback callback) {
        mCallback = callback;
    }

    public View getFrontPage() {
        return mFrontPage;
    }

    public View getBottomPage() {
        return mBottomPage;
    }

    public void flipPage(final boolean swipeLeft) {
        ObjectAnimator frontMoveLeftAnimator = ObjectAnimator.ofFloat(mFrontPage, "translationX", swipeLeft ? -mFrontPage.getMeasuredWidth() : mFrontPage.getMeasuredWidth());
        ObjectAnimator bottomMoveUpAnimator = ObjectAnimator.ofFloat(mBottomPage, "translationY", 0);
        ObjectAnimator bottomScaleXAnimator = ObjectAnimator.ofFloat(mBottomPage, "scaleX", 1);
        ObjectAnimator bottomScaleYAnimator = ObjectAnimator.ofFloat(mBottomPage, "scaleY", 1);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(frontMoveLeftAnimator, bottomMoveUpAnimator, bottomScaleXAnimator, bottomScaleYAnimator);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.addListener(new AnimationEndListener() {
            @Override
            public void onAnimationEndOrCanceled(Animator animation) {
                if (mCallback != null) {
                    final boolean hasNextPage = mCallback.onWillPresentNextBottomPage(mFrontPage);
                    doSwap(hasNextPage);
                } else {
                    doSwap(true);
                }
            }
        });
        animatorSet.start();

        mFrontPage.setVisibility(View.VISIBLE);
        mBottomPage.setVisibility(View.VISIBLE);
    }

    private void doSwap(final boolean hasNext) {
        mBottomPage.bringToFront();
        mFrontPage.setTranslationX(0);
        ObjectAnimator bottomMoveDownAnimator = ObjectAnimator.ofFloat(mFrontPage, "translationY", hasNext ? mBottomTranslationY : 1);
        ObjectAnimator bottomScaleXAnimator = ObjectAnimator.ofFloat(mFrontPage, "scaleX", hasNext ? mBottomScale : 1);
        ObjectAnimator bottomScaleYAnimator = ObjectAnimator.ofFloat(mFrontPage, "scaleY", hasNext ? mBottomScale : 1);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(bottomMoveDownAnimator, bottomScaleXAnimator, bottomScaleYAnimator);
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.start();

        final View temp = mFrontPage;
        mFrontPage = mBottomPage;
        mBottomPage = temp;

        mFrontPage.setVisibility(View.VISIBLE);
        mBottomPage.setVisibility(View.GONE);
    }

}
