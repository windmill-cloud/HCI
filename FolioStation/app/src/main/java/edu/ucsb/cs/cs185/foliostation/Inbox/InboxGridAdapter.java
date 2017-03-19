package edu.ucsb.cs.cs185.foliostation.Inbox;

import android.app.Activity;
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
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardViewHolder;
import edu.ucsb.cs.cs185.foliostation.mycollections.GridCardAdapter;

/**
 * Created by Hilda on 18/03/2017.
 */

public class InboxGridAdapter extends RecyclerView.Adapter<CardViewHolder>
                                implements View.OnClickListener {
    private Activity mActivity;
    List<ItemCards.Card> mCards;
    private GridCardAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public InboxGridAdapter(List<ItemCards.Card> cards, Activity callingActivity){
        mCards = cards;
        mActivity = callingActivity;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final ItemCards.Card card = ItemCards.getInstance(mActivity).cards.get(position);

        // TODO: refactor picture loading
        if(card.getCoverImage().isFromPath()) {
            Picasso.with(mActivity)
                    .load(new File(card.getCoverImage().mUrl))
                    .resize(450, 450)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        } else {
            Picasso.with(mActivity)
                    .load(card.getCoverImage().mUrl)
                    .resize(450, 450)
                    .centerCrop()
                    .noFade()
                    .into(holder.imageView);
        }

        holder.imageView.setTag(position);

        holder.imageView.setOnClickListener(this);

        holder.title.setText(card.getTitle());
        holder.description.setText(card.getDescription());

        holder.toolbar.getMenu().clear();

        holder.toolbar.inflateMenu(R.menu.inbox_options);

        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.inbox_option_add:
                        addToMyCollection(position);
                        break;
                    case R.id.inbox_option_ignore:
                        deleteSharedItemFromDB(position);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_grid, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);

        return cardViewHolder;
    }

    public void setOnItemClickListener(GridCardAdapter.OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
