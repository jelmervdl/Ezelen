/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jelmer & Inge
 */
public class Agent {
    
    private Model model;
    
    private Set<Card> hand;
    
    private Strategy strategy;
    
    private Set<Listener> listeners;
    
    private String name;
    
    private Table table;
    
    private BeliefModel belief;
    
    private BeliefModel beliefOfOthers;
    
    private Card receivedCard;
    
    public static interface Listener
    {
        public void agentChanged(Agent agent);
    }
    
    public Agent(Collection<Card> cards, String name, Strategy strategy, Table table)
    {
        this.hand = new HashSet<Card>(cards);
        
        this.listeners = new HashSet<Listener>();
        
        this.name = name;
        
        this.strategy = strategy;
        
        this.model = new Model();
        
        this.table = table;
        
        this.belief = new BeliefModel(4);
        
        this.beliefOfOthers = new BeliefModel(4);
    }
    
    public void initialize()
    {
        strategy.changeStrategy(hand);
        
        // Every card that I do not have, may be in the hand of each of the other players
        for (Card card : Card.getDeck())
        {
            if (!hand.contains(card))
            {
                for (Agent agent : table.getAgents())
                    if (!agent.equals(this))
                        model.add(new Predicate(Predicate.Operator.M, agent, "HasCard", card));
            }
        }
        
        refreshKnowledge();
        
        model.tick();
    }
    
    public void receiveCard(Card card)
    {
        receivedCard = card;
        
        // Add card to our hand
        hand.add(card);
        assert(hand.size() == 4);
        
        // Tell model that we received a card from our left neighbour
        model.add(new Predicate(Predicate.Operator.K, this, "HasCard", card));
        
        // Tell the model that if the player left of me gives my a card of type X, he is probably not collecting those.
        model.remove(new Predicate(Predicate.Operator.M, null, "Collects", card.getType()));
        
        //Tell the model that i received a card in the last round and I know of it.
        model.add(new Predicate(Predicate.Operator.K, this, "Received", card.getType(), model.getTime()));
        
        belief.receivedCard(card);
        
        // TODO SHOULDNT WE UPDATE OUR KNOWLEDGE RIGHT NOW INSTEAD OF IN THE NEXTROUND CALLBACK?
        refreshKnowledge();
        
        // Update the window.
        notifyListeners();
    }
    
    public Card giveCard()
    {
        // Ask strategy which card to pass on
        Card card = strategy.decide(belief, hand);
        
        // Remove card from my hand
        hand.remove(card);
        
        // Tell model what we now know about our right neighbour
        model.add(new Predicate(Predicate.Operator.K, table.getPlayerRightOf(this), "HasCard", card));
        
        // Update our belief of others their beliefs about what and how
        beliefOfOthers.receivedCard(card);
        
        // Tell all our listeners that we know things ;) ;)
        notifyListeners();
        
        return card;
    }
    
    public void nextRound()
    {
        // Tell the model that we are entering a next round, and that the other
        // agents have also passed their cards around.        
        for (Predicate pred : model.getPredicates("HasCard")) {
            switch (pred.getOperator()) {
                case K:
                    // Only for the other agents, not for myself because I know which cards I have
                    if (!pred.getAgent().equals(this)) {
                        model.remove(pred);
                        model.add(new Predicate(Predicate.Operator.M, pred.getAgent(), "HasCard", pred.getArgument()));
                        model.add(new Predicate(Predicate.Operator.M, table.getPlayerRightOf(pred.getAgent()), "HasCard", pred.getArgument()));
                    }
                    break;
                    
                case M:
                    // Again, not for myself as I know which cards I have
                    if (!table.getPlayerRightOf(pred.getAgent()).equals(this)) {
                        model.add(new Predicate(Predicate.Operator.M, table.getPlayerRightOf(pred.getAgent()), "HasCard", pred.getArgument()));
                    }
                    break;
            }
        }
        
        refreshKnowledge();
        
        model.tick();
        
        notifyListeners();
    }
    
    private void refreshKnowledge()
    {
        // We no longer use this method of storing knowledge. Using BeliefModel
        // is far easier.
        // pruneBeliefs();
        
        // formBeliefs();
    }
    
    private void pruneBeliefs()
    {
        // Prune old beliefs about not collected types.
        for (Predicate pred : model.getPredicates("NotCollected"))
            if (model.getAge(pred) > 2)
                model.remove(pred);
    }
    
    private void formBeliefs()
    {
        // If a card is by everyone now, and I still haven't received it, maybe someone is collecting it
        for (Card card : Card.getDeck())
        {
            // If I have the card myself, don't even bother (this shouldn't be
            // necessary since then other agents cannot maybe have it)
            if (hand.contains(card))
                continue;
            
            boolean everyoneMayHaveCard = true;
            
            for (Agent agent : table.getAgents()) {
                if (!agent.equals(this) && !model.contains(new Predicate(Predicate.Operator.M, agent, "HasCard", card))) {
                    everyoneMayHaveCard = false;
                    break;
                }
            }
            
            if (everyoneMayHaveCard)
                model.add(new Predicate(Predicate.Operator.M, null, "Collects", card.getType()));
            
        }
        
        EnumMap<Card.Type, Integer> counts = new EnumMap<Card.Type, Integer>(Card.Type.class);
        
        for (Predicate pred : model.getPredicates("Received")) {
            int count = 1;
            
            // Only use newer knowledge, skip older predicates
            if ((Integer) pred.getArgument2() < model.getTime() - 3)
                continue;
            
            if (counts.containsKey((Card.Type) pred.getArgument()))
                count = counts.get((Card.Type) pred.getArgument()) + 1;
            
            counts.put((Card.Type) pred.getArgument(), count);
        }
        
        for (Card.Type type : counts.keySet())
            if (counts.get(type) >= 2)
                model.add(new Predicate(Predicate.Operator.B, null, "NotCollected", type));
    }
    
    public boolean hasFourOfAKind()
    {
        Card.Type type = hand.iterator().next().getType();
        
        for (Card card : hand) {
            if (type != card.getType())
                return false;
        }
        
        return true;
    }
    
    /* Methods to get to know the agent better */
    
    public Set<Card> getHand()
    {
        return hand;
    }
    
    public Card getReceivedCard()
    {
        return receivedCard;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Model getModel()
    {
        return model;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    public String getThoughts()
    {
        StringBuilder out = new StringBuilder();
        
        out.append(strategy.toString()).append("\n\n");
        
        out.append("Times not passed around:\n");
        out.append(belief.toString()).append("\n");
        
        out.append("I believe:\n");
        out.append("  Most certainly collected type: ");
        out.append(belief.getMostCertainlyCollectedType());
        out.append("\n");
        
        out.append("  Most likely not collected type: ");
        out.append(belief.getMostLikelyNotCollectedType());
        out.append("\n\n");
        
        
        out.append("Times not passed around according\nto other agents:\n");
        out.append(beliefOfOthers.toString()).append("\n");
        
        out.append("I believe that they believe:\n");
        out.append("  Most certainly collected type: ");
        out.append(beliefOfOthers.getMostCertainlyCollectedType());
        out.append("\n");
        
        out.append("  Most likely not collected type: ");
        out.append(beliefOfOthers.getMostLikelyNotCollectedType());
        out.append("\n\n");
        
        out.append("Knowledge:\n");
        out.append(model.toString());
        
        return out.toString();
    }
    
    /* Methods to shout and stuff */
    
    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }
    
    private void notifyListeners()
    {
        for (Listener listener : listeners)
            listener.agentChanged(this);
    }
}
