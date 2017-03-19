/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.collectiondetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.editentry.EditTabsActivity;
import edu.ucsb.cs.cs185.foliostation.collections.CardsFragment;
import edu.ucsb.cs.cs185.foliostation.collections.DetailBlurDialog;
import edu.ucsb.cs.cs185.foliostation.share.ShareActivity;

public class CollectionDetailsActivity extends AppCompatActivity {

    private int mCardIndex;
    private RecyclerView mRecyclerView;
    private CollectionDetailsAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private boolean canAddImage = true;
    private Toolbar mToolbar;

    private static int IMAGE_PICKER = 1234;
    private static int EDIT_RESULT = 2345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);

        Intent intent= getIntent();
        mCardIndex = intent.getIntExtra("CARD_INDEX", 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        final ItemCards.Card card = ItemCards.getInstance(getApplicationContext()).cards.get(mCardIndex);

        mAdapter = new CollectionDetailsAdapter(getApplicationContext(), card.getImages());
        mAdapter.setHasStableIds(true);

        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(new CollectionDetailsAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                startDetailDialog(position);
            }
        });

        ItemCards itemCards = ItemCards.getInstance(getApplicationContext());
        itemCards.setAdapter(mAdapter);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mToolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if(card.getImages().size() >= 24) {
            canAddImage = false;
        }

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.details_add_image:
                        ImagePicker.getInstance().setSelectLimit(24 - card.getImages().size());
                        Intent imagePickerIntent = new Intent(CollectionDetailsActivity.this, ImageGridActivity.class);
                        startActivityForResult(imagePickerIntent, IMAGE_PICKER);
                        break;
                    case R.id.details_edit:
                        Intent editIntent = new Intent(CollectionDetailsActivity.this, EditTabsActivity.class);
                        editIntent.putExtra("CARD_INDEX", mCardIndex);
                        editIntent.putExtra("EDIT", true);
                        editIntent.putExtra("FROM", "DETAILS");
                        startActivityForResult(editIntent, EDIT_RESULT);
                        Log.i("selected", "edit");
                        break;
                    case R.id.details_share_collection:
                        Intent shareIntent = new Intent(CollectionDetailsActivity.this, ShareActivity.class);
                        shareIntent.putExtra("CARD_INDEX", mCardIndex);
                        shareIntent.putExtra("FROM", "DETAILS");
                        startActivity(shareIntent);
                        Log.i("selected", "share");
                        break;
                    case R.id.details_delete_collection:
                        Log.i("selected", "delete");
                        new AlertDialog.Builder(CollectionDetailsActivity.this)
                                .setTitle("Delete collection")
                                .setMessage("Are you sure you want to delete this collection?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        ItemCards.getInstance(getApplicationContext()).deleteIthCard(mCardIndex);
                                        finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                        ItemCards.getInstance(getApplicationContext()).deleteIthCard(mCardIndex);
                        break;
                }
                return true;
            }
        });

        inflateInfoBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inflateInfoBar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        if(canAddImage) {
            getMenuInflater().inflate(R.menu.menu_collection_details, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_collection_details_noadd, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected void inflateInfoBar(){
        ItemCards.Card card = ItemCards.getInstance(getApplicationContext()).cards.get(mCardIndex);
        ImageView coverImage = (ImageView) findViewById(R.id.details_cover_image);
        TextView titleText = (TextView) findViewById(R.id.details_title);
        TextView tagsText = (TextView) findViewById(R.id.details_tags);
        TextView descriptionText = (TextView) findViewById(R.id.details_descriptions);

        if(card != null) {
            // TODO: refactor picture loading
            if (card.getCoverImage().isFromPath()) {
                Picasso.with(getApplicationContext())
                        .load(new File(card.getCoverImage().mUrl))
                        .resize(600, 600)
                        .centerCrop()
                        .noFade()
                        .into(coverImage);
            } else {
                Picasso.with(getApplicationContext())
                        .load(card.getCoverImage().mUrl)
                        .resize(600, 600)
                        .centerCrop()
                        .noFade()
                        .into(coverImage);
            }
            titleText.setText(card.getTitle());
            tagsText.setText(card.getTagsString());
            descriptionText.setText(card.getDescription());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {

                ArrayList<ImageItem> images = (ArrayList<ImageItem>)
                        data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ItemCards.Card card = ItemCards.getInstance(getApplicationContext()).cards.get(mCardIndex);
                card.addImages(images);
                card.writeToDB();
                mAdapter.notifyDataSetChanged();
                this.onPrepareOptionsMenu(mToolbar.getMenu());
            } else {
                Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == EDIT_RESULT){
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }


    protected void startDetailDialog(int position){
        Bundle arguments = new Bundle();
        arguments.putInt("CARD_INDEX", mCardIndex);
        arguments.putInt("IMAGE_INDEX", position);

        arguments.putString("FROM", "DETAILS");
        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = this.getSupportFragmentManager();

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
