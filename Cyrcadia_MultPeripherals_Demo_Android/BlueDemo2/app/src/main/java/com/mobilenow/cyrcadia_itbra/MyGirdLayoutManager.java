package com.mobilenow.cyrcadia_itbra;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class MyGirdLayoutManager extends GridLayoutManager {
    public MyGirdLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyGirdLayoutManager(Context context, int spanCount, @RecyclerView.Orientation int
            orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public MyGirdLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
