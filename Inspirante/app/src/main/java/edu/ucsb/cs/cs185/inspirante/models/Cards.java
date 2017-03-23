/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.inspirante.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.lzy.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.ucsb.cs.cs185.inspirante.databasehandlers.DatabaseOperator;
import edu.ucsb.cs.cs185.inspirante.collections.CardViewHolder;

/**
 * Created by xuanwang on 3/4/17.
 */

public class Cards {
    public LinkedList<Card> cards = new LinkedList<>();
    public List<CardImage> flattenedImages = new ArrayList<>();
    public Map<String, List<CardImage>> tagMap = new HashMap<>();
    protected static Context mContext = null;
    private static RecyclerView.Adapter<CardViewHolder> mAdapter;

    public final static int URL = 0;
    public final static int PATH = 1;

    public void deleteIthCard(int i){
        if(cards != null && cards.size() > 0){
            Card card = cards.get(i);
            DatabaseOperator.getInstance(mContext).getItemCardDBOperator().deleteCard(card);
            cards.remove(i);
            rebuildTagsMap();
        }
    }

    public void rebuildTagsMap(){
        tagMap.clear();
        for(Card card: cards){
            for(String tag: card.tags){
                if(!tagMap.containsKey(tag)){
                    tagMap.put(tag, new ArrayList<CardImage>());
                }
                tagMap.get(tag).addAll(card.getImages());
            }
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
        public int mType = URL;

        CardImage(String url, int type){
            mUrl = url;
            mType = type;
        }

        CardImage(){

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

        public void deleteCoverImage(){
            int newCoverIndex = coverIndex;
            if(coverIndex < mImages.size()){
                if(coverIndex == mImages.size() - 1){
                    newCoverIndex = mImages.size() - 2;
                }
                mImages.remove(coverIndex);
            }
            coverIndex = newCoverIndex;
            writeToDB();
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

        public String getTagsJson(){
            String json = new Gson().toJson(tags);
            return json;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
            for(String tag: tags){
                List<CardImage> list = null;
                if(!tagMap.containsKey(tag)){
                    list = new ArrayList<>();
                } else {
                    list = tagMap.get(tag);
                }
                list.addAll(mImages);
                tagMap.put(tag, list);
            }
        }

        public boolean hasTag(String t){
            for(String tag: tags){
                if(tag.startsWith(t) || tag.equals(t)){
                    return true;
                }
            }
            return false;
        }

        List<String> tags = new ArrayList<>();
        public int coverIndex = 0;

        public String getUUID() {
            return mUUID;
        }

        String mUUID = "";
        String mTitle = "";
        String mDescription = "";

        public String mUsername = "";
        public CardImage mUserProfile = new CardImage();

        boolean mMarkedRead = false;

        public boolean isRead(){
            return mMarkedRead;
        }

        public void setRead(){
            mMarkedRead = true;
        }

        public void setUsername(String username){
            mUsername = username;
        }

        public String getUsername(){
            return mUsername;
        }

        public void setProfile(CardImage profile){
            mUserProfile = profile;
        }

        public CardImage getProfile(){
            return mUserProfile;
        }

        public String getProfileJSon(){
            String json = new Gson().toJson(mUserProfile);
            return json;
        }

        public Card(String UUID, String title, String descriptions, int coverIndex, List<String> tags,
                    List<CardImage> images) {
            mUUID = UUID;
            mTitle = title;
            mDescription = descriptions;
            this.coverIndex = coverIndex;
            this.tags = tags;
            this.mImages = images;
            this.setTags(tags);
        }

        public Card(String title, String descriptions, int coverIndex, String username,
                    CardImage profileImage, List<String> tags, List<CardImage> images) {
            mUUID = UUID.randomUUID().toString();
            mTitle = title;
            mDescription = descriptions;
            mUsername = username;
            mUserProfile = profileImage;
            this.coverIndex = coverIndex;
            this.tags = tags;
            this.mImages = images;
            this.setTags(tags);
        }

        public Card(String UUID, String title, String descriptions, int coverIndex, String username,
                    CardImage profileImage, List<String> tags, List<CardImage> images) {
            mUUID = UUID;
            mTitle = title;
            mDescription = descriptions;
            mUsername = username;
            mUserProfile = profileImage;
            this.coverIndex = coverIndex;
            this.tags = tags;
            this.mImages = images;
            this.setTags(tags);
        }

        public Card(String url, int type,  String title, String description){
            this(title, description);

            addImage(new CardImage(url, type));
            mUUID = UUID.randomUUID().toString();
            mTitle = title;
            mDescription =  description;
        }

        public Card( String title, String description){
            mUUID = UUID.randomUUID().toString();
            mTitle = title;
            mDescription =  description;
        }

        public Card(){
            mUUID = UUID.randomUUID().toString();
        }

        public CardImage getCoverImage(){
            int i = 0;
            return mImages.get(coverIndex);
        }

        public void setCoverIndex(int index){
            int i = 0;
            coverIndex = index;
        }

        public int getCoverIndex(){
            return coverIndex;
        }

        public boolean hasMaxNumOfImages(){
            return mImages.size() >= 24;
        }

        public void addImage(CardImage cardImage){
            mImages.add(cardImage);
            for(String tag: this.tags){
                if(!tagMap.containsKey(tag)){
                    tagMap.put(tag, new ArrayList<CardImage>());
                }
                tagMap.get(tag).add(cardImage);
            }
            flattenedImages.add(cardImage);
        }

        public void addImage(ImageItem imageItem){
            CardImage cardImage = new CardImage(imageItem.path, PATH);
            mImages.add(cardImage);
            for(String tag: this.tags){
                if(!tagMap.containsKey(tag)){
                    tagMap.put(tag, new ArrayList<CardImage>());
                }
                tagMap.get(tag).add(cardImage);
            }
            flattenedImages.add(cardImage);
        }

        public void addImages(List<ImageItem> images){
            for(ImageItem imageItem: images) {
                addImage(imageItem);
            }
        }

        public String getImagesJson(){
            String json =  new Gson().toJson(mImages);
            return json;
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

        public void setImages(List<CardImage> images) {
            mImages = images;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public void writeToDB(){
            DatabaseOperator.getInstance(mContext)
                    .getItemCardDBOperator().updateCards(this);
        }


    }

    public List<CardImage> getCopiedImages(List<CardImage> images){
        List<CardImage> res = new ArrayList<>();

        for(CardImage cardImage: images){
            CardImage newCardImage = new CardImage(cardImage.mUrl, cardImage.mType);
            res.add(newCardImage);
        }
        return res;
    }

    public void addNewCardFromImages(List<ImageItem> imageItemList){
        Card newCard = new Card();

        for(ImageItem imageItem: imageItemList){
            newCard.addImage(new CardImage(imageItem.path, PATH));
        }
        cards.addFirst(newCard);
        DatabaseOperator.getInstance(mContext).getItemCardDBOperator().insertCard(newCard);
    }

    public void addNewCardFromDB(String UUID, String title, String descriptions, int coverIndex, List<String> tags,
                                 List<CardImage> images){
        Card newCard = new Card(UUID, title, descriptions, coverIndex, tags, images);
        flattenedImages.addAll(newCard.getImages());
        cards.addFirst(newCard);
    }

    public List<CardImage> searchByTag(String query){
        List<CardImage> res = new ArrayList<>();
        for(Map.Entry<String, List<CardImage>> entry: tagMap.entrySet()){
            String key = entry.getKey();
            if(key.startsWith(query) || key.equals(query)) {
                res.addAll(entry.getValue());
            }
        }
        return res;
    }

    public boolean hasTagsBeginWithQuery(String query){
        for(Map.Entry<String, List<CardImage>> entry: tagMap.entrySet()){
            String key = entry.getKey();
            if(key.startsWith(query) || key.equals(query)) {
                return true;
            }
        }
        return false;
    }

    public CardImage makeNewCardImage(String url, int type){
        return new CardImage(url, type);
    }

    public List<CardImage> getFlattenedImages(){
        return flattenedImages;
    }
}
