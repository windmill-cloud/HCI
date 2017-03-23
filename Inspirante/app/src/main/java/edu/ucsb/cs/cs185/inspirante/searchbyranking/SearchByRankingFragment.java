/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.inspirante.searchbyranking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import edu.ucsb.cs.cs185.inspirante.R;
import edu.ucsb.cs.cs185.inspirante.models.Cards;
import edu.ucsb.cs.cs185.inspirante.models.ItemCards;
import edu.ucsb.cs.cs185.inspirante.collections.CardsFragment;
import edu.ucsb.cs.cs185.inspirante.collections.DetailBlurDialog;
import edu.ucsb.cs.cs185.inspirante.tagandimages.TagAndImagesActivity;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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
        searchView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        });

        mInspireRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_rv);
        mInspireRecyclerView.setHasFixedSize(true);
        mInspireRecyclerView.setNestedScrollingEnabled(false);

        frequentTags = ItemCards.getInstance(getContext()).getFrequentTags();

        mInspireAdapter = new RankByTagAdapter(getContext(), this, frequentTags);
        mInspireAdapter.setHasStableIds(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setItemPrefetchEnabled(true);

        mInspireRecyclerView.setLayoutManager(mLayoutManager);

        mInspireAdapter.setOnItemClickListener(new RankByTagAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                TextView tv = (TextView) view;
                Intent intent = new Intent(getActivity(), TagAndImagesActivity.class);
                intent.putExtra("TAG", tv.getText());
                startActivity(intent);
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

    protected void startDetailDialog(Cards.CardImage cardImage){

        Bundle arguments = new Bundle();
        arguments.putString("FROM", "SINGLE_IMAGE");
        arguments.putString("URL", cardImage.mUrl);
        arguments.putInt("TYPE", cardImage.mType);
        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = getActivity().getSupportFragmentManager();

        fragment.show(ft, "dialog");
        //TODO: move takeScreenShot method to somewhere else from CardsFragment

        Bitmap map = CardsFragment.takeScreenShot(getActivity());
        Bitmap fast = CardsFragment.BlurBuilder.blur(getContext(), map);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        ImageView background = (ImageView) getActivity().findViewById(R.id.activity_background);
        background.bringToFront();
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setImageDrawable(draw);
    }
}
