/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.searchbyranking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchByRankingFragment extends Fragment {

    RankByTagAdapter mInspireAdapter;
    TagsAdapter mTagsAdapter;
    RecyclerView mTagRecyclerView;
    RecyclerView mInspireRecyclerView;
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

        mInspireRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_rv);
        mInspireRecyclerView.setHasFixedSize(true);
        mInspireRecyclerView.setNestedScrollingEnabled(false);

        frequentTags = ItemCards.getInstance(getContext()).getFrequentTags();

        mInspireAdapter = new RankByTagAdapter(getContext(), frequentTags);
        mInspireAdapter.setHasStableIds(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setItemPrefetchEnabled(true);

        mInspireRecyclerView.setLayoutManager(mLayoutManager);

        mInspireAdapter.setOnItemClickListener(new RankByTagAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                //TODO:
                //TextView tv = (TextView) view;
                //searchView.setQuery(tv.getText().toString().toLowerCase(), true);
            }
        });

        ItemCards itemCards = ItemCards.getInstance(getContext());
        itemCards.setAdapter(mInspireAdapter);

        mInspireRecyclerView.setAdapter(mInspireAdapter);
        mInspireAdapter.notifyDataSetChanged();

        // Set Tag recycler view
        mTagRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_tags_rv);
        mTagRecyclerView.setHasFixedSize(true);
        mTagRecyclerView.setNestedScrollingEnabled(false);
        mTagsAdapter = new TagsAdapter(getContext(), frequentTags);
        mTagsAdapter.setHasStableIds(true);
        RecyclerView.LayoutManager tagsLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tagsLayoutManager.setItemPrefetchEnabled(true);

        mTagRecyclerView.setLayoutManager(tagsLayoutManager);

        mTagsAdapter.setOnItemClickListener(new TagsAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                String tag = mTagsAdapter.getTag(position);
                searchViewListener.onQueryTextChange(tag);
                //searchView.setFocus
                searchView.setQuery(tag, true);
                searchView.setIconified(false);
                searchView.clearFocus();
                mLayoutManager.scrollToPosition(0);

            }
        });

        mTagRecyclerView.setAdapter(mTagsAdapter);
        mTagsAdapter.notifyDataSetChanged();

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

    public void updateContents(){
        frequentTags = ItemCards.getInstance(getContext()).getFrequentTags();
        mInspireAdapter.updateImages(frequentTags);
        mTagsAdapter.updateImages(frequentTags);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void fillSearchResultWithFreqTags(){
        mInspireAdapter.updateImages(frequentTags);
        mInspireAdapter.notifyDataSetChanged();
    }

    protected void searchAndUpdateRecycler(String query){
        mInspireAdapter.updateImages(ItemCards.getInstance(getContext()).getInspired(query));
        mInspireAdapter.notifyDataSetChanged();
        mTagForSearch = query;
    }
}
