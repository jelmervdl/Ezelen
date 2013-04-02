/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

/**
 *
 * @author jelmer
 */
public class Predicate
{
    private Operator operator;
    
    private Agent agent;
    
    private String name;
    
    private Object argument;
    
    public enum Operator {
        K, M
    }
    
    public Predicate(Operator operator, Agent agent, String name, Object argument)
    {
        this.operator = operator;
        this.agent = agent;
        this.name = name;
        this.argument = argument;
    }
    
    public Operator getOperator()
    {
        return operator;
    }
    
    public Agent getAgent()
    {
        return agent;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Object getArgument()
    {
        return argument;
    }
    
    public boolean isConsistentWith(Predicate pred)
    {
        // HasCard(X) can only occur for one agent in the graph
        if ((getOperator() == Operator.K || pred.getOperator() == Operator.K)
            && pred.getName().equals("HasCard")
            && pred.getName().equals(getName())
            && pred.getArgument().equals(getArgument())
            && !pred.getAgent().equals(getAgent()))
            return false;
        
        // You can only know one strategy for certain
        if ((getOperator() == Operator.K || pred.getOperator() == Operator.K)
            && pred.getName().equals("Collects")
            && pred.getName().equals(getName())
            && pred.getAgent().equals(getAgent()))
            return false;
        
        // Otherwise, it is probably OK since I am not allowed by Inge to
        // implement a complete inference engine. Yet.
        return true;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Predicate))
            return false;
        
        Predicate other = (Predicate) obj;
        
        return other.getOperator().equals(getOperator())
            && other.getAgent().equals(getAgent())
            && other.getName().equals(getName())
            && other.getArgument().equals(getArgument());
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 29 * hash + (this.operator != null ? this.operator.hashCode() : 0);
        hash = 29 * hash + (this.agent != null ? this.agent.hashCode() : 0);
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.argument != null ? this.argument.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        
        out.append(getOperator());
        out.append("(").append(getAgent()).append(")");
        out.append(getName());
        out.append("(").append(getArgument()).append(")");
        
        return out.toString();
    }
}
