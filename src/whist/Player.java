package whist;

import cards.*;
import cards.Card.Suit;

/**
 * interface for a Whist Player  programming coursework
 * @author ajb
 */
public interface Player {
    /**
     * Adds card c to this players hand
     * @param c 
     */    
    void dealCard(Card c);
    
    /** 
     * Allows for external setting of player strategy
     * @param s 
     */    
    void setStrategy(Strategy s);
    
    /**
     * Determines which of the players cards to play based on the in play trick t 
     * and player strategy
     * @param t
     * @return card to play
     */
    Card playCard(Trick t);
    
    /**
     * Game passes the players the completed trick 
     * @param t 
     */    
    void viewTrick(Trick t);
    void setTrumps(Suit s);
    int getID();
}
