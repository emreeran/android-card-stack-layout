package com.emreeran.cardstack;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by Emre Eran on 21/05/2017.
 */

public class CardStackLayout extends FrameLayout {

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;

    private static final int DURATION = 300;

    private int mLayoutWidth;
    private int mYMultiplier;

    private int mCurrentAdapterItem;
    private float mStackScale;
    private int mStackSize;
    private boolean mRepeat;

    private BaseAdapter mAdapter;
    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            refreshViewsFromAdapter();
        }

        @Override
        public void onInvalidated() {
            removeAllViews();
        }
    };

    private ArrayList<OnCardRemovedListener> mOnCardRemovedListenerArrayList;
    private ArrayList<OnCardCountChangedListener> mOnCardCountChangedListenerArrayList;
    private OnCardMovedListener mOnCardMovedListener;
    private OnCardReleasedListener mOnCardReleasedListener;

    private final OnCardCountChangedListener mAdapterOnCardCountChangedListener = new OnCardCountChangedListener() {
        @Override
        public void onAdd(int cardCount) {

        }

        @Override
        public void onRemove(int cardCount) {
            if (mCurrentAdapterItem < mAdapter.getCount()) {
                View view = mAdapter.getView(mCurrentAdapterItem, null, CardStackLayout.this);
                addCard(view);
                mCurrentAdapterItem++;
            } else if (mRepeat) {
                mCurrentAdapterItem = 0;
                View view = mAdapter.getView(mCurrentAdapterItem, null, CardStackLayout.this);
                addCard(view);
                mCurrentAdapterItem++;
            }
        }
    };

    public CardStackLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CardStackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CardStackLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public void addOnCardRemovedListener(OnCardRemovedListener onCardRemovedListener) {
        mOnCardRemovedListenerArrayList.add(onCardRemovedListener);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public void removeOnCardRemovedListener(OnCardRemovedListener onCardRemovedListener) {
        mOnCardRemovedListenerArrayList.remove(onCardRemovedListener);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public void addOnCardCountChangedListener(OnCardCountChangedListener onCardCountChangedListener) {
        mOnCardCountChangedListenerArrayList.add(onCardCountChangedListener);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public void removeOnCardCountChangedListener(OnCardCountChangedListener onCardCountChangedListener) {
        mOnCardCountChangedListenerArrayList.remove(onCardCountChangedListener);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public void setOnCardMovedListener(OnCardMovedListener onCardMovedListener) {
        mOnCardMovedListener = onCardMovedListener;
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public void setOnCardReleasedListener(OnCardReleasedListener onCardReleasedListener) {
        mOnCardReleasedListener = onCardReleasedListener;
    }

    @SuppressWarnings("WeakerAccess") // Public API
    public void addCard(View cardView) {
        CardStackItemContainerLayout cardStackItemContainerLayout = new CardStackItemContainerLayout(cardView.getContext());
        cardStackItemContainerLayout.addView(cardView);
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int childCount = getChildCount();
        addView(cardStackItemContainerLayout, 0, layoutParams);

        float scaleValue = 1 - (childCount / mStackScale);

        cardStackItemContainerLayout.animate()
                .x(0)
                .y(childCount * mYMultiplier)
                .scaleX(scaleValue)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setDuration(DURATION);
    }

    @SuppressWarnings("WeakerAccess") // Public API
    public void setAdapter(BaseAdapter adapter) {
        // Unregister observer if there was a previous adapter
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;

        // Register to new adapter if one is set
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }

        initViewsFromAdapter();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (mOnCardCountChangedListenerArrayList != null) {
            for (OnCardCountChangedListener listener : mOnCardCountChangedListenerArrayList) {
                listener.onAdd(getChildCount());
            }
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (mOnCardCountChangedListenerArrayList != null) {
            for (OnCardCountChangedListener listener : mOnCardCountChangedListenerArrayList) {
                listener.onRemove(getChildCount());
            }
        }
    }

    void onCardMoved(View view, float posX) {
        int childCount = getChildCount();
        for (int i = childCount - 2; i >= 0; i--) {
            CardStackItemContainerLayout tinderCardView = (CardStackItemContainerLayout) getChildAt(i);

            if (tinderCardView != null) {
                if (Math.abs(posX) == (float) mLayoutWidth) {
                    float scaleValue = 1 - ((childCount - 2 - i) / mStackScale);

                    tinderCardView.animate()
                            .x(0)
                            .y((childCount - 2 - i) * mYMultiplier)
                            .scaleX(scaleValue)
                            .rotation(0)
                            .setInterpolator(new AnticipateOvershootInterpolator())
                            .setDuration(DURATION);
                }
            }
        }

        if (mOnCardMovedListener != null) {
            mOnCardMovedListener.onMove(view);
        }
    }

    void onCardReleased(View view) {
        if (mOnCardReleasedListener != null) {
            mOnCardReleasedListener.onRelease(view);
        }
    }

    void onCardRemoved(int direction) {
        if (mOnCardRemovedListenerArrayList != null) {
            for (OnCardRemovedListener listener : mOnCardRemovedListenerArrayList)
                listener.onRemove(direction);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mOnCardRemovedListenerArrayList = new ArrayList<>();
        mOnCardCountChangedListenerArrayList = new ArrayList<>();
        setClipChildren(false);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLayoutWidth = getWidth();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardStackLayout);
        mStackSize = typedArray.getInteger(
                R.styleable.CardStackLayout_stack_size,
                getResources().getInteger(R.integer.card_stack_layout_default_stack_size)
        );
        mRepeat = typedArray.getBoolean(R.styleable.CardStackLayout_stack_repeat, false);
        mStackScale = typedArray.getFloat(
                R.styleable.CardStackLayout_stack_scale,
                getResources().getInteger(R.integer.card_stack_layout_default_stack_size)
        );
        typedArray.recycle();

        mYMultiplier = getResources().getDimensionPixelSize(R.dimen.card_stack_layout_child_size_multiplier);
    }

    private void initViewsFromAdapter() {
        removeAllViews();
        removeOnCardCountChangedListener(mAdapterOnCardCountChangedListener);

        if (mAdapter != null) {
            for (mCurrentAdapterItem = 0;
                 mCurrentAdapterItem < mStackSize && mCurrentAdapterItem < mAdapter.getCount();
                 mCurrentAdapterItem++) {
                View view = mAdapter.getView(mCurrentAdapterItem, null, this);
                addCard(view);
            }

            addOnCardCountChangedListener(mAdapterOnCardCountChangedListener);
        }
    }

    private void refreshViewsFromAdapter() {
        int childCount = getChildCount();
        int adapterSize = mAdapter.getCount();
        int reuseCount = Math.min(childCount, adapterSize);

        for (int i = 0; i < reuseCount; i++) {
            mAdapter.getView(i, getChildAt(i), this);
        }

        if (childCount < adapterSize) {
            for (int i = childCount; i < adapterSize; i++) {
                addView(mAdapter.getView(i, null, this), i);
            }
        } else if (childCount > adapterSize) {
            removeViews(adapterSize, childCount);
        }
    }

    @SuppressWarnings("WeakerAccess") // Public API
    public interface OnCardRemovedListener {
        void onRemove(int direction);
    }

    @SuppressWarnings("WeakerAccess") // Public API
    public interface OnCardCountChangedListener {
        void onAdd(int cardCount);

        void onRemove(int cardCount);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public interface OnCardMovedListener {
        void onMove(View view);
    }

    @SuppressWarnings("WeakerAccess") // Public API
    public interface OnCardReleasedListener {
        void onRelease(View view);
    }

    @SuppressWarnings("WeakerAccess unused") // Public API
    public static abstract class CardStackAdapter<T extends ViewHolder> extends BaseAdapter {

        @Override
        public int getCount() {
            return getItemCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            T holder;
            if (convertView == null) {
                int viewType = getItemViewType(position);
                holder = onCreateViewHolder(parent, viewType);
                convertView = holder.itemView;
                convertView.setTag(R.id.holder_tag, holder);
            }

            holder = (T) convertView.getTag(R.id.holder_tag);
            onBindViewHolder(holder, position);

            return convertView;
        }

        public abstract T onCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(T holder, int position);

        public abstract int getItemCount();
    }

    @SuppressWarnings("WeakerAccess") // Public API
    public static class ViewHolder {
        protected View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
