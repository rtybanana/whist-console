////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            Card.java
// Date:            06/01/19
// Author:          Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package cards;

import java.io.*;
import java.util.*;
import static cards.Card.*;
import static cards.Card.Suit.*;
import static cards.Card.Rank.*;

/**
 * Holds a collection of 52 card objects and methods relevant to them
 * @author gxk17tru
 */
public class Deck implements Serializable, Iterable {
    private static final long serialVersionUID = 49;
    private transient DeckList deck = new DeckList();
    
    /**
     * Private extension to the ArrayList class which stops a deck from growing
     * larger than 52 cards long. As requested in the coursework description.
     */
    private class DeckList extends ArrayList<Card> {
        @Override
        public boolean add(Card c){
            if (this.size() < 52){
                return super.add(c);
            }
            System.out.println("Deck at maximum capacity");
            return false;
        }
    }
    
    public Deck(){
        for (Suit suit : Suit.values()){
            for (Rank rank : Rank.values()){
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);
    }
    
    /**
     * Removes the top card from the deck and returns it using the iterator
     * @return top card in the deck
     */
    public Card deal(){
        Iterator<Card> itr = this.iterator();
        Card a = itr.next();
        deck.remove(a);
        return a;
    }
    
    /**
     * Returns the size of the deck as an integer
     * @return integer size of the deck
     */
    public int size(){
        return this.deck.size();
    }
    
    /**
     * Reinstantiates the deck dealing it a fresh set of 52 cards and shuffling
     * them
     */
    public void newDeck(){
        this.deck.clear();
        for (Suit suit : Suit.values()){
            for (Rank rank : Rank.values()){
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);
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
    public static Deck loadDeck(String filename){
        try {
            FileInputStream fis = new FileInputStream(filename);
            Deck load;
            try (ObjectInputStream in = new ObjectInputStream(fis)) {
                load = (Deck)in.readObject();
            }
            return load;
        }catch(IOException | ClassNotFoundException | ClassCastException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    /**
     * Overwrites the standard writeObject() method to write only the spades in
     * the invoking Deck object's deck. The Deck class variable is transient so
     * if this method was not overwritten the deck would not be saved at all.
     * 
     * @param out
     * @throws IOException 
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        Card a;
        Iterator<Card> itr = spadeIterator();
        DeckList list = new DeckList();
        
        while (itr.hasNext()){
            a = itr.next();
            list.add(a);
        }
        Collections.reverse(list);
        out.writeObject(list);
    }
    
    /**
     * Overwrites the standard readObject() method to understand a deck object
     * serialised by the custom writeObject() above and forces it to write the
     * list of cards to the transient 'deck' class variable, because the standard
     * readObject() cannot understand 
     * 
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private void readObject(ObjectInputStream in) throws IOException, 
                                                        ClassNotFoundException {
        deck = (DeckList)in.readObject();
    }
    
    /**
     * Returns a DeckIterator object which will iterate through this.deck in
     * reverse order (top first)
     * 
     * @return instantiated DeckIterator object
     */
    @Override 
    public Iterator<Card> iterator(){
        return new DeckIterator(this.deck);
    }
    
    /**
     * Nested iterator class to iterate in reverse order through the deck 
     * (top first)
     */
    private static class DeckIterator implements Iterator<Card>{
        private int currentCard;
        private final List<Card> cards;
        
        public DeckIterator(List<Card> cards){
            this.cards = new ArrayList<>(cards);
            this.currentCard = cards.size();
        }
        
        @Override
        public boolean hasNext(){
            return currentCard > 0;
        }
        
        @Override
        public Card next(){
            if (hasNext()){
                return cards.remove(--currentCard);
            }
            return null;
        } 
        
        @Override
        public void remove(){}
    }
    
    /**
     * Returns a SpadeIterator object which will iterate through the spades  of
     * this.deck
     * 
     * @return instantiated SpadeIterator object
     */
    public Iterator<Card> spadeIterator(){
        return new SpadeIterator(this.deck);
    }
    
    /**
     * Nested iterator class to iterate just through the spades in a deck
     */
    private static class SpadeIterator implements Iterator<Card>{
        private int currentCard;
        private final List<Card> cards;
        
        public SpadeIterator(List<Card> cards){
            this.cards = new ArrayList<>(cards);
            this.currentCard = cards.size();
        }
        
        @Override
        public boolean hasNext(){
            Iterator<Card> itr = cards.iterator();
            while (itr.hasNext()){
                if (itr.next().getSuit().compareTo(SPADES) == 0){
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public Card next(){
            while (hasNext()){
                Card nextSpade = cards.remove(--currentCard);
                if (nextSpade.getSuit().compareTo(SPADES) == 0){
                    return nextSpade;
                }
            }
            return null;
        }
        
        @Override
        public void remove(){
        }
    }
    
    public static void main(String[] args) {
        /* DECK TEST HARNESS */
        
        //Question 1
        System.out.println("QUESTION 1");
        Deck deck = new Deck();
        System.out.println("Attempt to add a 53rd card");
        deck.deck.add(new Card(TWO, DIAMONDS));
        System.out.println();
        
        //Question 2
        System.out.println("QUESTION 2");
        Iterator<Card> itr = deck.iterator();
        int i = 1;
        while (itr.hasNext()){
            System.out.print(itr.next() + " ");
            if (i % 13 == 0){
                System.out.println();
            }
            i++;
        }
        System.out.println();
        
        //Question 3
        System.out.println("QUESTION 3");
        System.out.println("Deck deal(): " + deck.deal().toString());
        System.out.println("Deck size(): " + deck.size());
        System.out.println("Deck newDeck()");
        deck.newDeck();
        System.out.println("Deck size(): " + deck.size());
        System.out.println();
        
        //Question 4
        System.out.println("QUESTION 4");
        itr = deck.iterator();
        i = 1;
        while (itr.hasNext()){
            System.out.print(itr.next() + " ");
            if (i % 13 == 0){
                System.out.println();
            }
            i++;
        }
        System.out.println();
        
        //Question 5
        System.out.println("QUESTION 5");
        System.out.println("Deck deal(): " + deck.deal());
        System.out.println("Deck deal(): " + deck.deal());
        System.out.println("Deck deal(): " + deck.deal());
        System.out.println("Dekc size(): " + deck.size());
        System.out.println();
        
        //Question 6
        System.out.println("QUESTION 6");
        deck.newDeck();
        System.out.println("Deck iterator:");
        itr = deck.iterator();
        i = 1;
        while (itr.hasNext()){
            System.out.print(itr.next() + " ");
            if (i % 13 == 0){
                System.out.println();
            }
            i++;
        }
        
        System.out.println("Deck spade iterator:");
        itr = deck.spadeIterator();
        while (itr.hasNext()){
            System.out.print(itr.next() + " ");
        }
        System.out.println("\n");
        
        //Question 8
        System.out.println("QUESTION 8");
        System.out.println("Serialise the deck above");
        System.out.println("Deserialise and only the spades should be present:");
        deck.save("deck.ser");
        Deck deck2 = loadDeck("deck.ser");
        itr = deck2.iterator();
        while (itr.hasNext()){
            System.out.print(itr.next() + " ");
        }
        System.out.println();
    }
}
