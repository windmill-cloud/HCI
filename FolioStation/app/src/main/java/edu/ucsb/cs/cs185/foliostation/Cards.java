/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.lzy.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.ucsb.cs.cs185.foliostation.mycollections.CardViewHolder;

/**
 * Created by xuanwang on 3/4/17.
 */

public class Cards {
    public LinkedList<Card> cards = new LinkedList<>();
    private static Context mContext = null;
    private static RecyclerView.Adapter<CardViewHolder> mAdapter;

    final static int URL = 0;
    final static int PATH = 1;

    public void deleteIthCard(int i){
        if(cards.size() > 0){
            cards.remove(i);
        }
    }

    public void setAdapter(RecyclerView.Adapter<CardViewHolder> adapter) {
        mAdapter = adapter;
    }

    Cards() {
    }

    public Cards(Context context) {
        mContext = context;
    }

    public class CardImage {
        public String mUrl = "";
        public int mType = PATH;

        CardImage(String url, int type){
            mUrl = url;
            mType = type;
        }

        public boolean isFromPath(){
            return mType == PATH;
        }

        public boolean isFromUrl(){
            return mType == URL;
        }

    }


    public class Card{
        List<CardImage> mImages = new ArrayList<>();
        public List<Bitmap> mThumbnails = new ArrayList<>();

        public boolean isUserLiked() {
            return userLiked;
        }

        public void setUserLiked() {
            if(userLiked){
                userLiked = false;
                decreaseLikes();
            } else {
                userLiked = true;
                increaseLikes();
            }
        }

        boolean userLiked = false;

        public int getNumLikes() {
            return mNumLike;
        }

        public void setNumLikes(int mNumLike) {
            this.mNumLike = mNumLike;
        }


        public void decreaseLikes(){
            if(mNumLike > 0){
                mNumLike--;
            }
        }

        public void increaseLikes() {
            mNumLike++;
        }


        int mNumLike = 0;

        public List<String> getTags() {
            return tags;
        }

        public String getTagsString() {
            StringBuilder sb = new StringBuilder();
            for(String tag: tags){
                sb.append(tag).append(", ");
            }
            if(sb.length() >= 2 ){
                sb.setLength(sb.length()-2);
            }
            return sb.toString();
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        List<String> tags = new ArrayList<>();
        public int coverIndex = 0;
        String mTitle = "";
        String mDescription = "";

        public CardImage mUserProfile;

        public void setProfile(CardImage profile){
            mUserProfile = profile;
        }

        public Card(String url, int type,  String title, String description){
            mImages.add(new CardImage(url, type));
            mTitle = title;
            mDescription =  description;
        }

        public Card( String title, String description){
            mTitle = title;
            mDescription =  description;
        }

        public Card(){

        }

        public CardImage getCoverImage(){
            int i = 0;
            return mImages.get(coverIndex);
        }

        public void setCoverIndex(int index){
            int i = 0;
            coverIndex = index;

        }

        public boolean hasMaxNumOfImages(){
            return mImages.size() >= 24;
        }

        public void addImages(List<ImageItem> images){
            for(ImageItem imageItem: images) {
                mImages.add(new CardImage(imageItem.path, PATH));
            }
        }

        public boolean hasMultiPics(){
            return mImages.size() > 1;
        }

        public String getDescription() {
            return mDescription;
        }

        public void setDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        public List<CardImage> getImages() {
            return mImages;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }
    }


    public void addNewCardFromImages(List<ImageItem> imageItemList){

        Card newCard = new Card();

        for(ImageItem imageItem: imageItemList){
            newCard.mImages.add(new CardImage(imageItem.path, PATH));
        }
        cards.addFirst(newCard);
    }

}
