
package com.onehook.widget.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * @author EagleDiao
 */
public abstract class BaseWrapperRecyclerViewAdapter<T extends BaseRecyclerViewAdapter> extends BaseRecyclerViewAdapter {

    final T mWrappedAdapter;
    private RecyclerView.AdapterDataObserver mObserver;

    public BaseWrapperRecyclerViewAdapter(T adapter) {
        mWrappedAdapter = adapter;
    }

    final public void onResume() {
        if (mWrappedAdapter != null && mObserver != null) {
            mWrappedAdapter.registerAdapterDataObserver(mObserver);
        }

        /* in case my wrapped adapter is also a wrapper */
        if (mWrappedAdapter != null && mWrappedAdapter instanceof BaseWrapperRecyclerViewAdapter) {
            ((BaseWrapperRecyclerViewAdapter) mWrappedAdapter).onResume();
        }
    }

    final public void onPause() {
        if (mWrappedAdapter != null && mObserver != null) {
            mWrappedAdapter.unregisterAdapterDataObserver(mObserver);
        }

        /* in case my wrapped adapter is also a wrapper */
        if (mWrappedAdapter != null && mWrappedAdapter instanceof BaseWrapperRecyclerViewAdapter) {
            ((BaseWrapperRecyclerViewAdapter) mWrappedAdapter).onPause();
        }
    }

    public void setDataObserver(RecyclerView.AdapterDataObserver observer) {
        mObserver = observer;
    }

    final public T getWrappedAdapter() {
        return mWrappedAdapter;
    }
}
