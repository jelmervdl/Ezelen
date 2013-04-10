/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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
    
    public Card decide(BeliefModel model, Set<Card> hand)
    {
        // If I have no strategy yet (blank mind) don't base yourself on
        // the model.
        if (collectedType == null)
            changeStrategy(hand);
        else
            changeStrategy(model, hand);
        
        Card chosenCard = null;
        
        // Choose the card that I'm not collecting and that is most likely also
        // not collected by others.
        for (Card card : hand)
            if (card.getType() != collectedType && (chosenCard == null
                        || model.likelyhoodIsCollected(card.getType()) < model.likelyhoodIsCollected(chosenCard.getType())))
                chosenCard = card;
                
        
        assert chosenCard != null : "I did not choose a card";
        
        // Keep track of for how long we have been using this strategy.
        turnsSinceLastChange++;
        
        return chosenCard;
    }
    
    public void changeStrategy(BeliefModel model, Set<Card> hand)
    {
        // Change my strategy to collecting this type.
        Card.Type type = mostOccurringType(hand);
        
        // If I already have 3 of a type, collect this type
        if (countCardsOfType(type, hand) > 2 && turnsSinceLastChange < 8) // && I know that not someone is pesting me
            collectType(type);
        
        // Else, see which type is not collected by anyone, and start collecting
        // that type.
        else if (turnsSinceLastChange >= 2)
            collectType(model.getMostLikelyNotCollectedType());
        
        // An other strategy may be to start teasing one of the other players,
        // as model may know the strategy of one or moreof the other players.
        // This is already done by choosing a card that is most likely not
        // collected.
    }
    
    public void changeStrategy(Set<Card> hand)
    {
        // Change my strategy to collecting this type.
        Card.Type type = mostOccurringType(hand);
        collectType(type);
    }
    
    public Card.Type getCollectedType()
    {
        return collectedType;
    }
    
    public void collectType(Card.Type type)
    {
        assert type != null : "I'm trying to collect type null";
        
        if (type != collectedType)
            turnsSinceLastChange = 0;
        
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
    
    @Override
    public String toString()
    {
        return "I am collecting " + collectedType
                + "\n(and have been for " + turnsSinceLastChange + " turns)";
    }
}
