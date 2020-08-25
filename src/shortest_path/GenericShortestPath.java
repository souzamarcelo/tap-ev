/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shortest_path;

import cost_function.EnergyModel;
import cost_function.VDFTimeCostFunction;
import java.util.ArrayList;
import java.util.List;
import structures.Route;
import structures.Graph;
import utils.Parameters;

/**
 *
 * @author mdesouza
 */
public class GenericShortestPath {
    
    int source;
    int destination;
    List<Integer> queue;
    private double[] costToNode;
    private int[] predecessor;
    
    public GenericShortestPath(int source, int destination){
        this.source = source;
        this.destination = destination;
        this.costToNode = new double[Graph.vertices.length];
        this.predecessor = new int[Graph.vertices.length];
    }
    
    public Route getShortestPath(){
        calculateShortestPathTree();
        
        Route r = new Route();
        int n = destination;
        
        if(costToNode[n] == Double.MAX_VALUE)
            System.out.println("No path found");
        
        while (n != -1){
            r.addNode(n);
            n = predecessor[n];
        }
        
        return r;
    }
    
    private void calculateShortestPathTree(){
        reset();
        costToNode[source] = 0;
        predecessor[source] = -1;
        queue.add(source);
                
        while(!queue.isEmpty()){
            int node = -1;
            
            int u = chooseNodeFromQueue("Dijkstra");
            
            int limit;
            if((u + 1) < Graph.vertices.length)
                limit = Graph.vertices[u + 1];
            else
                limit = Graph.arcs.length;
            
            for(int i = Graph.vertices[u]; i < limit; i++){
                int v = Graph.arcs[i];
                
                double d = costToNode[u] + costFunction(u, v);
                if((d < costToNode[v])){
                    costToNode[v] = d;
                    predecessor[v] = u;
                    if(v != destination){
                        if(Parameters.network.equalsIgnoreCase("anaheim")){
                            if(v > 37)
                                addToQueue(v);
                        } else {
                            addToQueue(v);
                        }
                    }
                }
            }
        }
    }
    
    private void addToQueue(int n){
        for(int in: queue){
            if(in == n)
                return;
        }
        queue.add(n);
    }
    
    private double costFunction(int from, int to){
        
        switch (Parameters.singleObjective){
            case "TIME": return timeCostFunction(from, to);
            case "ENERGY": return energyCostFunction(from, to);
            case "FFTT": return ffttCostFunction(from, to);
        }
        
        return 0d;
    }
    
    private double ffttCostFunction(int from, int to){
        
        for(int i = Graph.vertices[from]; i < Graph.arcs.length; i++){
            if(Graph.arcs[i] == to)
                return Graph.aFreeFlowTravelTimes[i];
        }
        
        return -1;
    }
    
    private double timeCostFunction(int from, int to){
        
        for(int i = Graph.vertices[from]; i < Graph.arcs.length; i++){
            if(Graph.arcs[i] == to)
                return VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
        }
        return -1;
    }
    
    private double energyCostFunction(int from, int to){
        
        double elevationFrom;
        double elevationTo;
        double previousVelocity;
        double velocity;
        double distance;
        
        boolean beginning = false;
        boolean end = false;
        boolean oneArc = false;
        double finalCost = 0d;
        
        if(from == source) beginning = true;
        if(to == destination) end = true;
        if(beginning && end) oneArc = true;
        
        if(oneArc){
            previousVelocity = 0d;
            elevationFrom = Graph.vElevations[from];
            elevationTo = Graph.vElevations[to];
            velocity = Graph.aCalculatedVelocities[findArc(from, to)];
            distance = Graph.aDistances[findArc(from, to)];
            
            
            EnergyModel aux = new EnergyModel();
            aux.distance = 0d;
            aux.elevationFrom = 1d;
            aux.elevationTo = 1d;
            aux.actualVelocity = 0d;
            aux.previousVelocity = velocity;
            
            finalCost = aux.consumption()[0];
            
        } else if(beginning){
            previousVelocity = 0d;
            elevationFrom = Graph.vElevations[from];
            elevationTo = Graph.vElevations[to];
            velocity = Graph.aCalculatedVelocities[findArc(from, to)];
            distance = Graph.aDistances[findArc(from, to)];
            
        } else if(end){
            previousVelocity = Graph.aCalculatedVelocities[findArc(predecessor[from], from)];
            elevationFrom = Graph.vElevations[from];
            elevationTo = Graph.vElevations[to];
            velocity = Graph.aCalculatedVelocities[findArc(from, to)];
            distance = Graph.aDistances[findArc(from, to)];
            
            EnergyModel aux = new EnergyModel();
            aux.distance = 0d;
            aux.elevationFrom = 1d;
            aux.elevationTo = 1d;
            aux.actualVelocity = 0d;
            aux.previousVelocity = velocity;
                       
            finalCost = aux.consumption()[0];
                        
        } else {
            previousVelocity = Graph.aCalculatedVelocities[findArc(predecessor[from], from)];
            elevationFrom = Graph.vElevations[from];
            elevationTo = Graph.vElevations[to];
            velocity = Graph.aCalculatedVelocities[findArc(from, to)];
            distance = Graph.aDistances[findArc(from, to)];
            
        }
        
        EnergyModel em = new EnergyModel();
        em.distance = distance;
        em.elevationFrom = elevationFrom;
        em.elevationTo = elevationTo;
        em.actualVelocity = velocity;
        em.previousVelocity = previousVelocity;
        
        return em.consumption()[0] + finalCost;
    }
    
    private int findArc(int from, int to){
        for(int i = Graph.vertices[from]; i < Graph.arcs.length; i++){
            if(Graph.arcs[i] == to){
                return i;
            }
        }
        return -1;
    }
    
    private int chooseNodeFromQueue(String strategy){
        
        if(strategy.equals("Bellman-Ford")){
            int toReturn = queue.get(0);
            queue.remove(0);
            return toReturn;
        }
        
        if(strategy.equals("LIFO")){
            int toReturn = queue.get(queue.size() - 1);
            queue.remove(queue.size() - 1);
            return toReturn;
        }
        
        if(strategy.equals("Dijkstra")){
            double leastCost = Double.MAX_VALUE;
            int resultNode = -1;
            int indexToRemove = -1;
            
            for(int i = 0; i < queue.size(); i++){
                int n = queue.get(i);
                if(predecessor[n] != -1){
                    if(costToNode[predecessor[n]] < leastCost){
                        double vertexCost = costToNode[predecessor[n]] + costFunction(predecessor[n], n);
                        if(leastCost > vertexCost){
                            leastCost = vertexCost;
                            resultNode = n;
                            indexToRemove = i;
                        }
                    }
                }
            }
            if(resultNode == -1){
                resultNode = queue.get(0);
                indexToRemove = 0;
            }
            
            queue.remove(indexToRemove);
            return resultNode;
        }
        
        return -1;
    }
    
    private void reset(){
        for(int i = 0; i < this.costToNode.length; i++)
            this.costToNode[i] = Double.MAX_VALUE;
        for(int i = 0; i < this.predecessor.length; i++)
            this.predecessor[i] = -1;
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
        
        queue = new ArrayList<Integer>();
    }
    
}