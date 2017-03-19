package edu.ucsb.cs.cs185.foliostation.Inbox;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.Cards;
import edu.ucsb.cs.cs185.foliostation.models.InboxCards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardViewHolder;
import edu.ucsb.cs.cs185.foliostation.mycollections.GridCardAdapter;
import edu.ucsb.cs.cs185.foliostation.searchbyranking.RankInnerAdapter;

/**
 * Created by Hilda on 18/03/2017.
 */

public class InboxGridAdapter extends RecyclerView.Adapter<CardViewHolder>
                                implements View.OnClickListener {
    private Activity mActivity;
    List<InboxCards.Card> mCards;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public InboxGridAdapter(List<ItemCards.Card> cards, Activity callingActivity){
        mCards = cards;
        mActivity = callingActivity;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final InboxCards.Card card = InboxCards.getInstance(mActivity).cards.get(position);

        if(card.getProfile().isFromPath()) {
            Picasso.with(mActivity)
                    .load(new File(card.getProfile().mUrl))
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .into(holder.profileImage);
        } else {
            Picasso.with(mActivity)
                    .load(card.getProfile().mUrl)
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .into(holder.profileImage);
        }

        holder.title.setText(card.getTitle());
        holder.tags.setText(card.getTagsString());
        holder.username.setText(card.getUsername());
        holder.description.setText(card.getDescription());

        RecyclerView rv = holder.rv;
        InboxInnerAdapter adapter =
                new InboxInnerAdapter(mActivity, mCards.get(position).getImages());
        adapter.setHasStableIds(true);

        GridLayoutManager gridLayoutManager;
        if(mCards.get(position).getImages().size() < 12) {
            gridLayoutManager = new GridLayoutManager(mActivity, 1, LinearLayoutManager.HORIZONTAL,
                    false);
        } else {
            gridLayoutManager = new GridLayoutManager(mActivity, 2, LinearLayoutManager.HORIZONTAL,
                    false);
        }

        gridLayoutManager.setItemPrefetchEnabled(true);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    //TODO: finish implementation in database
    private void addToMyCollection(int position){

    }

    //TODO: finish implementation in database
    private void deleteSharedItemFromDB(int position){

    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            // get tag
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_inbox, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    //define Item click interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
