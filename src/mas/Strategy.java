/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jelmer
 */
public class Strategy
{
    private Card.Type collectedType;
    
    private int turnsSinceLastChange;
    
    public Strategy()
    {
        collectedType = null;
        
        turnsSinceLastChange = 0;
    }
    
    public Card decide(Model model, CardTypeCounter counter, Set<Card> hand)
    {
        // If I have no strategy yet (blank mind) don't base yourself on
        // the model.
        if (collectedType == null)
            changeStrategy(hand);
        
        // If my strategy is still smart...
        else if (turnsSinceLastChange > 3)
            changeStrategy(model, counter, hand);
        
        turnsSinceLastChange++;
        
        for (Card card : hand)
            if (card.getType() != collectedType)
                return card;
        
        return hand.iterator().next();
    }
    
    public void changeStrategy(Model model, CardTypeCounter counter, Set<Card> hand)
    {
        // Change my strategy to collecting this type.
        Card.Type type = mostOccurringType(hand);
        
        // If I already have 3 of a type, collect this type
        if (countCardsOfType(type, hand) > 2) // && I know that not someone is pesting me
            collectType(type);
        
        // Else, check wether the card we are collecting is passed around
        else if (counter.count(collectedType) == 0) {
            // If not, choose which one of the other cards to collect
            Card.Type bestType = null;
            
            for (Card card : hand) {
                if (!card.getType().equals(collectedType)) {
                    if (bestType == null || counter.count(card.getType()) > counter.count(bestType))
                        bestType = card.getType();
                }
            }
            
            collectType(bestType);
        }
        
        // An other strategy may be to start teasing one of the other players,
        // as model may know the strategy of one or moreof the other players.
        
        turnsSinceLastChange = 0;
    }
    
    public void changeStrategy(Set<Card> hand)
    {
        // Change my strategy to collecting this type.
        Card.Type type = mostOccurringType(hand);
        collectType(type);
        turnsSinceLastChange = 0;
    }
    
    public Card.Type getCollectedType()
    {
        return collectedType;
    }
    
    public void collectType(Card.Type type)
    {
        collectedType = type;
    }
    
    private int countCardsOfType(Card.Type type, Set<Card> hand)
    {
        int count = 0;
        
        for (Card card : hand)
            if (card.getType() == type)
                ++count;
        
        return count;
    }
    
    private Card.Type mostOccurringType(Set<Card> hand)
    {
        Map<Card.Type, Integer> freq = new EnumMap<Card.Type, Integer>(Card.Type.class);
        
        // Count how many cards I have of each of the types
        for (Card card : hand)
        {
            int count = 1;
            
            if (freq.containsKey(card.getType()))
                count = freq.get(card.getType()) + 1;
            
            freq.put(card.getType(), count);
        }
        
        // Find out which type I have the most of
        Card.Type mostOftenOccurringType = null;
        for (Card.Type type : freq.keySet())
        {
            if (mostOftenOccurringType == null) {
                mostOftenOccurringType = type;
                continue;
            }
            
            if (freq.get(type) > freq.get(mostOftenOccurringType))
                mostOftenOccurringType = type;
        }
        
        return mostOftenOccurringType;
    }
}
