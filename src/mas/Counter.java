/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jelmer
 */
class Counter<T> {
    
    Map<T,Integer> counts;
    
    int total;
    
    public Counter()
    {
        counts = new HashMap<T,Integer>();
        
        total = 0;
    }
    
    public void add(T element)
    {
        int count = 1;
        
        if (counts.containsKey(element))
            count = counts.get(element) + 1;
        
        counts.put(element, count);
        total += 1;
    }
    
    public void reset(T element)
    {
        if (!counts.containsKey(element))
            return;
        
        total -= counts.get(element);
        counts.put(element, 0);
    }
    
    public Map<T,Integer> getCounts()
    {
        return counts;
    }
    
    public int getTotal()
    {
        return total;
    }
}
