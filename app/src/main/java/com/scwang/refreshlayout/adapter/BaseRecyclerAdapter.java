package com.scwang.refreshlayout.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Created by SCWANG on 2017/6/11.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<SmartViewHolder> {

    private final int mLayoutId;
    private final List<T> mList;
    private AdapterView.OnItemClickListener mListener;

    public BaseRecyclerAdapter(@LayoutRes int layoutId) {
        setHasStableIds(false);
        this.mList = new ArrayList<>();
        this.mLayoutId = layoutId;
    }

    public BaseRecyclerAdapter(Collection<T> collection, @LayoutRes int layoutId) {
        setHasStableIds(false);
        this.mList = new ArrayList<>(collection);
        this.mLayoutId = layoutId;
    }

    public BaseRecyclerAdapter(Collection<T> collection, @LayoutRes int layoutId, AdapterView.OnItemClickListener listener) {
        setHasStableIds(false);
        setOnItemClickListener(listener);
        this.mList = new ArrayList<>(collection);
        this.mLayoutId = layoutId;
    }

    public BaseRecyclerAdapter<T> setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false),mListener);
    }

    @Override
    public void onBindViewHolder(SmartViewHolder holder, int position) {
        onBindViewHolder(holder, mList.get(position), position);
    }

    protected abstract void onBindViewHolder(SmartViewHolder holder, T model, int position);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public BaseRecyclerAdapter<T> refresh(Collection<T> collection) {
        mList.clear();
        mList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }

    public BaseRecyclerAdapter<T> loadmore(Collection<T> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }
}
