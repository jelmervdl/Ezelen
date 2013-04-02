/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jelmer & Inge
 */
public class KripkeModel
{

    public enum Event {
        GAVE_CARD,
        RECEIVED_CARD,
        NEXT_ROUND
    }
    
    private Map<Card.Type,Integer> seenCards;
    
    public KripkeModel()
    {
        seenCards = new EnumMap<Card.Type, Integer>(Card.Type.class);
        
//        Variable x = new Variable();
//        Variable y = new Variable();
//        Variable z = new Variable();
//        knowledge.add(new Implication(
//                new K(x, new Predicate("HasCard", y)),
//                new ForAll(z,
//                    new Implication(
//                        new Negation(new Identical(x, z)),
//                        new Negation(new K(z, new Predicate("HasCard", y)))))));
    }
    
    public void tell(Event action, Card card, Agent agent)
    {
        switch (action)
        {
            case RECEIVED_CARD:
                // Add knowledge to our memory
                // - Agent agent does not have Card card
//                knowledge.add(new K(agent, new Negation(new Predicate("HasCard", card))));
                // - No other agent can have Card card
                // for (Agent a : table.getAgents())
                //    knowledge.add(new K(an_agent, new Negation(new Predicate("HasCard" + card))));
                // - If I haven't seen a Card of Type type for 4 rounds,
                //   an other agent is collecting the same type. (Intention)
                
                // Keep track of which cards are passed along, on which we can
                // base our next strategy.
                markAsSeen(card);
                break;
               
            case GAVE_CARD:
                // Add knowledge to our memory
                // - Agent agent has Card card
//                knowledge.add(new K(agent, new Predicate("HasCard", card)));
                // (- By inference, No other agent other than agent has Card card)
                break;
        }
    }
    
    public void tell(Event action)
    {
        switch (action)
        {
            case NEXT_ROUND:
                // Add knowledge to our memory
                // - If we knew that agent X had card Y, we know believe it is
                //   possible for X to have card Y, and also possible for agent
                //   right of X to have card Y.
                break;
        }
    }
    
    private void markAsSeen(Card card)
    {
        int count = 0;

        if (seenCards.containsKey(card.getType()))
            count = seenCards.get(card.getType());

        seenCards.put(card.getType(), count + 1);
    }
    
    public Card.Type mostOftenSeenType()
    {
        Card.Type mostSeenType = null;
        
        for (Card.Type type : seenCards.keySet())
            if (mostSeenType == null || seenCards.get(type) > seenCards.get(mostSeenType))
                mostSeenType = type;
        
        return mostSeenType;
    }
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        
        for (Card.Type type : seenCards.keySet())
            out.append(type + ": " + seenCards.get(type) + "\n");
        
        return out.toString();
    }
}
