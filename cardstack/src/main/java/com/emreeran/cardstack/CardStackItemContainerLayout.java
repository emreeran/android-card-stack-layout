package com.emreeran.cardstack;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

/**
 * Created by Emre Eran on 21/05/2017.
 */

class CardStackItemContainerLayout extends FrameLayout implements View.OnTouchListener {
    private static final float CARD_ROTATION_DEGREES = 40.0f;
    private static final int DURATION = 300;

    private float mOldX;
    private float mOldY;
    private float mRightBoundary;
    private float mLeftBoundary;
    private int mViewWidth;
    private int mPadding;

    public CardStackItemContainerLayout(Context context) {
        super(context);
        init();
    }

    public CardStackItemContainerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardStackItemContainerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
        CardStackLayout tinderStackLayout = ((CardStackLayout) view.getParent());
        CardStackItemContainerLayout topCard = (CardStackItemContainerLayout) tinderStackLayout.getChildAt(tinderStackLayout.getChildCount() - 1);
        if (topCard.equals(view)) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOldX = motionEvent.getX();
                    mOldY = motionEvent.getY();
                    view.clearAnimation();

                    return true;
                case MotionEvent.ACTION_UP:
                    if (isCardBeyondLeftBoundary(view)) {
                        tinderStackLayout.onCardMoved(view, -(mViewWidth));
                        dismissCard(view, -(mViewWidth * 2));
                    } else if (isCardBeyondRightBoundary(view)) {
                        tinderStackLayout.onCardMoved(view, mViewWidth);
                        dismissCard(view, (mViewWidth * 2));
                    } else {
                        tinderStackLayout.onCardMoved(view, 0);
                        resetCard(view);
                    }

                    return true;
                case MotionEvent.ACTION_MOVE:
                    float newX = motionEvent.getX();
                    float newY = motionEvent.getY();

                    float dX = newX - mOldX;
                    float dY = newY - mOldY;

                    float posX = view.getX() + dX;
                    float posY = view.getY() + dY;

                    tinderStackLayout.onCardMoved(view, posX);

                    // Set new position
                    view.setX(posX);
                    view.setY(posY);

                    setCardRotation(view, view.getX());

                    return true;
                default:
                    return super.onTouchEvent(motionEvent);
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnTouchListener(null);
    }

    private void init() {
        if (!isInEditMode()) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mViewWidth = getWidth();
                    mLeftBoundary = mViewWidth * (1.0f / 6.0f); // Left 1/6 of screen
                    mRightBoundary = mViewWidth * (5.0f / 6.0f); // Right 1/6 of screen
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            mPadding = getResources().getDimensionPixelSize(R.dimen.card_stack_item_view_padding);
            setOnTouchListener(this);
        }
    }

    private boolean isCardBeyondLeftBoundary(View view) {
        return (view.getX() + (view.getWidth() / 2) < mLeftBoundary);
    }

    private boolean isCardBeyondRightBoundary(View view) {
        return (view.getX() + (view.getWidth() / 2) > mRightBoundary);
    }

    private void dismissCard(final View view, int xPos) {
        view.animate()
                .x(xPos)
                .y(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ViewGroup viewGroup = (ViewGroup) view.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(view);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    private void resetCard(View view) {
        view.animate()
                .x(0)
                .y(0)
                .rotation(0)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(DURATION);

        CardStackLayout tinderStackLayout = ((CardStackLayout) view.getParent());
        tinderStackLayout.onCardReleased(view);
    }

    private void setCardRotation(View view, float posX) {
        float rotation = (CARD_ROTATION_DEGREES * (posX)) / mViewWidth;
        int halfCardHeight = (view.getHeight() / 2);
        if (mOldY < halfCardHeight - (2 * mPadding)) {
            view.setRotation(rotation);
        } else {
            view.setRotation(-rotation);
        }
    }
}
