/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.inspirante.editentry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ucsb.cs.cs185.inspirante.collections.CardViewHolder;
import edu.ucsb.cs.cs185.inspirante.models.ItemCards;
import edu.ucsb.cs.cs185.inspirante.R;
import edu.ucsb.cs.cs185.inspirante.utilities.PicassoImageLoader;

/**
 * Created by xuanwang on 2/24/17.
 */

public class SelectCoverAdapter extends RecyclerView.Adapter<CardViewHolder>
    implements View.OnClickListener {

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    ItemCards.Card mCard;
    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    static Context mContext = null;

    public SelectCoverAdapter(ItemCards.Card Card) {
        mCard = Card;
    }

    static void setContext(Context context) {
        mContext = context;
    }

    public void updateData(ItemCards.Card Card) {
        mCard = Card;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_edit, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (parent != null) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int i) {

        if (mContext == null) {
            Log.e("mContext", "null");
        }

        ItemCards.CardImage image =  mCard.getImages().get(i);
        if(i == mCard.coverIndex){
            holder.checked.setImageResource(R.drawable.ic_check_circle_white_24dp);
            sendViewToBack(holder.mask);

            holder.mask.setColorFilter(android.R.color.transparent);
        } else {
            holder.checked.setImageResource(android.R.color.transparent);
            holder.mask.setBackgroundColor(mContext.getResources().getColor(R.color.colorGrayTransparent));
            holder.mask.bringToFront();
        }

        PicassoImageLoader.loadImageToView(mContext,
                image, holder.imageView, 450, 450);

        holder.imageView.setTag(i);
        holder.imageView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mCard.getImages().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            // get tag
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public void setOnItemClickListener(SelectCoverAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}