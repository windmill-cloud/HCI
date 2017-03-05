/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.discover;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import edu.ucsb.cs.cs185.foliostation.Cards;
import edu.ucsb.cs.cs185.foliostation.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardViewHolder;

/**
 * Created by xuanwang on 3/4/17.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<CardViewHolder>
        implements View.OnClickListener {
    List<ItemCards.Card> mCards;

    Context mContext = null;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public DiscoverAdapter(Context context, List<ItemCards.Card> images){
        mContext = context;
        mCards = images;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_discover, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {

        if(mContext == null){
            Log.e("mContext", "null");
        }

        Cards.CardImage coverImage = mCards.get(position).getCoverImage();
        // TODO: refactor picture loading
        if(coverImage.isFromPath()) {
            Picasso.with(mContext)
                    .load(new File(coverImage.mUrl))
                    .resize(450, 450)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        } else {
            Picasso.with(mContext)
                    .load(coverImage.mUrl)
                    .resize(450, 450)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        }

        holder.imageView.setOnClickListener(this);
        holder.imageView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mCards.size();
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
