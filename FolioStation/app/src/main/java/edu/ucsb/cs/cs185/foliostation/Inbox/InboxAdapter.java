package edu.ucsb.cs.cs185.foliostation.Inbox;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.databasehandlers.DatabaseOperator;
import edu.ucsb.cs.cs185.foliostation.models.InboxCards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.collections.CardViewHolder;
import edu.ucsb.cs.cs185.foliostation.utilities.PicassoImageLoader;

/**
 * Created by Hilda on 18/03/2017.
 */

public class InboxAdapter extends RecyclerView.Adapter<CardViewHolder>
                                implements View.OnClickListener {
    private Activity mActivity;
    List<InboxCards.Card> mCards;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public InboxAdapter(List<ItemCards.Card> cards, Activity callingActivity){
        mCards = cards;
        mActivity = callingActivity;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        final InboxCards.Card card = InboxCards.getInstance(mActivity).cards.get(position);

        PicassoImageLoader.loadImageToView(mActivity,
                card.getProfile(), holder.profileImage, 200, 200);

        setTextView(holder.title, card.getTitle());
        setTextView(holder.tags, card.getTagsString());
        setTextView(holder.description, card.getDescription());

        holder.username.setText(card.getUsername());

        // set toolbar behaviors
        holder.toolbar.getMenu().clear();
        holder.toolbar.inflateMenu(R.menu.menu_inbox);
        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:{
                        InboxCards.Card card = mCards.get(position);
                        ItemCards.getInstance(mActivity).addNewCardFromInboxCard(card);
                        InboxAdapter.this.notifyDataSetChanged();

                        Toast toast = Toast.makeText(mActivity, "Saved to local collections",
                                Toast.LENGTH_SHORT);

                        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                        toastMessage.setTextColor(Color.WHITE);
                        toast.show();
                        break;
                    }
                    case R.id.action_mark_as_read: {
                        InboxCards.Card card = mCards.get(position);
                        card.setRead();
                        InboxAdapter.this.notifyDataSetChanged();
                        DatabaseOperator.getInstance(mActivity)
                                .getInboxCardsDBOperator().updateCards(card);

                        Toast toast = Toast.makeText(mActivity, "Marked as read",
                                Toast.LENGTH_SHORT);

                        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                        toastMessage.setTextColor(Color.WHITE);
                        toast.show();
                        break;
                    }
                    case R.id.action_delete:
                        InboxCards.Card card = mCards.get(position);
                        DatabaseOperator.getInstance(mActivity)
                                .getInboxCardsDBOperator().deleteCard(card);

                        mCards.remove(position);
                        if(mCards.size() == 0){
                            mActivity.finish();
                        } else {
                            InboxAdapter.this.notifyDataSetChanged();
                        }

                        break;
                }
                return true;
            }
        });

        if(card.isRead()){
            holder.toolbar.setBackgroundColor(
                    mActivity.getResources().getColor(R.color.colorLightLightGray));
            holder.cv.setCardBackgroundColor(
                    mActivity.getResources().getColor(R.color.colorLightLightGray));
        }

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

    private void setTextView(TextView textview, String content){
        if(content.equals("")){
            textview.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 0));
        } else {
            textview.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            textview.setText(content);
        }
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
