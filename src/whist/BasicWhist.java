////////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File: BasicWhist.java
// File:            Hand.java
// Date:            20/01/19
// Author:          Tony Bagnall
// Modified by:     Rory Pinkney
// Student ID:      gxk17tru
// Student no:      100207541
//
////////////////////////////////////////////////////////////////////////////////
package whist;

import cards.Deck;
import cards.Card.Suit;
import cards.Card;
import java.util.Arrays;

/**
 * Runs a game of whist and keeps track of who the winner of each trick is
 * @author ajb
 */
public class BasicWhist {
    static final int NOS_PLAYERS = 4;
    static final int NOS_TRICKS = 13;
    static final int WINNING_POINTS = 7;
    int team1Points = 0;
    int team2Points = 0;
    Player[] players;
    
    public BasicWhist(Player[] pl){
        this.players = pl;
    }
    
    /**
     * Deals out all the cards in a shuffled deck (13 each)
     * @param newDeck
     */
    public void dealHands(Deck newDeck){
        for(int i = 0; i < 52; i++){
            players[i % NOS_PLAYERS].dealCard(newDeck.deal());
        }
    }
    
    /**
     * Plays one trick and returns the trick object which holds the winner
     * information
     * @param   firstPlayer ID of the player to start the trick (random)
     * @return  completed trick object
     */
    public Trick playTrick(Player firstPlayer){
        Trick t = new Trick(firstPlayer.getID());
        int playerID = firstPlayer.getID();
        for(int i = 0; i < NOS_PLAYERS; i++){
            int next = (playerID + i) % NOS_PLAYERS;
            Card c;
            t.setCard((c = players[next].playCard(t)), players[next]);
            System.out.println("player " + (players[next].getID() + 1) 
                            + " (" + ((players[next].getID() % 2) + 1) + ") " 
                            + c.getRank().toString() + " of " 
                                + c.getSuit().toString());
        }
        return t;
    }
    
    /**
     * Plays one game and adds the winning teams points above six to their score
     */
    public void playGame(){
        Deck d = new Deck();
        dealHands(d);
        int firstPlayer = (int)(NOS_PLAYERS * Math.random());
        Suit trumps = Suit.randomSuit();
        System.out.println(trumps.name().charAt(0) + trumps.name().substring(1)
                                                    .toLowerCase() + " trumps");
        Trick.setTrumps(trumps);
        for(int i = 0; i < NOS_PLAYERS; i++){ 
            players[i].setTrumps(trumps);
        }
        
        int[] score = {-6, -6};                         //1 point = 7 games one                   
        for(int i = 0; i < NOS_TRICKS; i++){
            Trick t = playTrick(players[firstPlayer]);
            firstPlayer = t.findWinner();
            int team = firstPlayer % 2;
            score[team] += 1;
            System.out.println("\nWinner = player " 
                        + (firstPlayer + 1) + " for team " + (team + 1) + "\n");
            
            for (int j = 0; j < NOS_PLAYERS; j++){
                players[j].viewTrick(t);
            }
        }
        
        if (score[0] > 0){
            System.out.println("Team 1 score " + score[0] + " points!\n");
            this.team1Points += score[0];
        }
        else {
            System.out.println("Team 2 score " + score[1] + " points!\n");
            this.team2Points += score[1];
        }
    }
    
    /**
     * Plays one match of whist made up of as many games as it takes for one team
     * to be above WINNING_POINTS (7)
     * @return integer indicating winning team (for my method which runs n games)
     */
    public int playMatch(){                 //return int indicating winning team
        team1Points = 0;
        team2Points = 0;
        while(team1Points < WINNING_POINTS && team2Points < WINNING_POINTS){
            playGame();
        }
        if(team1Points >= WINNING_POINTS) {
            System.out.println("Winning team is team 1 with " + team1Points);
            return 0;
        }
        else {
            System.out.println("Winning team is team 2 with " + team2Points);
            return 1;
        }
    }
    
    /**
     * Runs a game with one human player and 3 advanced
     */
    public static void advancedHumanGame(){
        Player[] p = new Player[NOS_PLAYERS];
        
        p[0] = new BasicPlayer(0, new HumanStrategy());
        for(int i = 1; i < 4; i++){
            p[i] = new BasicPlayer(i, new AdvancedStrategy(i));
        }
        
        BasicWhist bg = new BasicWhist(p);
        bg.playMatch();
    }
    
    /**
     * Runs a game with one human player and 3 basic
     */
    public static void humanGame(){
        Player[] p = new Player[NOS_PLAYERS];
        
        p[0] = new BasicPlayer(0, new HumanStrategy());
        for(int i = 1; i < 4; i++){
            p[i] = new BasicPlayer(i, new BasicStrategy(i));
        }
        
        BasicWhist bg = new BasicWhist(p);
        bg.playMatch();
    }
    
    /**
     * Runs a game with two basic players against two advanced
     */
    public static void advancedGame(){
        Player[] p = new Player[NOS_PLAYERS];
        
        //p[0] = new BasicPlayer(0, new BasicStrategy(0));
        for(int i = 0; i < 4; i+=2){
            p[i] = new BasicPlayer(i, new BasicStrategy(i));
            p[i+1] = new BasicPlayer(i+1, new AdvancedStrategy(i+1));
        }
        
        /*
         * This loop can play a large amount of matches and print the results
         */
        BasicWhist bg = new BasicWhist(p);
        int[] results = new int[2];
        for (int i = 0; i < 1000; i++){
            results[bg.playMatch()]++;
        }
        System.out.println("\n" + Arrays.toString(results));
    }
    
    /**
     * Runs a game with 4 basic players
     */
    public static void playTestGame(){
        Player[] p = new Player[NOS_PLAYERS];

        for(int i = 0; i < 4; i++){
            p[i] = new BasicPlayer(i, new BasicStrategy(i));
        }
        
        BasicWhist bg = new BasicWhist(p);
        bg.playMatch(); //Just plays a single match
    }
    
    public static void main(String[] args) {
        humanGame();
    }
    
}
