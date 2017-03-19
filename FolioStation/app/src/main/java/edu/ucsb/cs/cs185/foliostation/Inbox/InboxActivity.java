package edu.ucsb.cs.cs185.foliostation.Inbox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.InboxCards;
import edu.ucsb.cs.cs185.foliostation.collections.CardsFragment;
import edu.ucsb.cs.cs185.foliostation.collections.DetailBlurDialog;


public class InboxActivity extends AppCompatActivity {
    private final Activity mInboxActivity = this;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    InboxGridAdapter mGridCardAdapter;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("Inbox");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.cards_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        //TODO: replace with your own adapter
        mGridCardAdapter = new InboxGridAdapter(InboxCards.getInstance(this).cards, mInboxActivity);
        mGridCardAdapter.setHasStableIds(true);

        mGridCardAdapter.setOnItemClickListener(new InboxGridAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){

            }
        });

        mRecyclerView.setAdapter(mGridCardAdapter);
    }

    protected void startDetailDialog(int position){
        Bundle arguments = new Bundle();
        arguments.putInt("CARD_INDEX", position);
        arguments.putString("FROM", "GRID");

        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = getSupportFragmentManager();

        fragment.show(ft, "dialog");

        Bitmap map = takeScreenShot(mInboxActivity);
        Bitmap fast = CardsFragment.BlurBuilder.blur(mInboxActivity, map);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        ImageView background = (ImageView) findViewById(R.id.activity_background);
        background.bringToFront();
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setImageDrawable(draw);
    }

    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }
}
