
package ta;

import cost_function.VDFTimeCostFunction;
import java.util.ArrayList;
import java.util.List;
import shortest_path.ConstrainedBiobjectiveLabelCorrecting;
import structures.Route;
import structures.Travel;
import shortest_path.GenericShortestPath;
import structures.PathFlow;
import utils.Evaluation;
import structures.Graph;
import structures.PathFlowList;
import structures.TravelList;
import utils.EmpiricalAnalyzer;
import utils.ExecutionTime;
import utils.Parameters;
import utils.Result;

/**
 *
 * @author mdesouza
 */
public class BiobjectiveSuccessiveAveragesGreedySlope {

    private double fi;
    private double[] volumeLastIteration;
    
    private Travel[] travels;
    private TravelList[] newTravels;
    private PathFlowList[] pathFlows;
    
    private int n;
    
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
        result.taMethod = Parameters.method;
        result.objective = Parameters.firstObjective + " and " + Parameters.secondObjective;
        result.travels = travels;
        result.percentElectricVehicles = Parameters.percentElectricVehicles;
        
        result.pathFlows = new ArrayList<PathFlow>();
        for(PathFlowList pathFlowList: pathFlows){
            for(PathFlow pf : pathFlowList.pathFlows)
                result.pathFlows.add(pf);
        }
        
        if(Parameters.analyze) result.analysis = analyzer;
        else result.analysis = null;
        
        return result;
    }
    
    private void doTrafficAssignment(){
        
        this.pathFlows = new PathFlowList[travels.length];
        for(int i = 0; i < travels.length; i++)
            this.pathFlows[i] = new PathFlowList();
        
        this.newTravels = new TravelList[travels.length];
        for(int i = 0; i < travels.length; i++)
            this.newTravels[i] = new TravelList();
        
        volumeLastIteration = new double[Graph.arcs.length];
        n = 0;
        
        for(int i = 0; i < Graph.aVolumes.length; i++){
            Graph.aVolumes[i] = 0;
            volumeLastIteration[i] = 0;
        }
        
        do {
            
            ExecutionTime executionTime = new ExecutionTime();    
            if(Parameters.analyze) executionTime.initializeCounter();
            
            for(Travel travel: travels){
                ConstrainedBiobjectiveLabelCorrecting biObjective = new ConstrainedBiobjectiveLabelCorrecting(travel.source, travel.destination);
                travel.travelRoutes = biObjective.getShortestPaths();
                
                GenericShortestPath monoObjective = new GenericShortestPath(travel.source, travel.destination);
                travel.travelRoute = monoObjective.getShortestPath();
                
                analyzer.blcRuns += 1d;
                analyzer.blcMeanLabels += biObjective.meanLabels;
                analyzer.blcNonDominatedPaths += biObjective.nonDominatedPaths;
                analyzer.blcVisitedVertices += biObjective.visitedVertices;
            }
            
            if(Parameters.analyze){
                executionTime.finalizeCounter();
                analyzer.spTime += executionTime.getExecutionTimeS();
            }
            
            doAONAssignment();
            
            n++;
            fi = (double)1/(double)n;
            
            updateLastIterationVolumes();
            updateArcVolumes();
            
        } while(!stop(n));
        
        if(Parameters.analyze) analyzer.msaIterations = n;
    }
    
    private void doAONAssignment(){
        for(Travel travel: travels){
            travel.travelRoute.timeCost = Evaluation.evaluateTimeOfRoute(travel.travelRoute);
            travel.travelRoute.energyCost = Evaluation.evaluateEnergyOfRoute(travel.travelRoute);

            double lessTime = Double.MAX_VALUE;
            for(Route route: travel.travelRoutes){
                route.timeCost = Evaluation.evaluateTimeOfRoute(route);
                route.energyCost = Evaluation.evaluateEnergyOfRoute(route);
                route.energySavings = travel.travelRoute.energyCost - route.energyCost;
                route.additionalTime = route.timeCost - travel.travelRoute.timeCost;
                
                if(route.timeCost < lessTime){
                    lessTime = route.timeCost;
                }
            }
            travel.lessTime = lessTime;
        }
        
        this.newTravels = new TravelList[travels.length];
        for(int i = 0; i < travels.length; i++)
            this.newTravels[i] = new TravelList();
        
        for(Travel travel: travels){
            double amountGasoline = (((100 - Parameters.percentElectricVehicles) * travel.amount) / 100d);
            if(amountGasoline > 0){
                Travel gasolineTravel = new Travel();
                gasolineTravel.source = travel.source;
                gasolineTravel.destination = travel.destination;
                gasolineTravel.amount = amountGasoline;
                gasolineTravel.travelRoute = travel.travelRoute;
                gasolineTravel.vehicleType = "gasoline";
                gasolineTravel.id = travel.id;
                
                newTravels[gasolineTravel.id].travels.add(gasolineTravel);
            }
            
            double amountElectric = travel.amount - amountGasoline;
            if(amountElectric > 0){
                double assignedElectric = 0d;
                for(int i = 0; i < Parameters.admissibleExtraTimeValues.length; i++){
                    Route chosenRoute = null;
                    double lessAgentSlope = Double.MAX_VALUE;

                    for(Route route: travel.travelRoutes){
                        if(route.timeCost <= (travel.lessTime + (travel.lessTime * (Parameters.admissibleExtraTimeValues[i] / 100d)))){
                            double slope = acceptableSlope(route.additionalTime, route.energySavings, travel.travelRoute.timeCost, travel.travelRoute.energyCost);
                            if(slope > 0){
                                if(chosenRoute == null){
                                    chosenRoute = route;
                                    lessAgentSlope = slope;
                                } else {
                                    if(slope < lessAgentSlope){
                                        chosenRoute = route;
                                        lessAgentSlope = slope;
                                    } else {
                                        if(slope == lessAgentSlope){
                                            if(route.energyCost < chosenRoute.energyCost){
                                                chosenRoute = route;
                                                lessAgentSlope = slope;
                                            }
                                        }
                                    }
                                }
                            }
                            
                            if(chosenRoute == null)
                                chosenRoute = travel.travelRoute;
                        }
                    }
                    
                    Travel newElectricTravel = new Travel();
                    newElectricTravel.source = travel.source;
                    newElectricTravel.destination = travel.destination;
                    newElectricTravel.travelRoute = chosenRoute;
                    newElectricTravel.vehicleType = "electric";
                    newElectricTravel.id = travel.id;
                    
                    if(i == (Parameters.admissibleExtraTimeValues.length - 1))
                        newElectricTravel.amount = amountElectric - assignedElectric;
                    else
                        newElectricTravel.amount = amountElectric * (Parameters.admissibleExtraTimeDistribution[i] / 100d);
                    
                    this.newTravels[newElectricTravel.id].travels.add(newElectricTravel);
                    assignedElectric += newElectricTravel.amount;
                }
            }
        }
    }
    
    private void updateLastIterationVolumes(){
        volumeLastIteration = new double[Graph.arcs.length];
        for(int i = 0; i < Graph.arcs.length; i++){
            volumeLastIteration[i] = Graph.aVolumes[i];
        }
    }
    
    private void updateArcVolumes(){
                
        for(Travel travel: travels){
            double totalAmount = travel.amount;
            double sentAmount = 0d;
            for(PathFlow pathFlow: pathFlows[travel.id].pathFlows){
                pathFlow.flow = pathFlow.flow * (1 - fi);
                sentAmount += pathFlow.flow;
            }
            
            double toSendAmount = totalAmount - sentAmount;
            
            double totalNewAmount = 0d;
            for(Travel newTravel: newTravels[travel.id].travels){
                newTravel.amount = newTravel.amount * fi;
                totalNewAmount += newTravel.amount;
            }
            
            if(totalNewAmount != toSendAmount){
                for(Travel newTravel: newTravels[travel.id].travels){
                    newTravel.amount = newTravel.amount + (toSendAmount - totalNewAmount);
                    break;
                }
            }
        }
        
        for(TravelList travelList: this.newTravels){
            for(Travel travel: travelList.travels){
                boolean added = false;
                for(PathFlow pathFlow: pathFlows[travel.id].pathFlows){
                    if(pathFlow.vehicleType.equals(travel.vehicleType)){
                        if(pathFlow.route.getId().equals(travel.travelRoute.getId())){
                            pathFlow.flow += travel.amount;
                            added = true;
                            break;
                        }
                    }
                }

                if(!added){
                    PathFlow newPathFlow = new PathFlow();
                    newPathFlow.source = travel.source;
                    newPathFlow.destination = travel.destination;
                    newPathFlow.route = travel.travelRoute;
                    newPathFlow.flow = travel.amount;
                    newPathFlow.vehicleType = travel.vehicleType;
                    pathFlows[travel.id].pathFlows.add(newPathFlow);
                }
            }
        }
                
        for(int i = 0; i < Graph.aVolumes.length; i++){
            Graph.aVolumes[i] = 0;
        }
        
        for(PathFlowList pathFlowList : pathFlows) {
            for(PathFlow pathFlow : pathFlowList.pathFlows){
                List<Integer> lba = pathFlow.route.getRouteByArcs();
                for(int arc : lba){
                    Graph.aVolumes[arc] += pathFlow.flow;
                }
            }
        }
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
    }
    
    private double acceptableSlope(double routeAdditionalTime, double routeEnergySavings, double minIterationTime, double iterationEnergy){
        
        Parameters.minSlopeAdditionalTime = (minIterationTime * (Parameters.minSlopeAdditionalTimePercentage / 100d));
        Parameters.minSlopeEnergySavings = (iterationEnergy * (Parameters.minSlopeEnergySavingsPercentage / 100d));
        
        if(routeEnergySavings < 0) routeEnergySavings = 0;
        if(routeAdditionalTime < 0) routeAdditionalTime = 0;
        
        if(routeEnergySavings >= 0 && routeAdditionalTime == 0)
            return 0d;
        
        if(routeAdditionalTime > 0 && routeEnergySavings > 0){
            //if((routeAdditionalTime / routeEnergySavings) <= Parameters.minSlopeRatio){
            if((routeAdditionalTime * Parameters.minSlopeEnergySavings) <= (Parameters.minSlopeAdditionalTime * routeEnergySavings)){
                return (routeAdditionalTime / routeEnergySavings);
            } else {
                return -1;
            }
        }
        
        return -1;
    }
    
    private boolean stop(int n){
        if(Parameters.stopCriteria.equalsIgnoreCase("convergence")){
            for(int i = 0; i < Graph.arcs.length; i++){
                double difference = Math.abs(Graph.aVolumes[i] - volumeLastIteration[i]);
                double variation = ((100 * difference) / volumeLastIteration[i]);

                if(variation > Parameters.convergenceEpsilon){
                    return false;
                }
            }
        } else {
            if(n < Parameters.numberOfIterations)
                return false;
        }
        
        return true;
    }
}