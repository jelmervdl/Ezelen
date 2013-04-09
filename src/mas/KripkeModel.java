/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jelmer & Inge
 */
public class KripkeModel
{
    final int N_AGENTS = 3;
    
    final int N_CARDS = N_AGENTS;
    
    private class State
    {
        public Hand[] hands;
        
        public State()
        {
            hands = new Hand[N_AGENTS];
            
            for (int agent = 0; agent < N_AGENTS; ++agent)
                hands[agent] = new Hand();
        }
        
        public State(State prev)
        {
            hands = new Hand[N_AGENTS];
            
            for (int agent = 0; agent < N_AGENTS; ++agent)
                hands[agent] = new Hand(prev.hands[agent]);
        }
        
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            
            for (int agent = 0; agent < N_AGENTS; ++agent)
            {
                sb.append("agent ").append(agent).append(":");
                sb.append(hands[agent]);
                sb.append("\n");
            }
            
            return sb.toString();
        }
    }
    
    private class Hand
    {
        public Card[] cards;
        
        public Hand()
        {
            cards = new Card[N_AGENTS];
        }
        
        public Hand(Hand other)
        {
            cards = new Card[N_AGENTS];
            
            for (int card = 0; card < N_AGENTS; ++card)
                cards[card] = other.cards[card];
        }
        
        public boolean isFull()
        {
            return cards[N_AGENTS - 1] != null;
        }
        
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            
            for (int c = 0; c < N_AGENTS; ++c)
                sb.append(cards[c]).append(" ");
            
            sb.append("]");
            
            return sb.toString();
        }
    }
    
    private Set<State> states;
    
    private int hits;
    
    public KripkeModel()
    {
        states = new HashSet<State>();
        
        hits = 0;
        
        assert Card.getDeck(N_AGENTS).size() == N_AGENTS * N_AGENTS : "Card deck is of unexpected size";
        
        populateStates(new State(), 0, 0, Card.getDeck(N_AGENTS));
        
        // For N_AGENTS=4 this will be C(16,4) * C(12,4) * C(8,4) = 63_063_000 states :/
        // (hint: http://www.ikhebeenvraag.be/vraag/29757)
        // System.out.println("Model contains " + hits + " possible states");
        
        System.out.println("Model contains " + states.size() + " possible states");
    }
    
//    private void populateStates(State state, int agent, List<Card> cards)
//    {
//        for (int c1 = 0; c1 < cards.size() - 3; ++c1) {
//            for (int c2 = c1 + 1; c2 < cards.size() - 2; ++c2) {
//                for (int c3 = c2 + 1; c3 < cards.size() - 1; ++c3) {
//                    for (int c4 = c3 + 1; c4 < cards.size(); ++c4)
//                    {
//                        Card[] hand = new Card[]{cards.get(c1), cards.get(c2), cards.get(c3), cards.get(c4)};
//
//                        //State state = new State(base);
//                        //state.hands[agent].cards = hand;
//
//                        if (agent == N_AGENTS - 1) {
//                            assert(cards.size() == N_AGENTS);
//                            //states.add(state);
//                            ++hits;
////                            System.out.println(state);
//                        }
//                        else {
//                            List<Card> next = new ArrayList<Card>(cards);
//                            
//                            for (int c = 0; c < N_AGENTS; ++c)
//                                next.remove(hand[c]);
//                            
//                            populateStates(null, agent + 1, next);
//                        }
//                    }
//                }
//            }
//        }
//    }
    
    private void populateStates(State state, int agent, int card, List<Card> cards)
    {
        System.out.println("agent " + agent + "; card " + card + "; cards " + cards);
        System.out.println(state);
        
        assert cards.size() == N_CARDS * N_AGENTS - agent * N_CARDS : "The number of cards is wrong";
        
        if (agent == N_AGENTS) {
            assert cards.isEmpty() : "List of cards is not empty";
            states.add(new State(state));
        }
        else if (card == N_CARDS) {
            List<Card> next = new ArrayList<Card>(cards);
            
            for (int c = 0; c < N_CARDS; ++c) {
                assert state.hands[agent].cards[c] != null : "A card in the hand is still null";
                
                assert next.contains(state.hands[agent].cards[c]) : "There is a card missing";
                next.remove(state.hands[agent].cards[c]);
            }
            
            populateStates(state, agent + 1, 0, next);
        }
        else {
            assert card < cards.size() - ((N_AGENTS - card) - 1) : "What?";
            
            for (int c = card; c < cards.size() - ((N_AGENTS - card) - 1); ++c)
            {
                state.hands[agent].cards[card] = cards.get(c);
                populateStates(state, agent, card + 1, cards);
            }
        }
    }
}
