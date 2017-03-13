/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.searchbyranking;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.Cards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.search.SearchAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchByRankingFragment extends Fragment {

    RankByTagAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    String mTagForSearch = "";
    List<ItemCards.TagAndImages> frequentTags;

    public SearchByRankingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_by_ranking, container, false);

        final SearchView searchView = (SearchView) rootView.findViewById(R.id.search_by_ranking_search_view);
        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = (EditText) searchView.findViewById(id);
        searchEditText.setTextColor(Color.GRAY);
        searchEditText.setHintTextColor(Color.GRAY);
        searchView.setOnQueryTextListener(searchViewListener);
        searchView.setMaxWidth( Integer.MAX_VALUE );

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        frequentTags = ItemCards.getInstance(getContext()).getFrequentTags();

        mAdapter = new RankByTagAdapter(getContext(), frequentTags);
        mAdapter.setHasStableIds(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(new RankByTagAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                //TODO:
                TextView tv = (TextView) view;
                searchView.setQuery(tv.getText().toString().toLowerCase(), true);
            }
        });

        ItemCards itemCards = ItemCards.getInstance(getContext());
        itemCards.setAdapter(mAdapter);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return rootView;
    }

    private SearchView.OnQueryTextListener searchViewListener = new SearchView.OnQueryTextListener(){
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.i("query", query);
            searchAndUpdateRecycler(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                fillSearchResultWithFreqTags();
            }else {
                searchAndUpdateRecycler(newText);
                Log.i("change", newText);
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void fillSearchResultWithFreqTags(){
        mAdapter.updateImages(frequentTags);
        mAdapter.notifyDataSetChanged();
    }

    protected void searchAndUpdateRecycler(String query){
        mAdapter.updateImages(ItemCards.getInstance(getContext()).getInspired(query));
        mAdapter.notifyDataSetChanged();
        mTagForSearch = query;
    }
}
