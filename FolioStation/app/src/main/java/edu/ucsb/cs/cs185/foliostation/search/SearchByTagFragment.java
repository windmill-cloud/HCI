/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import edu.ucsb.cs.cs185.foliostation.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchByTagFragment extends Fragment {
    SearchAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;


    public SearchByTagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_by_tag, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new SearchAdapter(getContext(), ItemCards.getInstance(getContext()).getFlattenedImages());
        mAdapter.setHasStableIds(true);

        mLayoutManager = new GridLayoutManager(getContext(), 4);
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setOnItemClickListener(new SearchAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){

            }
        });


        /*
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);*/
        ItemCards itemCards = ItemCards.getInstance(getContext());
        itemCards.setAdapter(mAdapter);

        if(itemCards.cards.size() == 0){
            itemCards.inflateDummyContent();
        }

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        final SearchView searchView = (SearchView) rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("query", query);
                searchAndUpdateRecycler(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("chage", newText);
                searchAndUpdateRecycler(newText);
                return false;
            }
        });

        return rootView;
    }

    protected void searchAndUpdateRecycler(String query){
        mAdapter.updateImages(ItemCards.getInstance(getContext()).searchByTag(query));
        mAdapter.notifyDataSetChanged();
    }

}
