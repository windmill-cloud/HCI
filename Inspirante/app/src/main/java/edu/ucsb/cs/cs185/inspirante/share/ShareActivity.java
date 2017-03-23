/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.inspirante.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ucsb.cs.cs185.inspirante.R;
import edu.ucsb.cs.cs185.inspirante.collections.CardsFragment;
import edu.ucsb.cs.cs185.inspirante.collections.DetailBlurDialog;
import edu.ucsb.cs.cs185.inspirante.models.Cards;
import edu.ucsb.cs.cs185.inspirante.models.InboxCards;
import edu.ucsb.cs.cs185.inspirante.models.ItemCards;
import edu.ucsb.cs.cs185.inspirante.tagandimages.TagAndImagesAdapter;

public class ShareActivity extends AppCompatActivity {

    private List<ItemCards.CardImage> mCardImages;
    private int coverIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setViewContents();

        // Setting the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_share);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setOnMenuItemClickListener(onMenuItemClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_send:
                    TextView subject = (TextView) findViewById(R.id.enter_subject);
                    String title = subject.getText().toString();

                    TextView message = (TextView) findViewById(R.id.enter_message);
                    String description = message.getText().toString();

                    TextView tagsView = (TextView) findViewById(R.id.enter_tags);
                    String tags = tagsView.getText().toString();

                    prepareAndSendMessage(title, description, tags);

                    Toast toast = Toast.makeText(getApplicationContext(), "Successfully Shared",
                            Toast.LENGTH_SHORT);

                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(Color.WHITE);
                    toast.show();
                    finish();
                    break;
            }
            return true;
        }
    };

    private void setViewContents(){
        Intent intent = getIntent();
        String from = intent.getStringExtra("FROM");
        if(from != null && (from.equals("GRID") || from.equals("DETAILS"))) {
            int cardIndex = intent.getIntExtra("CARD_INDEX", -1);
            if(cardIndex < 0) {
                return;
            }
            ItemCards.Card card =
                    ItemCards.getInstance(getApplicationContext()).cards.get(cardIndex);

            mCardImages = card.getImages();
            coverIndex = card.coverIndex;

            TextView subject = (TextView) findViewById(R.id.enter_subject);
            subject.setText(card.getTitle());

            TextView message = (TextView) findViewById(R.id.enter_message);
            message.setText(card.getDescription());

            TextView tags = (TextView) findViewById(R.id.enter_tags);
            tags.setText(card.getTagsString().toUpperCase());

        } else {
            String tag = intent.getStringExtra("TAG").toLowerCase();
            mCardImages = ItemCards.getInstance(getApplicationContext()).tagMap.get(tag);
            TextView tags = (TextView) findViewById(R.id.enter_tags);
            tags.setText(tag.toUpperCase());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        gridLayoutManager.setItemPrefetchEnabled(true);

        recyclerView.setLayoutManager(gridLayoutManager);

        TagAndImagesAdapter tagsAndImagesAdapter =
                new TagAndImagesAdapter(getApplicationContext(), mCardImages);
        recyclerView.setAdapter(tagsAndImagesAdapter);
        tagsAndImagesAdapter.notifyDataSetChanged();
        tagsAndImagesAdapter.setOnItemClickListener(
                new TagAndImagesAdapter.OnRecyclerViewItemClickListener(){
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.i("image", "clicked");
                        startDetailDialog(position);
                    }
                });

    }

    private void prepareAndSendMessage(String title, String description, String tagsString){
        InboxCards.CardImage profile =
                ItemCards.getInstance(getApplicationContext())
                        .makeNewCardImage("http://i.telegraph.co.uk/multimedia/archive/02227/jonathaniveparty_2227253b.jpg", ItemCards.URL);

        String[] tags = tagsString.replaceAll("^[,\\s]+", "").replaceAll("[,\\s]*$", "").split("[\\s]*,[\\s]*");
        List<String> tagsList = new ArrayList<>();
        for(String str: tags){
            if(!str.equals("")){
                tagsList.add(str.toLowerCase());
            }
        }

        List<InboxCards.CardImage> newList = getCopiedList(mCardImages);

        InboxCards.getInstance(getApplicationContext())
                    .addNewCardFromDetails(title, description, coverIndex, "Jonathan Ive",
                            profile, tagsList, newList);
    }

    private List<InboxCards.CardImage> getCopiedList(List<Cards.CardImage> images){
        return ItemCards.getInstance(getApplicationContext()).getCopiedImages(images);
    }

    protected void startDetailDialog(int position){

        Bundle arguments = new Bundle();
        arguments.putString("FROM", "SINGLE_IMAGE");
        arguments.putString("URL", mCardImages.get(position).mUrl);
        arguments.putInt("TYPE", mCardImages.get(position).mType);
        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = getSupportFragmentManager();

        fragment.show(ft, "dialog");
        //TODO: move takeScreenShot method to somewhere else from CardsFragment

        Bitmap map = CardsFragment.takeScreenShot(this);
        Bitmap fast = CardsFragment.BlurBuilder.blur(getApplicationContext(), map);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        ImageView background = (ImageView) findViewById(R.id.activity_background);
        background.bringToFront();
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setImageDrawable(draw);

    }
}
