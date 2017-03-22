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
import edu.ucsb.cs.cs185.foliostation.models.InboxCards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;

/**
 * Created by xuanwang on 3/18/17.
 */

public class InboxCardsDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Folios.db";
    public final String TABLE_NAME = "inboxcards";

    private boolean hasTable = false;

    public static final int UUID = 0;
    public static final int TITLE = 1;
    public static final int DESCRIPTION = 2;
    public static final int COVERINDEX = 3;
    public static final int USERNAME = 4;
    public static final int USERPROFILE = 5;
    public static final int READ = 6;
    public static final int TAGS = 7;
    public static final int IMAGES = 8;

    public InboxCardsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

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
                "coverindex text, " +
                "username text, " +
                "userprofile text, " +
                "read integer, " +
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
        contentValues.put("coverindex", String.valueOf(card.getCoverIndex()));
        contentValues.put("username", card.getUsername());
        contentValues.put("userprofile", card.getProfileJSon());
        int read = card.isRead()? 1: 0;
        contentValues.put("read", read);
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
        contentValues.put("coverindex", String.valueOf(card.getCoverIndex()));
        contentValues.put("username", card.getUsername());
        contentValues.put("userprofile", card.getProfileJSon());
        int read = card.isRead()? 1: 0;
        contentValues.put("read", read);
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
            String coverindex = res.getString(COVERINDEX);
            String username = res.getString(USERNAME);
            String userprofile = res.getString(USERPROFILE);
            int read = res.getInt(READ);
            String tags = res.getString(TAGS);
            String images = res.getString(IMAGES);

            Gson gson = new Gson();

            List<String> tagsList =
                    gson.fromJson(tags, new TypeToken<List<String>>(){}.getType());

            List<Cards.CardImage> imageList =
                    gson.fromJson(images, new TypeToken<List<Cards.CardImage>>(){}.getType());

            Cards.CardImage profileImage =
                    gson.fromJson(userprofile, Cards.CardImage.class);

            boolean readbool = read > 0;

            InboxCards.getInstance(context).
                    addNewCardFromDetails(uuid, title, description, Integer.parseInt(coverindex),
                            username, profileImage, readbool, tagsList, imageList);

            res.moveToNext();
        }
        ItemCards.getInstance(context).rebuildTagsMap();
        res.close();
    }
}