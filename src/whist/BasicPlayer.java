////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            BasicPlayer.java
// Date:            24/01/19
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package whist;

import cards.Card;
import cards.Hand;

/**
 * This class controls how a player interfaces with the game it's playing.
 * It's not specific to any one game and can be reused with a different strategy
 * to obey any game logic.
 * @author gxk17tru
 */
public class BasicPlayer implements Player {
    private final Hand hand;
    private Strategy strategy;
    private final int ID;
    
    public BasicPlayer(int i, Strategy s){
        this.hand = new Hand();
        this.ID = i;
        this.strategy = s;
    }

    @Override
    public void dealCard(Card c) {
        hand.add(c);
    }

    @Override
    public void setStrategy(Strategy s) {
        this.strategy = s;
    }

    @Override
    public Card playCard(Trick t) {
        Card c = strategy.chooseCard(hand, t);
        this.hand.remove(c);
        return c;
    }

    @Override
    public void viewTrick(Trick t) {
        this.strategy.updateData(t);
    }

    @Override
    public void setTrumps(Card.Suit s) {}

    @Override
    public int getID() {
        return this.ID;
    }
    
}
