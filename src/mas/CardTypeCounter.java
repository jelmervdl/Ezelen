/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

/**
 *
 * @author jelmer
 */
class CardTypeCounter {

    private Card.Type[] seen;
    
    private int pos;
    
    public CardTypeCounter()
    {
        seen = new Card.Type[4];
        
        pos = 0; 
    }
    
    public void add(Card.Type type)
    {
        seen[pos++ % 4] = type;
    }
    
    public int count(Card.Type collecting)
    {
        int count = 0;
        
        for (Card.Type type : seen)
            if (type.equals(collecting))
                count++;
        
        return count;
    }
}
