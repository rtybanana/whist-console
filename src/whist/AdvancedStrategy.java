////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            AdvancedStrategy.java
// Date:            31/01/19
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package whist;

import cards.Card;
import cards.Card.*;
import cards.Hand;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A more advanced strategy that a player can employ to select better cards that
 * statistically win more often.
 * @author roryp
 */
public class AdvancedStrategy implements Strategy{
    private final int playerID;
    private Suit trumps;
    private int[][] usedCards;              //matrix representing used cards

    public AdvancedStrategy(int p){
        this.playerID = p;
    }
    
    /**
     * Defines how this strategy selects a card to play based on the rules of
     * whist, the previous cards played in the trick and also by keeping track
     * of the cards played in previous tricks.
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
        
        if (hand.size() == 13){                         //when dealt a new hand
            usedCards = new int [13][4];
            for (int j = 0; j < 4; j++){
                for (int i = 0; i < 13; i++){
                    usedCards[i][j] = -1;
                }
            }
        }
        else if (hand.size() ==  1){
            return hand.get(0);
        }
        
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
            if (t.getPlayerNo() == 3){                  //if last player
                if (h.hasSuit(t.getLeadSuit())){    //can follow suit
                    Card def = null;                    //default card
                    while (itr.hasNext()){
                        c = itr.next();
                        if (c.getSuit().compareTo(t.getLeadSuit()) == 0){
                            if (def == null) def = c;   //lowest of lead suit
                            if (bestCard.compareTo(c) == -1){
                                return c;                           //play high
                            }
                        }
                    }
                    return def;                                     //play low
                }
                else if (h.hasSuit(trumps)){        //cant follow suit, can trump
                    Card def = null;
                    while (itr.hasNext()){
                        c = itr.next();
                        if (def == null || def.getSuit().compareTo(trumps) == 0){
                            def = c;                    //lowest prefer non trump
                        }
                        //if trick not trumped or have higher trump
                        if ((!tflag || tflag && bestCard.compareTo(c) == -1)
                                && c.getSuit().compareTo(trumps) == 0){
                            return c;                               //play high
                        }
                    }
                    return def;                                     //play low
                }
                //cant follow suit, cant trump
                return hand.get(0);                             //play lowest
            }
            //can follow suit
            if (h.hasSuit(t.getLeadSuit())){ 
                Card def = null;
                Card sel = null;
                while (itr.hasNext()){
                    c = itr.next();
                    if (c.getSuit().compareTo(t.getLeadSuit()) == 0){
                        if (def == null) def = c;       //lowest of lead suit
                        if (bestCard.compareTo(c) == -1){
                            sel = c;                            //update high
                        }
                    }
                }
                if (sel == null || tflag || wflag) return def; //play low
                return sel;                                         //play high
            }
            //cant follow suit, has trumps
            else if (h.hasSuit(trumps)){
                Card def = null;                        //default card
                Card sel = null;                        //chosen card
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
            //can't follow suit, doesn't have trumps
            return hand.get(0);
        }
        
        /*
         * Code following is if this player is leading the current trick
         */
        Collections.reverse(hand);                          //descending order
        ArrayList<Card> options = new ArrayList<>();
        /*
         * unseenSuits
         * Stores the suits which still need to be checked for how many cards
         * are between your your top cards and becoming the highest unplayed 
         * card of their respective suits.
         */
        ArrayList<Integer> unseenSuits = new ArrayList<>();
        /*
         * untilHighest
         * Stores the number of unplayed cards higher than this players highest
         * card of each suit. The suit with the lowest number of higher cards 
         * until one of yours becomes the highest unplayed (of which you also
         * have a lower card of the same suit to play) will be selected to play
         * in order to coax out the higher cards making yours the controlling
         * card.
         */
        ArrayList<Integer> untilHighest = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            unseenSuits.add(i);
            untilHighest.add(13);
        }
        
        int index = 0;
        while (!unseenSuits.isEmpty() && index < hand.size()){
            int s = hand.get(index).getSuit().ordinal();
            int r = hand.get(index).getRank().ordinal();
            if (unseenSuits.contains(s)){
                unseenSuits.remove(Integer.valueOf(s));
                int higherCards = 0;
                for (int i = r+1; i < 13; i++){
                    if (usedCards[i][s] == -1){
                        higherCards += 1;
                    }
                }
                //if there are no higher unplayed cards in other hands
                if (higherCards == 0){
                    untilHighest.set(s, 0);
                    options.add(hand.get(index));   //highest unplayed of suit
                }
                else {
                    untilHighest.set(s, higherCards);
                }
            }
            index++;
        }
        
        //if no options which are the highest unplayed rank of their suit
        if (options.isEmpty()){
            Collections.reverse(hand);                      //ascending order
            Card card;
            while (true){
                int minSuit = untilHighest.indexOf(Collections.min(untilHighest));
                if (untilHighest.get(minSuit) == 13) break;
                //if they have lower of this suit available
                if (h.getSuitCount()[minSuit] > 1){
                    card = hand.get(0);
                    int i = 0;
                    //iterate through until the first (lowest) of this suit
                    while (card.getSuit().ordinal() != minSuit){
                        card = hand.get(i++);
                    }
                    return card;                                    //play low
                }
                untilHighest.set(minSuit, 13);
            }
            options = new ArrayList<>(hand);
        }
        
        int random = ThreadLocalRandom.current().nextInt(0, options.size());
        return options.get(random);
    }
    
    /**
     * Updates this classes card counting data to help it to select cards more
     * statistically likely to win
     * 
     * @param t the previously completed trick
     */
    @Override
    public void updateData(Trick t) {
        for (int i = 0; i < 4; i++){
            Card c = t.getTrick()[i];
            /* 
             * Setting matrix cell which represents this card to the playerID 
             * which played it to keep track of whos played what
             */
            usedCards[c.getRank().ordinal()][c.getSuit().ordinal()] = i;
        }
    }
}
