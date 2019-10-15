////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            Card.java
// Date:            27/12/18
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package cards;

import java.io.*;
import java.io.Serializable;
import java.util.*;
import static cards.Card.Rank.*;
import static cards.Card.Suit.*;

/**
 * Represents a card with suit and rank enumerated types
 * @author gxk17tru
 */
public class Card implements Serializable, Comparable<Card> {
    private static final long serialVersionUID = 100;

    /** Enumerated Type
     * Holds rank name and associated value for an instantiated Card object
     */
    public enum Rank {
        /**
         * One name and associated value for each rank, TEN - KING have the 
         * same associated value.
         */
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
        NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);
        
        private final int value;
        private static final Rank[] vals = Rank.values();
        
        private Rank(int value){
            this.value = value;
        }
        
        @Override
        public String toString(){
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
        
        /**
         * @return      Next card rank in the enumerated type list ie. 
         *              QUEEN -> KING
         */
        public Rank getNext(){
            return vals[(this.ordinal()+1) % vals.length];
        }
        
        public Rank getPrevious(){
            return vals[(this.ordinal()-1) % vals.length];
        }
        
        public int getValue(){
            return value;
        }
    }
    private final Rank rank;
    
    /** Enumerated Type
     * Holds suit name for an instantiated Card object
     */
    public enum Suit {
        /**
         * One value for each suit
         */
        CLUBS, DIAMONDS, HEARTS, SPADES;
        
        private static final Suit[] suits = Suit.values();
        
        @Override
        public String toString(){
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
        
        /**
         * @return      Randomly selected suit
         */
        public static Suit randomSuit(){
            Random r = new Random();
            int i = r.nextInt(3);
            
            return suits[i];
        }
    }
    private final Suit suit;
    
    /**
     * Constructs a card from the provided valid enumerated type names
     * 
     * @param rank rank of the card to instantiate
     * @param suit suit of the card to instantiate
     */
    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }
    
    public Rank getRank(){
        return this.rank;
    }
    
    public Suit getSuit(){
        return this.suit;
    }
    
    /**
     * Returns a string representation of this card
     * @return card string
     */
    @Override
    public String toString(){
        String toString = "";
        if (this.rank.ordinal() < 9){
            toString += Integer.toString(this.rank.ordinal() + 2);
        }
        else {
            toString += Character.toString(this.rank.name().charAt(0));
        }
        
        toString += Character.toString(this.suit.name().charAt(0));
        return toString;
    }
    /**
     * Returns an integer based on the comparison of two Cards, 'this.' and 'b'
     * @param   b
     * @return  -1 if this.rank is lower than b.rank
     *          -2 if ranks are equal but this.suit is lower than b.suit
     *           0 if both rank and suit are equal
     *           1 if this.rank is greater than b.rank
     *           2 if ranks are equal but this.suit is greater than b.suit
     */
    @Override
    public int compareTo(Card b){
        if (this.rank.compareTo(b.rank) < 0){
            return -1;
        }
        else if (this.rank.compareTo(b.rank) == 0){
            if (this.suit.compareTo(b.suit) < 0){
                return -2;
            }
            else if (this.suit.compareTo(b.suit) == 0){  //cards are equal
                return 0;
            }
            else if (this.suit.compareTo(b.suit) > 0){
                return 2;
            }
        }
        return 1;
    }
    
    /**
     * Overrides the standard equals() method so that an ArrayList<Card> can use
     * its methods which rely on .equals to work properly, such as the .remove 
     * and .contains methods.
     * 
     * @param   obj the object to compare to the invoking object
     * @return  true of equal, false if otherwise
     */
    @Override
    public boolean equals(Object obj){
        if (obj == null || !(obj instanceof Card)) return false;
        Card card = (Card)obj;
        return this.compareTo(card) == 0;
    }

    /**
     * Overridden in conjunction with equals() method so that if hashmaps get 
     * implemented in a future version, the overridden equals() method will 
     * still operate as expected.
     * @return int  Hash code of this card (unique for different card values)
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.rank);
        hash = 53 * hash + Objects.hashCode(this.suit);
        return hash;
    }
    
    /*
    * Custom method which condenses all the verbose fileInput/OutputStream code
    * from the main method into obj.save("file") making it much easier to read
    */
    public void save(String filename) {
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
    public static Card loadCard(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            Card load;
            try (ObjectInputStream in = new ObjectInputStream(fis)) {
                load = (Card)in.readObject();
            }
            return load;
        }catch(IOException | ClassNotFoundException | ClassCastException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    /**
     * Returns the highest card in a list of cards
     * 
     * @param   list the list of cards to find the highest one of
     * @return  the highest card in the list
     */
    public static Card max(ArrayList<Card> list){
        Iterator<Card> itr = list.iterator();
        Card a = itr.next();
        while (itr.hasNext()) {
            Card b = itr.next();
            if (b.compareTo(a) > 0){
                a = b;
            }
        }
        return a;
    }
    
    /**
     * Returns an ArrayList of cards which a given comparator has created based
     * on them being 'greater' than a provided Card. A card can be considered 
     * 'greater' by the comparator even if they are lesser in ascending order.
     * For example if the comparator sorts into descending order.
     * 
     * @param   list the list of cards to choose from
     * @param   comp the comparator used to choose the cards which are 'greater'
     * @param   card the card to compare the cards in 'list' with
     * @return  a list of cards which the comparator indicates are 'greater'
     */
    public static ArrayList<Card> chooseGreater(ArrayList<Card> list,
                                            Comparator<Card> comp, Card card){
        Iterator<Card> itr = list.iterator();
        ArrayList<Card> newList = new ArrayList<>();
        
        while (itr.hasNext()){
            Card c = itr.next();
            if (comp.compare(card, c) == -1){
                newList.add(c);
            }
        }
        return newList;
    }
    
    /**
     * Nested comparator class to compare in descending order of rank but still
     * ascending order of suit
     */
    public static class CompareDescending implements Comparator<Card> {
        @Override
        public int compare(Card a, Card b){
            int n = a.compareTo(b);
            if (n == 1 || n == -2){ 
                //if rank is lesser || ranks are equal and suit is greater
                return -1;                      //select
            }
            return 0;                           //skip
        }
    }
    
    /**
     * Nested comparator class to compare solely in ascending order of rank 
     * (ignoring the order of suit entirely)  
     */
    public static class CompareRank implements Comparator<Card> {
        @Override
        public int compare(Card a, Card b){
            if (a.compareTo(b) == -1){               //if rank is greater
                return -1;                      //select
            }
           return 0;                            //skip
        }
    }
    
    /**
     * A lambda implementation of chooseGreater(), using two custom comparators
     * to choose 'greater' cards from a list sorted in ascending order of rank
     * and then suit
     * 
     * @param   list the list of cards to choose from
     * @param   card the card to compare the cards in 'list' with
     * @return  the list of cards from 'list' 'greater' than card
     */
    public static ArrayList<Card> selectTest(ArrayList<Card> list, Card card){
        Comparator<Card> byRank = new CompareRank();
        Comparator<Card> descending = new CompareDescending();
        ArrayList<Card> newList = new ArrayList<>();
        
        list.forEach(cards -> {
            if (byRank.compare(card, cards) == -1){
                newList.add(cards);
            }
            else if (card.getRank() == cards.getRank()){
                if (descending.compare(card, cards) == -1){
                    newList.add(cards);
                }
            }
        });
        
        return newList;
    }
    
    public static void main(String[] args) {
        /* CARD TEST HARNESS */
        
        //Question 1
        System.out.println("QUESETION 1");
        Card queenD = new Card(QUEEN, DIAMONDS);
        System.out.println("Card to serialise: " + queenD);
        queenD.save("queenD.ser");
        Card load = loadCard("queenD.ser");
        if (load != null) {
            System.out.println("Deserialised card: " + load.toString() + "\n");
            
        //Question 2 (with deserialised card)
            System.out.println("QUESTION 2");
            System.out.println(load.toString());
            System.out.println("Value: " + load.rank.getValue());
            System.out.println("Next enum: " + load.rank.getNext());
            System.out.println("Random suit: " + Suit.randomSuit());
        }
        System.out.println("ACE getNext(): " + ACE.getNext() + "\n");
        
        //Question 3
        System.out.println("QUESTION 3");
        System.out.println("Jack of Hearts toString(): " 
                            + new Card(JACK, HEARTS).toString() + "\n");
        
        //Question 4
        System.out.println("QUESTION 4");
        Comparator<Card> cmprank = new CompareRank();
        Comparator<Card> cmpdesc = new CompareDescending();
        ArrayList<Card> al = new ArrayList<>();
        al.add(new Card(JACK, DIAMONDS));
        al.add(new Card(FOUR, SPADES));
        al.add(new Card(TEN, SPADES));
        al.add(new Card(THREE, CLUBS));
        al.add(new Card(TWO, CLUBS));
        al.add(new Card(SIX, HEARTS));
        al.add(new Card(THREE, DIAMONDS));
        
        System.out.println("Unordered list:\n" + al);
        Collections.sort(al);
        System.out.println("In ascending order (CompareTo):\n" + al + "\n");
        
        //Question 5
        System.out.println("QUESTION 5");
        System.out.println(al);
        System.out.println("Test of max(): " + max(al) + "\n");
        
        //Question 6
        System.out.println("QUESTION 6");
        al.add(new Card(THREE, HEARTS));
        al.add(new Card(THREE, SPADES));
        Collections.shuffle(al);
        Collections.sort(al, cmpdesc);
        System.out.println("CompareDescending:\n" + al);
        Collections.shuffle(al);
        Collections.sort(al, cmprank);
        System.out.println("CompareRank:\n" + al + "\n");
        
        //Question 7
        System.out.println("QUESTION 7");
        Card fiveD = new Card(FIVE, DIAMONDS);
        ArrayList<Card> greater = chooseGreater(al, cmprank, fiveD);
        System.out.println("Test of chooseGreater(" + fiveD.toString() 
                            + "):\n" + greater.toString() + "\n");
       
        //Question 8
        System.out.println("QUESTION 8");
        System.out.println("Test of selectTest():\n" + selectTest(al, fiveD));
    }
}