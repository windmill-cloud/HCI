package edu.ucsb.cs.cs185.foliostation.Inbox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.Cards;
import edu.ucsb.cs.cs185.foliostation.collections.CardViewHolder;

/**
 * Created by xuanwang on 3/18/17.
 */

public class InboxInnerAdapter extends RecyclerView.Adapter<CardViewHolder>
        implements View.OnClickListener {

    List<Cards.CardImage> mCardImages;

    Context mContext = null;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public InboxInnerAdapter(Context context, List<Cards.CardImage> images){
        mContext = context;
        mCardImages = images;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_inbox_image, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {

        if(mContext == null){
            Log.e("mContext", "null");
        }

        Cards.CardImage cardImage = mCardImages.get(position);
        ImageView imageView = holder.imageView;

        if(cardImage.isFromPath()) {
            Picasso.with(mContext)
                    .load(new File(cardImage.mUrl))
                    .resize(220, 220)
                    .centerCrop()
                    .noFade()
                    .into(imageView);
        } else {
            Picasso.with(mContext)
                    .load(cardImage.mUrl)
                    .resize(220, 220)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //mFragment.startDetailDialog(mCardImages.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCardImages.size();
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