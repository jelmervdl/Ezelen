/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jelmer
 */
public class KripkeModel2
{
    public class Belief
    {
        public Map<Card.Type, Boolean> collected;
    }
    
    public class State
    {
        public Map<Agent, Belief> beliefs;
    }
    
    public KripkeModel2(List<Agent> agents)
    {
        List<State> states = new ArrayList<State>();
        
        //belief[4][4][4]
        
    }
}
