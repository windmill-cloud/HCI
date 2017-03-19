/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.models;

import android.content.Context;

import com.lzy.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.ucsb.cs.cs185.foliostation.databasehandlers.DatabaseOperator;

/**
 * Created by xuanwang on 2/19/17.
 */

public class ItemCards extends Cards{
    public static ItemCards mInstance;

    ItemCards(Context context) {
        super(context);
    }

    public static synchronized ItemCards getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ItemCards(context);
            mInstance.inflateFromDB();
        }
        return mInstance;
    }

    private void inflateFromDB(){
        DatabaseOperator.getInstance(mContext).
                getItemCardDBOperator().populateCards(mContext);
    }

    public void inflateDummyContent(){
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/413e3mR4cSL.jpg", URL,
                "騎士団長殺し 第1部 顕れるイデア編", "村上 春樹"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/51raGEQSo2L.jpg", URL,
                "色彩を持たない多崎つくると、彼の巡礼の年", "村上 春樹"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/41jAK3VHZ2L.jpg", URL,
                "海辺のカフカ", "村上 春樹"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/51pdnZBq-aL.jpg", URL,
                "1Q84 BOOK1〈4月‐6月〉", "村上 春樹"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/41oYBNer4pL.jpg", URL,
                "職業としての小説家", "村上 春樹"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/41PGlYT6DgL._SX332_BO1,204,203,200_.jpg", URL,
                "ねじまき鳥クロニクル", "村上 春樹"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/51FYrDp2WEL._SX346_BO1,204,203,200_.jpg", URL,
                "恋しくて - TEN SELECTED LOVE STORIES", "村上 春樹  (編集)"));
        cards.add(new Card("https://images-na.ssl-images-amazon.com/images/I/51cNUdZY69L._SX341_BO1,204,203,200_.jpg", URL,
                "女のいない男たち", "村上 春樹"));
    }

    public class TagAndImages{
        public String tag = "";
        public int level = 0;
        public List<CardImage> cardImages =  new ArrayList<>();

        public TagAndImages(String tag, int level){
            this.tag = tag;
            this.level = level;
        }

        public TagAndImages(String tag, List<CardImage> cardImages){
            this.tag = tag;
            this.cardImages = cardImages;
        }

        public void addAll(List<CardImage> all){
            cardImages.addAll(all);
        }
    }

    public List<TagAndImages> getFrequentTags(){
        List<TagAndImages> res = new ArrayList<>();
        for(String tag: tagMap.keySet()){
            res.add(new TagAndImages(tag, tagMap.get(tag)));
        }

        // number of images in reverse order
        Collections.sort(res, new Comparator<TagAndImages>() {
            @Override
            public int compare(TagAndImages t1, TagAndImages t2) {
                return t2.cardImages.size() - t1.cardImages.size();
            }
        });

        return res;
    }

    public void addNewCardFromTagAndImages(TagAndImages tagAndImages){
        Card newCard = new Card();

        newCard.mImages.addAll(tagAndImages.cardImages);
        newCard.tags.add(tagAndImages.tag);
        cards.addFirst(newCard);
        DatabaseOperator.getInstance(mContext).getItemCardDBOperator().insertCard(newCard);
    }


    public List<TagAndImages> getInspired(String rawTag){

        List<String> matchedTags = new ArrayList<>();

        for(Map.Entry<String, List<CardImage>> entry: tagMap.entrySet()){
            String key = entry.getKey();
            if(key.startsWith(rawTag) || key.equals(rawTag)) {
                matchedTags.add(key);
            }
        }

        List<TagAndImages> res = new ArrayList<>();

        for(String tag: matchedTags) {
            Set<String> visitedTags = new HashSet<>();
            visitedTags.add(tag);
            Queue<String> tagQueue = new LinkedList<>();
            tagQueue.offer(tag);

            int level = 0;

            while (!tagQueue.isEmpty()) {
                int n = tagQueue.size();
                for (int i = 0; i < n; i++) {
                    String t = tagQueue.poll();

                    TagAndImages tai = new TagAndImages(t, level);
                    if (tagMap.containsKey(t)) {
                        tai.addAll(tagMap.get(t));
                        res.add(tai);
                    }

                    for (int j = 0; j < cards.size(); j++) {
                        Card card = cards.get(j);
                        if (card.hasTag(t)) {
                            for (String tt : card.getTags()) {
                                if (!visitedTags.contains(tt)) {
                                    visitedTags.add(tt);
                                    tagQueue.offer(tt);
                                }
                            }
                        }
                    }
                }
                level++;
            }
        }

        Collections.sort(res, new Comparator<TagAndImages>() {
            @Override
            public int compare(TagAndImages t0, TagAndImages t1) {
                return t0.level - t1.level;
            }
        });

        return res;
    }
}
