////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            HumanStrategy.java
// Date:            25/01/19
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package whist;

import cards.Card;
import cards.Hand;
import java.util.*;

/**
 * This strategy leaves the card choice to the user by printing their options
 * and giving them a way to select one of them
 * @author roryp
 */
public class HumanStrategy implements Strategy {
    
    public HumanStrategy(){}     
    
    /**
     * Displays this players hand to the user and prompts them to select one to
     * play. It forces you to follow suit if you can, but leaves all other 
     * decisions to the human player.
     * 
     * @param h this players hand
     * @param t the current trick
     * @return the card selected by the user to play
     */
    @Override
    public Card chooseCard(Hand h, Trick t) {
        ArrayList<Card> hand = new ArrayList<>(h.getHand());
        Collections.sort(hand);
        for (int i = 0; i < hand.size(); i++){
            System.out.print("(" + i + ")" + hand.get(i).toString() + " ");
        }
        System.out.println("\nPlease select a card using the numbers in brackets");
        
        Scanner sc = new Scanner(System.in);
        while (true){
            int i;
            try {
                i = sc.nextInt();
            }catch(InputMismatchException e){
                i = -1;
                sc.next();
            }
            
            //if selection not in range
            if (i < 0 || i > hand.size() - 1){
                System.out.println("Please type a valid number");
            }
            //if not following suit when they can
            else if (t.getPlayerNo() != 0){
                if (hand.get(i).getSuit().compareTo(t.getLeadSuit()) != 0
                                                && h.hasSuit(t.getLeadSuit())){
                    System.out.println("You must follow suit if you can");
                }
                else {
                    return hand.get(i);
                }
            }
            else {
                return hand.get(i);
            }
        }
    }

    @Override
    public void updateData(Trick t) {}
}
