/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author jelmer
 */
public class BeliefModel
{
    private Map<Card.Type, Integer> collected;
    
    private Card.Type[] history;
    
    private int historySize;
    
    private int historyPos;
    
    public BeliefModel(int historySize)
    {
        collected = new EnumMap<Card.Type, Integer>(Card.Type.class);
        
        this.historySize = historySize;
        
        this.historyPos = 0;
        
        history = new Card.Type[historySize];
        
        for (Card.Type type : Card.Type.values())
            this.collected.put(type, 0);
    }
    
    public void receivedCard(Card card)
    {
        for (Card.Type type : Card.Type.values())
            this.collected.put(type, card.getType() == type
                ? 0
                : this.collected.get(type) + 1);
        
        history[historyPos++ % historySize] = card.getType();
    }
    
    public Card.Type getMostCertainlyCollectedType()
    {
        Card.Type found = null;
        
        // Look at all the history, which type of card hasn't been passed on for
        // the longest amout of time.
        for (Card.Type type : Card.Type.values())
            if (found == null || collected.get(type) > collected.get(found))
                found = type;
        
        return found;
    }
    
    public Card.Type getMostLikelyNotCollectedType()
    {
        Card.Type highestCountType = null;
        int highestCount = -1;
     
        // Find the card which has been passed on the most often the last
        // historySize times
        for (Card.Type type : Card.Type.values())
        {
            int count = 0;
            
            for (int i = 0; i < Math.min(historySize, historyPos); ++i)
                if (history[i] == type)
                    count++;
            
            if (count > highestCount)
            {
                highestCountType = type;
                highestCount = count;
            }
        }
        
        return highestCountType;
    }
    
    public Card.Type getLastReceivedType()
    {
        if (historyPos == 0)
            return null;
        
        return history[(historyPos - 1) % historySize];
    }
    
    public int likelyhoodIsCollected(Card.Type type)
    {
        return collected.get(type);
    }
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        
        for (Card.Type type : Card.Type.values()) {
            out.append("  ").append(type);
            out.append("\t").append(collected.get(type));
            out.append("\n");
        }
        
        return out.toString();
    }
}
