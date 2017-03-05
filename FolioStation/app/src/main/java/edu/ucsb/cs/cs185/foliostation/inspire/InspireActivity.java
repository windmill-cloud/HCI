/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.inspire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.DiscoverCards;
import edu.ucsb.cs.cs185.foliostation.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.discover.DiscoverAdapter;

public class InspireActivity extends AppCompatActivity {

    InspireAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspire);

        Intent intent = getIntent();
        String query = intent.getStringExtra("QUERY");

        List<ItemCards.TagAndImages> list =
                ItemCards.getInstance(getApplicationContext()).getInspired(query);


        mRecyclerView = (RecyclerView) findViewById(R.id.inspire_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new InspireAdapter(getApplicationContext(), list);
        mAdapter.setHasStableIds(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mAdapter.setOnItemClickListener(new InspireAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){

            }
        });


        /*
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);*/
        ItemCards itemCards = ItemCards.getInstance(getApplicationContext());
        itemCards.setAdapter(mAdapter);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
