/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package structures;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcelo
 */
public class Route {
    public List<Integer> routeByNodes;
    public double timeCost = -1;
    public double energyCost = -1;
    public double energySavings;
    public double additionalTime;
    
    public Route(){
        routeByNodes = new ArrayList<Integer>();
    }
    
    public String getId(){
        String result = "";
        for(int node: routeByNodes){
            result += node + "-";
        }
        return result;
        //return result.substring(0, result.length()-1);
    }
    
    public void addNode(Integer n){
        routeByNodes.add(0, n);
    }
    
    public void addNodeAtFinal(Integer n){
        routeByNodes.add(n);
    }
    
    public List<Integer> getRouteByArcs(){
        List<Integer> routeByArcs = new ArrayList<Integer>();
        
        for(int i = 0; i < routeByNodes.size() - 1; i++){
            int from = routeByNodes.get(i);
            int to = routeByNodes.get(i+1);
            boolean achou = false;
            
            int limit;
            if((from + 1) < Graph.vertices.length)
                limit = Graph.vertices[from + 1];
            else
                limit = Graph.arcs.length;
            
            for(int j = Graph.vertices[from]; j < limit; j++){
                if(Graph.arcs[j] == to){
                    routeByArcs.add(j);
                    achou = true;
                    break;
                }
            }
        }
        
        return routeByArcs;
    }
}
