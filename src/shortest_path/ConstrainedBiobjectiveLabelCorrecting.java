
package shortest_path;

import structures.LabelList;
import cost_function.EnergyModel;
import cost_function.VDFTimeCostFunction;
import java.util.ArrayList;
import java.util.List;
import structures.Label;
import structures.Route;
import structures.Graph;
import utils.EmpiricalAnalyzer;
import utils.Parameters;

/**
 *
 * @author mdesouza
 */
public class ConstrainedBiobjectiveLabelCorrecting {
    
    private int labelCounter;
    private int source;
    private int destination;
    private List<Integer> modNodes = new ArrayList<Integer>();
    private int[] predecessor;
    private LabelList[] labelsOfVertice;
    
    public double visitedVertices = 0d;
    public double meanLabels = 0d;
    public double nonDominatedPaths = 0d;
    
    public ConstrainedBiobjectiveLabelCorrecting(int source, int destination){
        this.source = source;
        this.destination = destination;
    }
    
    public List<Route> getShortestPaths(){
        calculateShortestPathTree();
        List<Route> routes = new ArrayList<Route>();
        
        
        for(Label destinationLabel: labelsOfVertice[destination].labels){
            int node = destination;
            Route route = new Route();
            route.routeByNodes.add(0, node);
            int predecessorNode = destinationLabel.predecessorNode;
            int idLabel = destinationLabel.idPreviousLabel;
            
            boolean found = false;
            while(predecessorNode != -1){
                found = false;
                for(Label label: labelsOfVertice[predecessorNode].labels){
                    if(label.id == idLabel){
                        node = predecessorNode;
                        predecessorNode = label.predecessorNode;
                        route.routeByNodes.add(0, node);
                        idLabel = label.idPreviousLabel;
                        found = true;
                        break;
                    }
                }
                if(!found)
                    break;
            }
            if(found)
                routes.add(route);
        }
        
        if(Parameters.analyze){
            //this.nonDominatedPaths += labelsOfVertice[destination].labels.size();
            this.nonDominatedPaths += routes.size();
            double meanLabels = 0d;
            for(int vertex = 0; vertex < Graph.vertices.length; vertex++){
                meanLabels += labelsOfVertice[vertex].labels.size();
            }
            meanLabels = (meanLabels / Graph.vertices.length);
            this.meanLabels = meanLabels;
        }
        
        return routes;
    }
    
    
    private void calculateShortestPathTree(){
        reset();
        modNodes.add(source);
        
        Label firstLabel = new Label();
        firstLabel.time = 0d;
        firstLabel.energy = Parameters.remainingStorageCapacity;
        firstLabel.velocity = 0d;
        firstLabel.predecessorNode = -1;
        labelsOfVertice[source].labels.add(firstLabel);
        firstLabel.id = labelCounter;
        labelCounter++;
        firstLabel.idPreviousLabel = -1;
        
        while(!modNodes.isEmpty()){
            int n = selectNode();
            
            if(Parameters.analyze) this.visitedVertices += 1;
            
            int limit;
            if((n + 1) < Graph.vertices.length)
                limit = Graph.vertices[n + 1];
            else
                limit = Graph.arcs.length;
            
            for(int i = Graph.vertices[n]; i < limit; i++){
                int sucessor = Graph.arcs[i];
                predecessor[sucessor] = n;
                
                List<Label> newLabels = new ArrayList<Label>();
                for(Label label: labelsOfVertice[n].labels){

                    Label newLabel = new Label();

                    newLabel.predecessorNode = n;                    
                    predecessor[n] = label.predecessorNode;

                    newLabel.time = (label.time + costFunction(n, sucessor, Parameters.firstObjective));
                    double energyCost = (label.energy + costFunction(n, sucessor, Parameters.secondObjective));
                    if(energyCost < 0) energyCost = 0;
                    newLabel.energy = energyCost;
                    newLabel.velocity = Graph.aCalculatedVelocities[findArc(n, sucessor)];
                    newLabel.id = labelCounter;
                    labelCounter++;
                    newLabel.idPreviousLabel = label.id;
                    
                    newLabels.add(newLabel);
                }
                
                for(Label newLabel : newLabels){
                    if(newLabel.energy <= Parameters.maxCapacity){
                        if(mergeLabels(newLabel, sucessor)){
                            if(sucessor != destination){
                                if(!modNodes.contains(sucessor))
                                    
                                    if(Parameters.network.equalsIgnoreCase("anaheim")){
                                        if(sucessor > 37)
                                            modNodes.add(sucessor);
                                    } else {
                                        modNodes.add(sucessor);
                                    }
                                    
                                    //modNodes.add(sucessor);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean mergeLabels(Label newLabel, int sucessor){
        
        if(sucessor != destination){
            boolean addNewLabel = true;

            for(Label label: labelsOfVertice[sucessor].labels){
                
                if(label.time < newLabel.time){
                    if(label.energy <= newLabel.energy){
                        if(label.velocity >= newLabel.velocity){
                            addNewLabel = false;
                            break;
                        }
                    }
                }
                
                if(label.energy < newLabel.energy){
                    if(label.time <= newLabel.time){
                        if(label.velocity >= newLabel.velocity){
                            addNewLabel = false;
                            break;
                        }
                    }
                }

                if(label.time == newLabel.time){
                    if(label.energy < newLabel.energy){
                        if(label.velocity >= newLabel.velocity){
                            addNewLabel = false;
                            break;
                        }
                    }

                    if(label.energy == newLabel.energy){
                        if(label.velocity > newLabel.velocity){
                            addNewLabel = false;
                            break;
                        }
                    }

                    if(label.energy == newLabel.energy){
                        if(label.velocity == newLabel.velocity){
                            if(label.predecessorNode == newLabel.predecessorNode){
                                addNewLabel = false;
                                break;
                            }
                        }
                    }
                }
            }

            if(addNewLabel){
                
                boolean changed = true;
                while(changed){
                    changed = false;
                    for(int i = 0; i < labelsOfVertice[sucessor].labels.size(); i++){
                        Label label = labelsOfVertice[sucessor].labels.get(i);
                        
                        if(newLabel.time < label.time){
                            if(newLabel.energy <= label.energy){
                                if(newLabel.velocity >= label.velocity){
                                    changed = true;
                                    labelsOfVertice[sucessor].labels.remove(i);
                                    break;
                                }
                            }
                        }
                        
                        if(newLabel.energy < label.energy){
                            if(newLabel.time <= label.time){
                                if(newLabel.velocity >= label.velocity){
                                    changed = true;
                                    labelsOfVertice[sucessor].labels.remove(i);
                                    break;
                                }
                            }
                        }
                        
                        if(newLabel.time == label.time){
                            if(newLabel.energy < label.energy){
                                if(newLabel.velocity >= label.velocity){
                                    changed = true;
                                    labelsOfVertice[sucessor].labels.remove(i);
                                    break;
                                }
                            }
                            
                            if(newLabel.energy == label.energy){
                                if(newLabel.velocity > label.velocity){
                                    changed = true;
                                    labelsOfVertice[sucessor].labels.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
                labelsOfVertice[sucessor].labels.add(newLabel);
            }
            
            return addNewLabel;
            
            
        } else {
            boolean addNewLabel = true;

            for(Label label: labelsOfVertice[sucessor].labels){
                
                if(label.time < newLabel.time){
                    if(label.energy <= newLabel.energy){                        
                        addNewLabel = false;
                        break;
                    }
                }
                
                if(label.energy < newLabel.energy){
                    if(label.time <= newLabel.time){                        
                        addNewLabel = false;
                        break;
                    }
                }

                if(label.time == newLabel.time){
                    if(label.energy < newLabel.energy){
                        addNewLabel = false;
                        break;
                    }

                    if(label.energy == newLabel.energy){
                        if(label.predecessorNode == newLabel.predecessorNode){
                            addNewLabel = false;
                            break;
                        }
                    }
                }
            }

            if(addNewLabel){
                
                boolean changed = true;
                while(changed){
                    changed = false;
                    for(int i = 0; i < labelsOfVertice[sucessor].labels.size(); i++){
                        Label label = labelsOfVertice[sucessor].labels.get(i);
                        
                        if(newLabel.time < label.time){
                            if(newLabel.energy <= label.energy){
                                changed= true;
                                labelsOfVertice[sucessor].labels.remove(i);
                                break;
                            }
                        }
                        
                        if(newLabel.energy < label.energy){
                            if(newLabel.time <= label.time){
                                changed= true;
                                labelsOfVertice[sucessor].labels.remove(i);
                                break;
                            }
                        }
                        
                        if(newLabel.time == label.time){
                            if(newLabel.energy < label.energy){
                                changed= true;
                                labelsOfVertice[sucessor].labels.remove(i);
                                break;
                            }
                        }
                    }
                }
                labelsOfVertice[sucessor].labels.add(newLabel);
            }
            
            return addNewLabel;
        }
    }
    
    private double costFunction(int from, int to, String objective){
        
        switch(objective){
            case "TIME": return timeCostFunction(from, to);
            case "ENERGY":return energyCostFunction(from, to);
            case "FFTT": return ffttCostFunction(from, to);
        }
        
        return 0d;
    }
    
    private int selectNode(){
        int toReturn = -1;
        toReturn = modNodes.get(0);
        modNodes.remove(0);
        return toReturn;
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
    
    private void reset(){
        this.predecessor = new int[Graph.vertices.length];
        this.labelsOfVertice = new LabelList[Graph.vertices.length];
        
        this.labelCounter = 1;
        
        for(int i = 0; i < Graph.vertices.length; i++)
            labelsOfVertice[i] = new LabelList();
        
        for(int i = 0; i < this.predecessor.length; i++)
            this.predecessor[i] = -1;
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
        modNodes = new ArrayList<Integer>();
    }
}
