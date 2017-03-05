/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.inspire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import edu.ucsb.cs.cs185.foliostation.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;

public class InspireActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspire);

        List<ItemCards.TagAndImages> list = ItemCards.getInstance(getApplicationContext()).getInspired("a");
        int i = 1;
    }
}
