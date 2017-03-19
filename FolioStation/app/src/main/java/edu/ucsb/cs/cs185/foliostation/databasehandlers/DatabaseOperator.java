/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.databasehandlers;

import android.content.Context;

/**
 * Created by xuanwang on 2/9/17.
 */

public class DatabaseOperator {

    private static DatabaseOperator mInstance;
    private static ItemCardsDBHelper mItemCardDBOperator;
    private static InboxCardsDBHelper mInboxCardsDBOperator;

    private static Context mCtx;

    private DatabaseOperator(Context context) {
        mCtx = context;
        mItemCardDBOperator = getItemCardDBOperator();
    }

    public static synchronized DatabaseOperator getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseOperator(context);
        }
        return mInstance;
    }

    public ItemCardsDBHelper getItemCardDBOperator() {
        if (mItemCardDBOperator == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mItemCardDBOperator = new ItemCardsDBHelper(mCtx.getApplicationContext());
        }
        return mItemCardDBOperator;
    }

    public InboxCardsDBHelper getInboxCardsDBOperator() {
        if (mInboxCardsDBOperator == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mInboxCardsDBOperator = new InboxCardsDBHelper(mCtx.getApplicationContext());
        }
        return mInboxCardsDBOperator;
    }
}
