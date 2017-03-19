package edu.ucsb.cs.cs185.foliostation.tagandimages;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.Cards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardsFragment;
import edu.ucsb.cs.cs185.foliostation.mycollections.DetailBlurDialog;
import edu.ucsb.cs.cs185.foliostation.share.ShareActivity;

public class TagAndImagesActivity extends AppCompatActivity {

    private List<ItemCards.CardImage> mCardImages;
    private String mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_and_images);

        // Getting Tag from intent
        Intent intent = getIntent();
        mTag = intent.getStringExtra("TAG");

        // Set the TextView in toolbar with the tag
        TextView tagTextView = (TextView) findViewById(R.id.tag_and_images_tag);
        tagTextView.setText(mTag);

        // Setting the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tag_and_images_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        // Setting the recyclerview
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tag_and_images_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        gridLayoutManager.setItemPrefetchEnabled(true);

        recyclerView.setLayoutManager(gridLayoutManager);

        mCardImages = ItemCards.getInstance(getApplicationContext()).tagMap.get(mTag.toLowerCase());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tag_images, menu);
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
                case R.id.action_share:
                    Log.i("clicked", "share");
                    Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                    intent.putExtra("TAG", mTag.toLowerCase());
                    intent.putExtra("FROM", "SEARCH");
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };

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
