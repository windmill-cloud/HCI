/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;

import edu.ucsb.cs.cs185.foliostation.Inbox.InboxActivity;
import edu.ucsb.cs.cs185.foliostation.SplashScreenActivity;
import edu.ucsb.cs.cs185.foliostation.editentry.EditTabsActivity;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.collectiondetails.CollectionDetailsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardsFragment extends Fragment {

    GridCardAdapter mGridCardAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Toolbar toolbar;

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 7654;
    private static final int IMAGE_PICKER = 1234;

    public CardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_cards, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.card_fragment_toolbar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cards_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        toolbar.getBackground().setAlpha(0);

        GridCardAdapter.setContext(getContext());
        mGridCardAdapter = new GridCardAdapter(ItemCards.getInstance(getContext()).cards);
        mGridCardAdapter.setHasStableIds(true);

        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mGridCardAdapter.setOnItemClickListener(new GridCardAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                ItemCards.Card card = ItemCards.getInstance(getContext()).cards.get(position);

                if (card.hasMultiPics()){
                    Intent intent = new Intent(getActivity(), CollectionDetailsActivity.class);
                    intent.putExtra("CARD_INDEX", position);
                    startActivity(intent);
                } else {
                    startDetailDialog(position);
                }
            }
        });

        /*
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);*/
        ItemCards itemCards = ItemCards.getInstance(getContext());
        itemCards.setAdapter(mGridCardAdapter);

        mRecyclerView.setAdapter(mGridCardAdapter);
        mGridCardAdapter.notifyDataSetChanged();


        return rootView;
    }

    protected void startDetailDialog(int position){
        Bundle arguments = new Bundle();
        arguments.putInt("CARD_INDEX", position);
        arguments.putString("FROM", "GRID");

        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = getActivity().getSupportFragmentManager();

        fragment.show(ft, "dialog");

        Bitmap map = takeScreenShot(getActivity());
        Bitmap fast = BlurBuilder.blur(getContext(), map);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        ImageView background = (ImageView) getActivity().findViewById(R.id.activity_background);
        background.bringToFront();
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setImageDrawable(draw);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGridCardAdapter != null){
            mGridCardAdapter.notifyDataSetChanged();
        }
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

    public static class BlurBuilder {
        private static final float BITMAP_SCALE = 0.3f;
        private static final float BLUR_RADIUS = 24.0f;

        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_adding:
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            android.Manifest.permission.CAMERA},
                                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {

                        ImagePicker imagePicker = ImagePicker.getInstance();
                        imagePicker.setShowCamera(true);
                        startImagesPicking();
                    }
                    msg += "Click edit";
                    break;
                case R.id.action_shared:
                    // TODO: implement get shared content functionality
                    msg += "Click share";
                    startInboxActivity();
                    break;
                case R.id.action_settings:
                    msg += "Click setting";
                    break;
                case R.id.action_logout:
                    startSplashScreen();
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    protected void startInboxActivity(){
        Intent intent = new Intent(getActivity(), InboxActivity.class);
        startActivity(intent);
    }

    protected void startSplashScreen(){
        Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    protected void startImagesPicking() {
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ItemCards itemCards = ItemCards.getInstance(getContext());
                itemCards.addNewCardFromImages(images);
                Intent intent = new Intent(getActivity(), EditTabsActivity.class);
                intent.putExtra("CARD_INDEX", 0);

                startActivity(intent);

            } else {
                Toast.makeText(getActivity(), "No picture selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ImagePicker imagePicker = ImagePicker.getInstance();
                    imagePicker.setShowCamera(true);
                    startImagesPicking();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
