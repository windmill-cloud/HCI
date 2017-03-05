/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.inspire;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardViewHolder;

/**
 * Created by xuanwang on 3/5/17.
 */

public class InspireAdapter extends RecyclerView.Adapter<CardViewHolder>
        implements View.OnClickListener {

    List<ItemCards.TagAndImages> mTagAndImages;

    Context mContext = null;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public InspireAdapter(Context context, List<ItemCards.TagAndImages> images){
        mContext = context;
        mTagAndImages = images;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_inspire, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {

        if(mContext == null){
            Log.e("mContext", "null");
        }
        holder.title.setText(mTagAndImages.get(position).tag);

        RecyclerView rv = holder.rv;
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        InspireCardAdapter adapter =
                new InspireCardAdapter(mContext, mTagAndImages.get(position).cardImages);
        adapter.setHasStableIds(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(true);

        rv.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnItemClickListener(new InspireCardAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){

            }
        });

        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTagAndImages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //define Item click interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            // get tag
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
