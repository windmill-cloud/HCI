package edu.ucsb.cs.cs185.foliostation.tagandimages;

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
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.collections.CardViewHolder;
import edu.ucsb.cs.cs185.foliostation.utilities.PicassoImageLoader;

/**
 * Created by xuanwang on 3/17/17.
 */

public class TagAndImagesAdapter extends RecyclerView.Adapter<CardViewHolder>
        implements View.OnClickListener {

    List<ItemCards.CardImage> mImages;

    Context mContext = null;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public TagAndImagesAdapter(Context context, List<ItemCards.CardImage> images){
        mContext = context;
        mImages = images;
    }

    public void updateImages(List<ItemCards.CardImage> images){
        mImages = images;
        this.notifyDataSetChanged();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tagandimages, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        if(mContext == null){
            Log.e("mContext", "null");
        }

        ImageView imageView = holder.imageView;
        ItemCards.CardImage cardImage = mImages.get(position);

        PicassoImageLoader.loadImageToView(mContext,
                cardImage, imageView, 500, 500);

        imageView.setTag(position);
        imageView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
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
