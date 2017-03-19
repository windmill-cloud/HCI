/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.collections;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.editentry.EditTabsActivity;
import edu.ucsb.cs.cs185.foliostation.share.ShareActivity;

/**
 * Created by xuanwang on 2/19/17.
 */

public class GridCardAdapter extends RecyclerView.Adapter<CardViewHolder>
        implements View.OnClickListener {

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    List<ItemCards.Card> mCards;

    static Context mContext = null;

    public GridCardAdapter(List<ItemCards.Card> cards){
        mCards = cards;
    }

    static void setContext(Context context){
        mContext = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_grid, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int i) {

        final int position = i;

        if(mContext == null){
            Log.e("mContext", "null");
        }
        final ItemCards.Card card = ItemCards.getInstance(mContext).cards.get(i);

        // TODO: refactor picture loading
        if(card.getCoverImage().isFromPath()) {
            Picasso.with(mContext)
                    .load(new File(card.getCoverImage().mUrl))
                    .resize(450, 450)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        } else {
            Picasso.with(mContext)
                    .load(card.getCoverImage().mUrl)
                    .resize(450, 450)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        }

        holder.imageView.setTag(i);

        holder.imageView.setOnClickListener(this);
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                startEditActivity(view, position);
                return true;
            }
        });

        // set title
        holder.title.setText(card.getTitle());

        // set description
        if(card.getDescription().equals("")){
            holder.description.setText(card.getDescription());
        }else {  // limit description size
            String[] limitText = card.getDescription().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int t = 0; t < Math.min(limitText.length, 10); t++) {
                sb.append(limitText[t]).append(" ");
            }
            sb.setLength(sb.length() - 2);
            sb.append("...");
            holder.description.setText(sb.toString());
        }

        //if(card.getTags().size()==0){
            //holder.tags.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,0));

        //}else {
            // set Tags
            StringBuilder sb_tag = new StringBuilder();
            for(int g = 0; g < card.getTags().size(); g++){
                sb_tag.append(card.getTags().get(g)).append(" ");
            }
            holder.tags.setText(sb_tag.toString());
        //}



        holder.toolbar.getMenu().clear();
        holder.toolbar.inflateMenu(R.menu.card_toolbar);

        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.card_toolbar_share:
                        startShareActivity(holder.imageView, position);
                        break;
                    case R.id.card_toolbar_edit:
                        Log.i("selected", "edit");
                        startEditActivity(holder.imageView, position);
                        break;
                    case R.id.card_toolbar_delete:
                        Log.i("selected", "delete");
                        new AlertDialog.Builder(mContext)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        ItemCards.getInstance(mContext).deleteIthCard(position);
                                        GridCardAdapter.this.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                        break;
                }
                return true;
            }
        });

        if (card.hasMultiPics()){
            holder.hasMultiPics.setVisibility(View.VISIBLE);
        } else {
            holder.hasMultiPics.setVisibility(View.GONE);
        }
    }

    public void startShareActivity(View view, int position){
        Intent intent = new Intent(view.getContext(), ShareActivity.class);
        intent.putExtra("CARD_INDEX", position);
        intent.putExtra("FROM", "GRID");
        view.getContext().startActivity(intent);
    }

    public void startEditActivity(View view, int position){
        Intent intent = new Intent(view.getContext(), EditTabsActivity.class);
        intent.putExtra("CARD_INDEX", position);
        intent.putExtra("EDIT", true);
        view.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    @Override public long getItemId(int position) { return position; }

    /**
    * Handlers for click listeners
     */


    /**
     * Item click
     */

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
