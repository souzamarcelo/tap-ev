
package ta;

import structures.PathFlow;
import cost_function.VDFTimeCostFunction;
import java.util.ArrayList;
import java.util.List;
import shortest_path.ConstrainedLabelCorrecting;
import structures.Travel;
import shortest_path.GenericShortestPath;
import structures.Graph;
import structures.PathFlowList;
import utils.EmpiricalAnalyzer;
import utils.ExecutionTime;
import utils.Parameters;
import utils.Result;

/**
 *
 * @author mdesouza
 */
public class SuccessiveAverages {


    private double fi;
    private double[] volumeLastIteration;
    
    private Travel[] travels;
    private PathFlowList[] pathFlows;
    
    private EmpiricalAnalyzer analyzer = new EmpiricalAnalyzer();
    
    public Result run(List<Travel> travels) throws Exception{
        ExecutionTime executionTime = new ExecutionTime();
        
        if(Parameters.analyze) executionTime.initializeCounter();
        
        this.travels = new Travel[travels.size()];
        for(Travel travel : travels){
            this.travels[travel.id] = travel;
        }
        
        doTrafficAssignment();
        
        if(Parameters.analyze){ 
            executionTime.finalizeCounter();
            analyzer.totalTime = executionTime.getExecutionTimeS();
        }
        
        return report(travels);
    }
    
    private Result report(List<Travel> travels){
        Result result = new Result();
        result.taMethod = Parameters.method + "-" + Parameters.singleObjective;
        result.objective = Parameters.singleObjective;
        result.percentElectricVehicles = Parameters.percentElectricVehicles;
        result.travels = travels;
        
        result.pathFlows = new ArrayList<PathFlow>();
        for(PathFlowList pathFlowList: pathFlows){
            for(PathFlow pf : pathFlowList.pathFlows){
                if(Parameters.singleObjective.equalsIgnoreCase("TIME"))
                    pf.vehicleType = "gasoline";
                else if(Parameters.singleObjective.equalsIgnoreCase("ENERGY"))
                    pf.vehicleType = "electric";
                
                result.pathFlows.add(pf);
            }
        }
        
        if(Parameters.analyze) result.analysis = analyzer;
        else result.analysis = null;
        
        return result;
    }
    
    private void doTrafficAssignment(){
        
        this.pathFlows = new PathFlowList[travels.length];
        for(int i = 0; i < travels.length; i++)
            this.pathFlows[i] = new PathFlowList();
        
        volumeLastIteration = new double[Graph.arcs.length];
        int n = 0;
        
        for(int i = 0; i < Graph.aVolumes.length; i++){
            Graph.aVolumes[i] = 0;
            volumeLastIteration[i] = 0;
        }
        
        do {
            
            ExecutionTime executionTime = new ExecutionTime();    
            if(Parameters.analyze) executionTime.initializeCounter();
                
            for(Travel travel: travels){
                if(Parameters.singleObjective.equals("TIME")){
                    GenericShortestPath cgsp = new GenericShortestPath(travel.source, travel.destination);
                    travel.travelRoute = cgsp.getShortestPath();
                }
                
                if(Parameters.singleObjective.equals("ENERGY")){
                    ConstrainedLabelCorrecting clb = new ConstrainedLabelCorrecting(travel.source, travel.destination);
                    travel.travelRoute = clb.getShortestPath();
                }
            }
            
            if(Parameters.analyze){
                executionTime.finalizeCounter();
                analyzer.spTime += executionTime.getExecutionTimeS();
            }
            
            n++;
            fi = (double)1/(double)n;
            
            updateLastIterationVolumes();
            updateArcVolumes();
        } while(!stop(n));
        
        if(Parameters.analyze) analyzer.msaIterations = n;
    }
    
    private void updateLastIterationVolumes(){
        volumeLastIteration = new double[Graph.arcs.length];
        for(int i = 0; i < Graph.arcs.length; i++){
            volumeLastIteration[i] = Graph.aVolumes[i];
        }
    }
    
    private void updateArcVolumes(){
        
        for(PathFlowList pathFlowList: pathFlows){
            for(PathFlow pathFlow: pathFlowList.pathFlows){
                pathFlow.flow = pathFlow.flow * (1 - fi);
            }
        }
        
        for(Travel travel: travels){
            double totalAmount = travel.amount;
            double sentAmount = 0d;
            for(PathFlow pathFlow: pathFlows[travel.id].pathFlows){
                if(pathFlow.source == travel.source){
                    if(pathFlow.destination == travel.destination){
                        sentAmount += pathFlow.flow;
                    }
                }
            }
            
            double toSendAmount = totalAmount - sentAmount;
                        
            boolean added = false;
            for(PathFlow pathFlow: pathFlows[travel.id].pathFlows){
                if(pathFlow.route.getId().equals(travel.travelRoute.getId())){
                    pathFlow.flow += toSendAmount;
                    added = true;
                }
            }
            
            if(!added){
                PathFlow newPathFlow = new PathFlow();
                newPathFlow.source = travel.source;
                newPathFlow.destination = travel.destination;
                newPathFlow.route = travel.travelRoute;
                newPathFlow.flow = toSendAmount;
                pathFlows[travel.id].pathFlows.add(newPathFlow);
            }
        }

        for(int i = 0; i < Graph.aVolumes.length; i++){
            Graph.aVolumes[i] = 0;
        }
        
        for(PathFlowList pathFlowList : pathFlows){
            for(PathFlow pathFlow : pathFlowList.pathFlows){
                List<Integer> rba = pathFlow.route.getRouteByArcs();
                for(int arc : rba){
                    Graph.aVolumes[arc] += pathFlow.flow;
                }
            }
        }
        
        for(int i = 0; i < Graph.aVolumes.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
        }
    }
    
    private boolean stop(int n){
        if(Parameters.stopCriteria.equalsIgnoreCase("convergence")){
            for(int i = 0; i < Graph.arcs.length; i++){
                double difference = Math.abs(Graph.aVolumes[i] - volumeLastIteration[i]);
                double variation = ((100 * difference) / volumeLastIteration[i]);

                if(variation > Parameters.convergenceEpsilon)
                    return false;
            }
        } else {
            if(n < Parameters.numberOfIterations)
                return false;
        }
        
        return true;
    }
    
    private int findArc(int from, int to){
        int limit;
        if((from + 1) < Graph.vertices.length)
            limit = Graph.vertices[from + 1];
        else
            limit = Graph.arcs.length;

        for(int i = Graph.vertices[from]; i < limit; i++){
            if(Graph.arcs[i] == to){
                return i;
            }
        }
        
        return -1;
    }
    
    
    public void finish(){
        
        double[] inputFlow = new double[Graph.vertices.length];
        double[] outputFlow = new double[Graph.vertices.length];
        
        for(int i = 0; i < inputFlow.length; i++){
            inputFlow[i] = 0d;
            outputFlow[i] = 0d;
        }
        
        for(int i = 0; i < Graph.vertices.length; i++){
            for(int j = 0; j < Graph.vertices.length; j++){
                
                if(i != j){
                    int arc = findArc(i, j);
                    if(arc != -1){
                        if(Graph.aVolumes[arc] != 0){
                            inputFlow[j] += Graph.aVolumes[arc];
                            outputFlow[i] += Graph.aVolumes[arc];
                        }
                    }
                }
            }
        }
        
        double totalOutputFlow = 0d;
        for(int i = 0; i < inputFlow.length; i++){
            if(outputFlow[i] > inputFlow[i]){
                totalOutputFlow += (outputFlow[i] - inputFlow[i]);
            }
        }
        
        double totalInputFlow = 0d;
        for(int i = 0; i < inputFlow.length; i++){
            if(outputFlow[i] < inputFlow[i]){
                totalInputFlow += (inputFlow[i] - outputFlow[i]);
            }
        }
        
        for(int i = 0; i < inputFlow.length; i++){
            if(outputFlow[i] != inputFlow[i]){
                //System.out.println(i + " ---> " + Math.abs(outputFlow[i] - inputFlow[i]));
            }
        }
        
        System.out.println("Out : " + totalOutputFlow);
        System.out.println("In  : " + totalInputFlow);
        
        
        
        
        
        double[] inputFlow2 = new double[Graph.vertices.length];
        double[] outputFlow2 = new double[Graph.vertices.length];
        
        for(int i = 0; i < inputFlow2.length; i++){
            inputFlow2[i] = 0d;
            outputFlow2[i] = 0d;
        }
        
        for(Travel travel: travels){
            inputFlow2[travel.destination] += travel.amount;
            outputFlow2[travel.source] += travel.amount;
        }
        
        double totalOutputFlow2 = 0d;
        for(int i = 0; i < inputFlow2.length; i++){
            if(outputFlow2[i] > inputFlow2[i]){
                totalOutputFlow2 += (outputFlow2[i] - inputFlow2[i]);
            }
        }
        
        double totalInputFlow2 = 0d;
        for(int i = 0; i < inputFlow2.length; i++){
            if(outputFlow2[i] < inputFlow2[i]){
                totalInputFlow2 += (inputFlow2[i] - outputFlow2[i]);
            }
        }
        
        System.out.println("Out : " + totalOutputFlow2);
        System.out.println("In  : " + totalInputFlow2);
    }
}