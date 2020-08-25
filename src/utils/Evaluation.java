
package utils;

import structures.Graph;
import structures.PathFlow;
import structures.Route;
import cost_function.EnergyModel;
import cost_function.VDFTimeCostFunction;
import java.util.List;
import structures.Travel;

/**
 *
 * @author mdesouza
 */
public class Evaluation {
    
    private static List<Travel> travels;
    
    public static double timeByFlowsOfGasolineVehicles(List<Travel> myTravels, List<PathFlow> pathFlows){
        travels = myTravels;
         
        double totalTime = 0d;
        double totalAmount = 0d;
        
        for(PathFlow pathFlow: pathFlows){
            if(pathFlow.vehicleType.equals("gasoline")){
                totalAmount += pathFlow.flow;
            }
        }
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
            
            double totalGasolineVehicles = 0d;
            for(PathFlow pathFlow: pathFlows){
                if(pathFlow.vehicleType.equals("gasoline")){
                    List<Integer> rba = pathFlow.route.getRouteByArcs();
                    if(rba.contains(i))
                        totalGasolineVehicles += pathFlow.flow;
                }
            }
            totalTime += (totalGasolineVehicles * Graph.aCalculatedTimes[i]);
        }
        
        return (totalTime / totalAmount);
    }
    
    public static double timeByFlowsOfElectricVehicles(List<Travel> myTravels, List<PathFlow> pathFlows){
        travels = myTravels;
         
        double totalTime = 0d;
        double totalAmount = 0d;
        
        for(PathFlow pathFlow: pathFlows){
            if(pathFlow.vehicleType.equals("electric")){
                totalAmount += pathFlow.flow;
            }
        }
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
            
            double totalGasolineVehicles = 0d;
            for(PathFlow pathFlow: pathFlows){
                if(pathFlow.vehicleType.equals("electric")){
                    List<Integer> rba = pathFlow.route.getRouteByArcs();
                    if(rba.contains(i))
                        totalGasolineVehicles += pathFlow.flow;
                }
            }
            totalTime += (totalGasolineVehicles * Graph.aCalculatedTimes[i]);
        }
        
        return (totalTime / totalAmount);
    }
    
    public static double energyByFlowsOfGasolineVehicles(List<Travel> myTravels, List<PathFlow> pathFlows){
        setEnergyEvaluationEtas("gasoline");
        
        travels = myTravels;
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
        
        double totalEnergy = 0d;
        double totalAmount = 0d;
        
        for(PathFlow pathFlow: pathFlows){
            if(pathFlow.vehicleType.equals("gasoline")){
                totalAmount += pathFlow.flow;
                int previousArc = -1;
                for(int i = 0; i < pathFlow.route.getRouteByArcs().size(); i++){
                    int actualArc = pathFlow.route.getRouteByArcs().get(i);
                    if(i != 0){
                        previousArc = pathFlow.route.getRouteByArcs().get(i-1);
                    }
                    
                    int vertexFrom = -1;
                    for(int j = 0; j < Graph.vertices.length; j++){
                        if(Graph.vertices[j] <= actualArc)
                            vertexFrom = j;
                    }
                    
                    totalEnergy += (energyCostFunction(vertexFrom, Graph.arcs[actualArc], pathFlow.source, pathFlow.destination, previousArc) * pathFlow.flow);
                }
            }
        }
        
        return ((totalEnergy / totalAmount) / 1000000);
    }
    
    public static double energyByFlowsOfElectricVehicles(List<Travel> myTravels, List<PathFlow> pathFlows){
        setEnergyEvaluationEtas("electric");
        
        travels = myTravels;
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
        
        double totalEnergy = 0d;
        double totalAmount = 0d;
        
        for(PathFlow pathFlow: pathFlows){
            if(pathFlow.vehicleType.equals("electric")){
                totalAmount += pathFlow.flow;
                int previousArc = -1;
                for(int i = 0; i < pathFlow.route.getRouteByArcs().size(); i++){
                    int actualArc = pathFlow.route.getRouteByArcs().get(i);
                    if(i != 0){
                        previousArc = pathFlow.route.getRouteByArcs().get(i-1);
                    }
                    
                    int vertexFrom = -1;
                    for(int j = 0; j < Graph.vertices.length; j++){
                        if(Graph.vertices[j] <= actualArc)
                            vertexFrom = j;
                    }
                    
                    totalEnergy += (energyCostFunction(vertexFrom, Graph.arcs[actualArc], pathFlow.source, pathFlow.destination, previousArc) * pathFlow.flow);
                }
            }
        }
        
        return ((totalEnergy / totalAmount) / 1000000);
    }
    
    public static double time(List<Travel> myTravels){
        travels = myTravels;
        
        double totalTime = 0d;
        double totalAmount = 0d;
        
        for(Travel travel: travels){
            totalAmount += travel.amount;
        }
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
            totalTime += Graph.aVolumes[i] * Graph.aCalculatedTimes[i];
        }
        
        return (totalTime / totalAmount);
    }
    
    public static double energyByFlows(List<Travel> myTravels, List<PathFlow> pathFlows, String vehicleType){
        setEnergyEvaluationEtas(vehicleType);
        
        travels = myTravels;
        
        for(int i = 0; i < Graph.arcs.length; i++){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
        
        double totalEnergy = 0d;
        double totalAmount = 0d;
        
        //for(int a = 0; a < Graph.arcs.length; a++){
            for(PathFlow pathFlow: pathFlows){
                int previousArc = -1;
                for(int i = 0; i < pathFlow.route.getRouteByArcs().size(); i++){
                    int actualArc = pathFlow.route.getRouteByArcs().get(i);
                    //if(actualArc == a){
                        if(i != 0){
                            previousArc = pathFlow.route.getRouteByArcs().get(i-1);
                        }
                        
                        int vertexFrom = -1;
                        for(int j = 0; j < Graph.vertices.length; j++){
                            if(Graph.vertices[j] <= actualArc)
                                vertexFrom = j;
                        }
                        
                        totalEnergy += (energyCostFunction(vertexFrom, Graph.arcs[actualArc], pathFlow.source, pathFlow.destination, previousArc) * pathFlow.flow);
                    //}
                }
            }
        //}
        
        for(Travel travel : travels){
            totalAmount += travel.amount;
        }
        
        return ((totalEnergy / totalAmount) / 1000000);
    }
    
    public static double evaluateTimeOfRoute(Route route){
        
        double totalTime = 0d;
        for(int i: route.getRouteByArcs()){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
            totalTime += Graph.aCalculatedTimes[i];
        }
        
        return totalTime;
    }
    
    public static double evaluateEnergyOfRoute(Route route){
        
        List<Integer> rba = route.getRouteByArcs();
        for(int i: rba){
            Graph.aCalculatedTimes[i] = VDFTimeCostFunction.getTime(Graph.aAlphas[i], Graph.aBetas[i], Graph.aCapacities[i], Graph.aVolumes[i], Graph.aFreeFlowTravelTimes[i]);
            Graph.aCalculatedVelocities[i] = Graph.aDistances[i] / (Graph.aCalculatedTimes[i] * 60);
        }
        
        double totalEnergy = 0d;
        for(int i = 0; i < rba.size(); i++){
            int previousArc = -1;
            int a = rba.get(i);

            if(i != 0)
                previousArc = rba.get(i-1);

            int vertexFrom = -1;
            for(int j = 0; j < Graph.vertices.length; j++){
                if(Graph.vertices[j] <= a)
                    vertexFrom = j;
            }
            
            totalEnergy += energyCostFunction(vertexFrom, Graph.arcs[a], route.routeByNodes.get(0),
                    route.routeByNodes.get(route.routeByNodes.size()-1), previousArc);
        }
        
        return totalEnergy / 1000000;
    }
    
    private static double energyCostFunction(int from, int to, int source, int destination, int previousArc){
        
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
            previousVelocity = Graph.aCalculatedVelocities[previousArc];
            elevationFrom = elevationFrom = Graph.vElevations[from];
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
            previousVelocity = Graph.aCalculatedVelocities[previousArc];
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
    
    private static void setEnergyEvaluationEtas(String vehicleType){
        if(vehicleType.equalsIgnoreCase("electric")){
            
            if(Parameters.energyEvaluationMode.equalsIgnoreCase("Tank-to-wheel")){
                Parameters.eta_in = Parameters.eta_in_electric_tank_to_wheel;
                Parameters.eta_out = Parameters.eta_out_electric_tank_to_wheel;
            } else if(Parameters.energyEvaluationMode.equalsIgnoreCase("Well-to-wheel")){
                Parameters.eta_in = Parameters.eta_in_electric_well_to_wheel;
                Parameters.eta_out = Parameters.eta_out_electric_well_to_wheel;
            }
            
        } else if(vehicleType.equalsIgnoreCase("gasoline")){
            
            if(Parameters.energyEvaluationMode.equalsIgnoreCase("Tank-to-wheel")){
                Parameters.eta_in = Parameters.eta_in_gasoline_tank_to_wheel;
                Parameters.eta_out = Parameters.eta_out_gasoline_tank_to_wheel;
            } else if(Parameters.energyEvaluationMode.equalsIgnoreCase("Well-to-wheel")){
                Parameters.eta_in = Parameters.eta_in_gasoline_well_to_wheel;
                Parameters.eta_out = Parameters.eta_out_gasoline_well_to_wheel;
            }
            
        }
    }
    
    private static int findArc(int from, int to){
        for(int i = Graph.vertices[from]; i < Graph.arcs.length; i++){
            if(Graph.arcs[i] == to){
                return i;
            }
        }
        return -1;
    }
}
