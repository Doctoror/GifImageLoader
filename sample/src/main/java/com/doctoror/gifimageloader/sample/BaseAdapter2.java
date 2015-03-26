package com.doctoror.gifimageloader.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extended {@link BaseAdapter} which contains items {@link List}, {@link Context} and {@link
 * LayoutInflater}.
 */
public abstract class BaseAdapter2<T> extends BaseAdapter {

    private final ArrayList<T> mItems = new ArrayList<>();

    @NonNull
    private final Context mContext;

    @NonNull
    private final LayoutInflater mLayoutInflater;

    public BaseAdapter2(@NonNull final Context context) {
        this(context, (List<T>) null);
    }

    public BaseAdapter2(@NonNull final Context context, @Nullable final T[] items) {
        this(context, items != null ? Arrays.asList(items) : null);
    }

    public BaseAdapter2(@NonNull final Context context, @Nullable final List<T> items) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (items != null) {
            mItems.addAll(items);
        }
    }

    protected final List<T> getItems() {
        return mItems;
    }

    public void updateData(@Nullable final List<T> data) {
        if (data == null && mItems.isEmpty() || data != null && mItems.equals(data)) {
            return;
        }
        mItems.clear();
        if (data != null) {
            mItems.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    protected final Context getContext() {
        return mContext;
    }

    @NonNull
    protected final LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public T getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }
}
