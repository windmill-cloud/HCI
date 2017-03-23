package edu.ucsb.cs.cs185.inspirante.models;

import android.content.Context;

import java.util.List;

import edu.ucsb.cs.cs185.inspirante.databasehandlers.DatabaseOperator;
import edu.ucsb.cs.cs185.inspirante.databasehandlers.InboxCardsDBHelper;

/**
 * Created by xuanwang on 3/18/17.
 */

public class InboxCards extends Cards{
    public static InboxCards mInstance;

    InboxCards(Context context) {
        super(context);
    }

    public static synchronized InboxCards getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new InboxCards(context);
            mInstance.inflateFromDB();
        }
        return mInstance;
    }

    private void inflateFromDB(){
        InboxCardsDBHelper dbHelper = DatabaseOperator.getInstance(mContext).
                getInboxCardsDBOperator();
        dbHelper.populateCards(mContext);
    }

    public void addNewCardFromDetails(String UUID, String title, String descriptions,
                                      int coverIndex, String username, CardImage profileImage,
                                      boolean read, List<String> tags, List<CardImage> images){
        Card newCard = new Card(UUID, title, descriptions, coverIndex,
                username, profileImage, tags, images);
        if(read) {
            newCard.setRead();
        }
        flattenedImages.addAll(newCard.getImages());
        cards.add(newCard);
    }

    public void addNewCardFromDetails(String title, String descriptions, int coverIndex,
                                      String username, CardImage profileImage,
                                      List<String> tags, List<CardImage> images){
        Card newCard = new Card(title, descriptions, coverIndex,
                username, profileImage, tags, images);
        flattenedImages.addAll(newCard.getImages());
        cards.addFirst(newCard);
        DatabaseOperator.getInstance(mContext).getInboxCardsDBOperator().insertCard(newCard);
    }

    public boolean hasUnreadMessage(){
        for(Card card: cards){
            if(!card.isRead()){
                return true;
            }
        }
        return false;
    }

    public boolean hasNoMessage(){
        return cards.size() == 0;
    }
}
