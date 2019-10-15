////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            Trick.java
// Date:            24/01/19
// Author:          Tony Bagnall
// Modified by:     Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package whist;
import cards.Card;
import cards.Card.Suit;
import java.util.*;


/**
 * Skeleton class for storing information about whist tricks
 * @author ajb
 */
public class Trick{
    public static Suit trumps;
    private final Card[] trick;
    private final int leadPlayer;
   
    public Trick(int p){
        this.leadPlayer = p;        //p is the lead player 
        this.trick = new Card[4];
    }
   
    public static void setTrumps(Suit s){
        trumps = s;
    }
    
    /**
     * Gets the suit of the lead card played, if no cards played yet null 
     * pointer exception is caught and handled
     * @return the Suit of the lead card.
     */    
    public Suit getLeadSuit(){
        try {
            return trick[leadPlayer].getSuit();
        }catch(NullPointerException e){
            //no cards have been played yet
            return null;
        }
    }
    
    /**
     * Additional method to count the current number of played cards to find out
     * what turn number we are on now (used to know if a player is going first
     * or last or neither). 0 is first, 3 is last
     * 
     * @return number from 0 - 3 indicating current turn in the trick
     */
    public int getPlayerNo(){
        int playerNo = 0;
        for (Card c : trick){
            if (c != null) playerNo += 1;
        }
        return playerNo;
    }
    
    public Card[] getTrick(){
        return trick;
    }
    
    /**
     * Records the Card c played by Player p for this trick
     * @param c
     * @param p 
     */
    public void setCard(Card c, Player p){
        this.trick[p.getID()] = c;
    }
    
    /**
     * Returns the card played by player with id p for this trick. I only call 
     * this method on completed tricks so it will never happen, but in a 
     * different implementation this can potentially throw a NullPointerException
     * 
     * @param p
     * @return the card played by this playerID
     */    
    public Card getCard(Player p){
        return trick[p.getID()];
    }
    
    /**
     * Additional method to return the winning card using findWinner() to 
     * get the playerID and using that to grab the card they played from trick[]
     * 
     * @return currently winning card in this trick
     */
    public Card getBestCard(){
        return trick[findWinner()];
    }
    
    /**
     * Finds the ID of the winner of a completed trick. In my implementation
     * this method is called by strategy (via getBestCard) on incomplete tricks.
     * This means that potentially it can throw a NullPointerException if it's 
     * called on a trick with no cards played yet. My strategy performs a check
     * to make sure that it isn't the first player in the trick before calling
     * this method to prevent this from happening
     * 
     * @return 
     */    
    public int findWinner(){
        Suit leadSuit = getLeadSuit();
        
        int winner = leadPlayer;
        Comparator<Card> byRank = new Card.CompareRank();
        for (int n = 1; n < (getPlayerNo()); n++){
            int current = (leadPlayer + n) % 4;
            if (trick[current].getSuit().compareTo(leadSuit) == 0){
                if (byRank.compare(trick[winner], trick[current]) == -1){
                    winner = current;
                }
            }
            else if (trick[current].getSuit().compareTo(trumps) == 0){
                leadSuit = trick[current].getSuit();
                winner = current;
            }
        }
        
        return winner;
    }
}
