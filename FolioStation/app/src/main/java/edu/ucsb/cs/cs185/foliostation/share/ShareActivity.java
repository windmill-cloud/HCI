/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.share;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.models.Cards;
import edu.ucsb.cs.cs185.foliostation.models.InboxCards;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setViewContents();

        Button shareButton = (Button) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView subject = (TextView) findViewById(R.id.enter_subject);
                String title = subject.getText().toString();

                TextView message = (TextView) findViewById(R.id.enter_message);
                String description = message.getText().toString();
                prepareAndSendMessage(title, description);

                Toast toast = Toast.makeText(getApplicationContext(), "Successfully Shared",
                        Toast.LENGTH_SHORT);

                TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                toastMessage.setTextColor(Color.WHITE);
                toast.show();
                finish();
            }
        });
    }

    private void setViewContents(){
        Intent intent = getIntent();
        String from = intent.getStringExtra("FROM");
        if(from != null && from.equals("GRID")){
            int cardIndex = intent.getIntExtra("CARD_INDEX", -1);
            if(cardIndex < 0) {
                return;
            }
            ItemCards.Card card =
                    ItemCards.getInstance(getApplicationContext()).cards.get(cardIndex);
            TextView subject = (TextView) findViewById(R.id.enter_subject);
            subject.setText(card.getTitle());

            TextView message = (TextView) findViewById(R.id.enter_message);
            message.setText(card.getDescription());

        }
    }

    private void prepareAndSendMessage(String title, String description){
        Intent intent = getIntent();
        String from = intent.getStringExtra("FROM");
        InboxCards.CardImage profile =
                ItemCards.getInstance(getApplicationContext())
                        .makeNewCardImage("http://i.telegraph.co.uk/multimedia/archive/02227/jonathaniveparty_2227253b.jpg", ItemCards.URL);

        if(from != null && (from.equals("GRID") || from.equals("DETAILS"))) {
            int cardIndex = intent.getIntExtra("CARD_INDEX", -1);
            if(cardIndex < 0) {
                return;
            }
            ItemCards.Card card =
                    ItemCards.getInstance(getApplicationContext()).cards.get(cardIndex);
            List<ItemCards.CardImage> list = new ArrayList<>(card.getImages());
            List<InboxCards.CardImage> newList = getCopiedList(list);

            InboxCards.getInstance(getApplicationContext())
                    .addNewCardFromDetails(title, description, card.coverIndex, "Jonathan Ive",
                            profile, card.getTags(), newList);

        } else {
            String tag = intent.getStringExtra("TAG").toLowerCase();

            List<String> tags = new ArrayList<>();
            tags.add(tag);

            List<ItemCards.CardImage> list =
                    ItemCards.getInstance(getApplicationContext()).tagMap.get(tag);
            List<InboxCards.CardImage> newList = getCopiedList(list);

            InboxCards.getInstance(getApplicationContext())
                    .addNewCardFromDetails(title, description, 0, "Jonathan Ive",
                            profile, tags, newList);
        }
    }

    private List<InboxCards.CardImage> getCopiedList(List<Cards.CardImage> images){
        return ItemCards.getInstance(getApplicationContext()).getCopiedImages(images);
    }

}
