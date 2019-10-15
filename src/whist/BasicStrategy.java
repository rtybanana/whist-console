////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            BasicStrategy.java
// Date:            25/01/19
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package whist;

import cards.Card;
import cards.Hand;
import cards.Card.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The basic strategy that a player of whist should employ to select their card.
 * @author gxk17tru
 */
public class BasicStrategy implements Strategy {
    private final int playerID;
    private Suit trumps;
    
    public BasicStrategy(int p){
        this.playerID = p;       
    }                                               
    
    /**
     * Defines how this strategy selects a card to play based on the rules of
     * whist and the previous cards played in the trick
     * 
     * @param h this players hand
     * @param t the current trick
     * @return the card this strategy has chosen to play
     */
    @Override
    public Card chooseCard(Hand h, Trick t) {
        this.trumps = Trick.trumps;
        ArrayList<Card> hand = new ArrayList<>(h.getHand());
        Collections.sort(hand);
        
        if (t.getPlayerNo() != 0){               //if not first player
            /* 
             * bestCard - the currently winning card in the trick
             * tflag    - flag to indicate that the trick has been trumped
             * wflag    - flag to indicate that this team is currently winning
             */
            Card bestCard = t.getBestCard();
            boolean tflag = (bestCard.getSuit().compareTo(trumps) == 0
                            && t.getLeadSuit().compareTo(trumps) != 0);
            boolean wflag = ((t.findWinner()) % 2 == (this.playerID % 2));
            
            Iterator<Card> itr = hand.iterator();
            Card c;
            if (h.hasSuit(t.getLeadSuit())){        //can follow suit
                Card def = null;                        //default card
                Card sel = null;                        //chosen card
                while (itr.hasNext()){
                    c = itr.next();
                    if (c.getSuit().compareTo(t.getLeadSuit()) == 0){
                        if (def == null) def = c;
                        if (bestCard.compareTo(c) == -1){
                            sel = c;                            //update high
                        }
                    }
                }
                if (sel == null || tflag || wflag) return def;      //play low
                return sel;                                         //play high
            }
            else if (h.hasSuit(trumps)){    //can't follow suit & has trumps
                Card def = null;                        
                Card sel = null;
                while (itr.hasNext()){
                    c = itr.next();
                    if (def == null || def.getSuit().compareTo(trumps) == 0){
                        def = c;                        //lowest prefer non trump
                    }
                    if ((!tflag || tflag && bestCard.compareTo(c) == -1)
                            && c.getSuit().compareTo(trumps) == 0){
                        sel = c;                                //update high
                    }
                }
                if (sel == null || wflag) return def;               //play low
                return sel;                                         //play high
            }
            //can't follow suit & doesn't have trumps
            return hand.get(0);
        }
        
        //select all highset cards of the same rank and add to list
        ArrayList<Card> options = new ArrayList<>();
        Rank r = hand.get(hand.size() - 1).getRank();
        Card c = hand.remove(hand.size() - 1);
        while (c.getRank().ordinal() == r.ordinal()){
            options.add(c);
            if (hand.isEmpty()){
                break;
            }
            c = hand.remove(hand.size() - 1);
        }
        //choose a random one to play if more than one
        int random = ThreadLocalRandom.current().nextInt(0, options.size());
        return options.get(random);
    }
    
    @Override
    public void updateData(Trick t) {
    }
}
