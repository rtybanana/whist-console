////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            Hand.java
// Date:            13/01/19
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package cards;

import static cards.Card.Rank.*;
import static cards.Card.Suit.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Holds a collection of cards which are treated as a player's hand also holds 
 * other relevant data such as hand value (aces both high and low) and the 
 * counts of each suit
 * @author gxk17tru
 */
public class Hand implements Serializable, Iterable {
    private final static long serialVersionUID = 300;
    private final ArrayList<Card> hand;
    private final ArrayList<Card> unsortedHand;
    private int[] suitCount;
    private int[] handValue;
    
    /**
     * Constructs a default hand with an empty list 
     */
    public Hand(){
        this.hand = new ArrayList<>();
        this.unsortedHand = new ArrayList<>();
    }
    
    /**
     * Copies a list of cards into this hands list of cards
     * @param cards
     */
    public Hand(ArrayList<Card> cards){
        this.hand = new ArrayList<>(cards);
        this.unsortedHand = new ArrayList<>(cards);
        calculateHand();
    }
    
    /**
     * Copies the cards from another Hand object into this hands list of cards
     * @param other Hand to copy the cards from into this hand
     */
    public Hand (Hand other){
        this.hand = new ArrayList<>(other.getHand());
        this.unsortedHand = new ArrayList<>(other.getHand());
        calculateHand();
    }
    
    public ArrayList<Card> getHand(){
        return this.hand;
    }
    
    public int[] getSuitCount(){
        return this.suitCount;
    }
    
    public int[] getHandValue(){
        return this.handValue;
    }
    
    /**
     * Adds one card to this hand
     * @param card Card object to add
     */
    public void add(Card card){
        this.hand.add(card);
        this.unsortedHand.add(card);
        calculateHand();
    }
    
    /**
     * Adds a list of cards to this hand
     * @param cards list of cards to add
     */
    public void add(ArrayList<Card> cards){
        this.hand.addAll(cards);
        this.unsortedHand.addAll(cards);
        calculateHand();
    }
    
    /**
     * Takes the cards from another hand and adds them to this hand
     * @param hand Hand object to get cards from
     */
    public void add(Hand hand){
        this.hand.addAll(hand.getHand());
        this.unsortedHand.addAll(hand.getHand());
        calculateHand();
    }
    
    /**
     * Removes one card from this hand
     * @param card Card object to remove
     * @return true if card removed, false if not found in list
     */
    public boolean remove(Card card){
        if (this.hand.remove(card)){
            this.unsortedHand.remove(card);
            calculateHand();
            return true;
        }
        return false;
    }
    
    /**
     * Takes the cards from another hand and removes any that appear in this hand
     * @param hand Hand object to get cards from
     * @return true if any removed, false if none found in this hand
     */
    public boolean remove(Hand hand){
        if (this.hand.removeAll(hand.getHand())){
            this.unsortedHand.removeAll(hand.getHand());
            calculateHand();
            return true;
        }
        return false;
    }
    
    /**
     * Removes a card from a given index in this list and returns it
     * @param index index of the card to remove
     * @return Card object removed from the provided index
     */
    public Card remove(int index){
        if (hand.size() >= index){
            this.unsortedHand.remove(index);
            calculateHand();
            return this.hand.remove(index);
        }
        return null;
    }
    
    /**
     * Returns a string representation of this hand
     * @return hand string
     */
    @Override
    public String toString(){
        String handString = "";
        for (Card c : hand){
            handString = handString.concat(c.toString() + " ");
        }
        if ("".equals(handString)) handString = "Empty Hand";
        return handString;
    }
    
    /*
    * Custom method which condenses all the verbose fileInput/OutputStream code
    * from the main method into obj.save("file") making it much easier to read
    */
    public void save(String filename){
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            try (ObjectOutputStream out = new ObjectOutputStream(fos)) {
                out.writeObject(this);
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    /*
    * Custom method which condenses all the verbose fileInput/OutputStream code
    * from the main method into obj = load("file") making it much easier to read
    */
    public static Hand loadHand(String filename){
        try {
            FileInputStream fis = new FileInputStream(filename);
            Hand load;
            try (ObjectInputStream in = new ObjectInputStream(fis)) {
                load = (Hand)in.readObject();
            }
            return load;
        }catch(IOException | ClassNotFoundException | ClassCastException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    /**
     * Sorts this hand (not unsortedHand) into ascending order
     */
    public void sort(){
        Collections.sort(this.hand);
    }
    
    /**
     * Sorts this hand (not unsortedHand) into order of rank (ignoring suit)
     */
    public void sortByRank(){
        Comparator<Card> cmprank = new Card.CompareRank();
        Collections.sort(this.hand, cmprank);
    }
    
    /**
     * Returns the number of a given suit in this hand
     * @param suit suit to count
     * @return the number of cards of the given suit
     */
    public int countSuit(Card.Suit suit){
        return getSuitCount()[suit.ordinal()];
    }
    
    /**
     * Returns the number of a given rank in this hand
     * @param rank rank to count
     * @return the number of cards of the given rank
     */
    public int countRank(Card.Rank rank){
        int count = 0;
        for (Card c : this.hand){
            if (c.getRank().compareTo(rank) == 0){
                count++;
            }
        }
        return count;
    }
    
    /**
     * Returns whether or not this hand contains any cards of a given suit
     * @param suit suit to check
     * @return true if this hand contains any of this suit, false if not
     */
    public boolean hasSuit(Card.Suit suit){
        return (getSuitCount()[suit.ordinal()] > 0);
    }
    
    /**
     * Returns a HandIterator object to iterate through this hand in the order
     * that the cards were added
     * @return instantiated HandIterator object
     */
    @Override
    public Iterator<Card> iterator(){
        return new HandIterator(this.unsortedHand);
    }
    
    /**
     * Nested iterator class to iterate through this hand in the order the cards
     * were added
     */
    private static class HandIterator implements Iterator<Card> {
        private int currentCard = 0;
        private final List<Card> cards;
        
        public HandIterator(List<Card> cards){
            this.cards = cards;
        }
        
        @Override
        public boolean hasNext(){
            return currentCard < cards.size();
        }
        
        @Override
        public Card next(){
            if (hasNext()){
                return cards.get(currentCard++);
            }
            return null;
        } 
        
        @Override
        public void remove(){}
    }

    /**
     * Sets the handValue and suitCount class variables after any changes have 
     * occurred to the hand (add or remove of any kind)
     */
    private void calculateHand(){
        int aces = 0;
        int value = 0;
        int[] suits = new int[4];
        for (Card c : this.hand){
            if (c.getRank().compareTo(ACE) != 0){
                suits[c.getSuit().ordinal()]++;         //increment suit count
                value += c.getRank().getValue();        //add to total value
            }
            else {
                suits[c.getSuit().ordinal()]++;         //increment suit count
                aces++;                                 //aces calculated after
            }
        }
        int[] values = new int[aces + 1];
        int acesValue = aces;                       //x aces = x points(1+1+...)
        for (int n = 0; n < aces + 1; n++){         //assuming aces low
            values[n] = value + acesValue;          
            acesValue += 10;                    //4, 14, 24, 34, 44 (4 aces)
        }                                       //3, 13, 23, 33     (3 aces)
                                                //2, 12, 22         (2 aces)
        this.suitCount = suits;
        this.handValue = values;
    }
    
    public static void main(String[] args) {
        /* HAND TEST HARNESS */
        
        //Question 1
        System.out.println("QUESTION 1");
        ArrayList<Card> al = new ArrayList<>();
        al.add(new Card(ACE, CLUBS));
        al.add(new Card(FIVE, DIAMONDS));
        al.add(new Card(QUEEN, SPADES));
        al.add(new Card(NINE, SPADES));
        al.add(new Card(THREE, HEARTS));
        System.out.println("list 'al' to make hand from: " + al.toString());
        
        Hand hand1 = new Hand();
        Hand hand2 = new Hand(al);
        Hand hand3 = new Hand(hand2);
        
        System.out.println("Hand1 created with no cards: " + hand1.toString());
        System.out.println("Hand2 created from list: " + hand2.toString());
        System.out.println("Hand3 created from above hand: " + hand3.toString());
        System.out.println();
        
        //Question 2
        System.out.println("QUESTION 2");
        System.out.println("Hand2 counts of each suit:");
        System.out.println("C D H S");
        for (int i = 0; i < 4; i++){
            System.out.print(hand2.getSuitCount()[i] + " ");
        }
        System.out.println();
        
        System.out.println("Hand2 remove 3H");
        hand2.remove(new Card(THREE, HEARTS));
        for (int i = 0; i < 4; i++){
            System.out.print(hand2.getSuitCount()[i] + " ");
        }
        System.out.println("\n");
        
        //Question 3
        System.out.println("QUESTION 3");
        System.out.println("hand3: " + hand3.toString());
        System.out.println("Serialise hand3");
        hand3.save("hand3.ser");
        System.out.println("Deserialise hand3 and load it into new hand4");
        Hand hand4 = loadHand("hand3.ser");
        if (hand4 != null){
            System.out.println("hand4: " + hand4.toString() + "\n");
        }
        
        //Question 4
        System.out.println("QUESTION 4");
        System.out.println("hand3 (one ace): " + hand3.toString());
        System.out.print("Possible values: ");
        for (int i : hand3.getHandValue()){
            System.out.print(i + " ");
        }
        System.out.println();
        
        hand3.add(new Card(ACE, SPADES));
        System.out.println("Add an ace (two aces): " + hand3.toString());
        System.out.print("Possible values: ");
        for (int i : hand3.getHandValue()){
            System.out.print(i + " ");
        }
        System.out.println("\n");
        
        //Question 5 and 6
        System.out.println("QUESTION 5 AND 6");
        System.out.println("hand1: " + hand1.toString());
        hand1.add(new Card(SIX, DIAMONDS));
        System.out.println("hand1 add 6D: " + hand1.toString());
        hand1.remove(new Card(SIX, DIAMONDS));
        System.out.println("hand1 remove 6D: " + hand1.toString());
        hand1.add(al);
        System.out.println("hand1 add list 'al': " + hand1.toString());
        System.out.println("hand3: " + hand3.toString());
        hand1.remove(hand3);
        System.out.println("hand1 remove hand3: " + hand1.toString());
        hand1.add(hand3);
        System.out.println("hand1 add hand3: " + hand1.toString());
        System.out.println("hand1 remove index [0]: " + hand1.remove(0).toString());
        System.out.println("hand1 after remove: " + hand1.toString() + "\n");
        
        //Question 7
        System.out.println("QUESTION 7");
        System.out.println("hand1 original order: " + hand1.toString());
        hand1.sort();
        System.out.println("hand1 sorted ascending: " + hand1.toString());
        System.out.print("hand1 iterator order: ");
        Iterator<Card> itr = hand1.iterator();
        while (itr.hasNext()){
            System.out.print(itr.next().toString() + " ");
        }
        System.out.println("\n");
        
        //Question 8
        System.out.println("QUESTION 8");
        hand2.add(new Card(FIVE, CLUBS));
        System.out.println("hand2 toString(): " + hand2.toString());
        //sortByRank()
        hand2.sortByRank();
        System.out.println("hand2 sortByRank(): " + hand2.toString());
        System.out.println("(5D 5C out of suit order as defined by compareTo)");
        //sort()
        hand2.sort();
        System.out.println("hand2 sort(): " + hand2.toString());
        System.out.println("(5C 5D now in suit order as defined by compareTo)");
        //countSuit()
        System.out.println("hand2 countSuit(SPADES): " + hand2.countSuit(SPADES));
        //countRank()
        System.out.println("hand2 countRank(QUEEN): " + hand2.countRank(QUEEN));
        //hasSuit()
        System.out.println("hand2 hasSuit(HEARTS): " + hand2.hasSuit(HEARTS));
    }
}
