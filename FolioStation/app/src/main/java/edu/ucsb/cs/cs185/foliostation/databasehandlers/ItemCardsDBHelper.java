/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.databasehandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.models.Cards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;

/**
 * Created by xuanwang on 2/9/17.
 */

public class ItemCardsDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Folios.db";
    public final String TABLE_NAME = "itemcards";
    private boolean hasTable = false;

    public static final int UUID = 0;
    public static final int TITLE = 1;
    public static final int DESCRIPTION = 2;
    public static final int COVERINDEX = 3;
    public static final int TAGS = 4;
    public static final int IMAGES = 5;

    public ItemCardsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //context.deleteDatabase(DB_NAME);
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Matches(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql = "create table if not exists " +
                TABLE_NAME +
                " (id text primary key, " +
                "title text, " +
                "description text, " +
                "coverindex integer, " +
                "tags text, " +
                "images text)";

        sqLiteDatabase.execSQL(sql);
        hasTable = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean insertCard (ItemCards.Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", card.getUUID());
        contentValues.put("title", card.getTitle());
        contentValues.put("description", card.getDescription());
        contentValues.put("coverindex", card.getCoverIndex());
        contentValues.put("tags", card.getTagsJson());
        contentValues.put("images", card.getImagesJson());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id, null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateCards (ItemCards.Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", card.getUUID());
        contentValues.put("title", card.getTitle());
        contentValues.put("description", card.getDescription());
        contentValues.put("coverindex", card.getCoverIndex());
        contentValues.put("tags", card.getTagsJson());
        contentValues.put("images", card.getImagesJson());
        db.update(TABLE_NAME, contentValues, "id='" + card.getUUID() + "'", null);
        return true;
    }

    public Integer deleteCard(ItemCards.Card card) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id='" + card.getUUID() + "'", null);
    }

    public void populateCards(Context context) {

        SQLiteDatabase db = this.getReadableDatabase();
        if(!hasTable){
            this.onCreate(db);
        }
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            String uuid = res.getString(UUID);
            String title = res.getString(TITLE);
            String description = res.getString(DESCRIPTION);
            int coverindex = res.getInt(COVERINDEX);
            String tags = res.getString(TAGS);
            String images = res.getString(IMAGES);

            Gson gson = new Gson();

            List<String> tagsList =
                    gson.fromJson(tags, new TypeToken<List<String>>(){}.getType());

            List<Cards.CardImage> imageList =
                    gson.fromJson(images, new TypeToken<List<Cards.CardImage>>(){}.getType());

            ItemCards.getInstance(context).
                    addNewCardFromDB(uuid, title, description, coverindex, tagsList, imageList);

            res.moveToNext();
        }
        ItemCards.getInstance(context).rebuildTagsMap();
        res.close();
    }
}