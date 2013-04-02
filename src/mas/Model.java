/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jelmer
 */
public class Model
{
    private Set<Predicate> predicates;
    
    public Model()
    {
        predicates = new HashSet<Predicate>();
    }
    
    public void add(Predicate pred)
    {
        // To test: remove everything that is inconsistent with pred.
        // Warning: this only tests on predicates with the same name.
        for (Predicate known : getPredicates(pred.getName())) {
            if (!pred.isConsistentWith(known))
                predicates.remove(known);
        }
                
        
        predicates.add(pred);
    }
    
    public void remove(Predicate pred)
    {
        predicates.remove(pred);
    }
    
    public boolean contains(Predicate predicate)
    {
        return predicates.contains(predicate);
    }
    
    public Set<Predicate> getPredicates(String name)
    {
        Set<Predicate> filtered = new HashSet<Predicate>();
        
        for (Predicate pred : predicates)
            if (pred.getName().equals(name))
                filtered.add(pred);
        
        return filtered;
    }
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        out.append("Knowledge:\n");
        
        Predicate[] sortedPredicates = predicates.toArray(new Predicate[0]);
        Arrays.sort(sortedPredicates, new Comparator<Predicate>() {
            @Override
            public int compare(Predicate a, Predicate b) {
                if (a.getOperator() != b.getOperator())
                    return a.getOperator() == Predicate.Operator.K ? -1 : 1;
                
                if (!a.getName().equals(b.getName()))
                    return a.getName().compareTo(b.getName());
                
                if (!a.getAgent().equals(b.getAgent()))
                    return a.getAgent().getName().compareTo(b.getAgent().getName());
                
                return a.getArgument().toString().compareTo(b.getArgument().toString());
            }
        });
        
        for (Predicate predicate : sortedPredicates)
            // don't print the boring knowledge, because that.. is boring.
            if (!(predicate.getOperator() == Predicate.Operator.M && predicate.getName().equals("HasCard")))
                out.append("  ").append(predicate).append("\n");
        
        return out.toString();
    }
}
