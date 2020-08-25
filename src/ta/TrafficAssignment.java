/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ta;

import structures.Travel;
import java.util.ArrayList;
import java.util.List;
import utils.DataFiles;
import utils.Evaluation;
import utils.ExecutionTime;
import structures.Graph;
import utils.Parameters;
import utils.Result;

/**
 *
 * @author mdesouza
 */
public class TrafficAssignment {
    
    private List<Travel> travels = new ArrayList<Travel>();
    private double totalAmount = 0d;
    
    public Result run() throws Exception{
        importData();
        setEtas();
        Result result = null;
        ExecutionTime executionTime = new ExecutionTime();
        executionTime.initializeCounter();
        
        switch(Parameters.method){
            case "SA":
                result = runSuccessiveAverages();
                break;
            case "BSA-GE":
                result = runBiobjectiveSuccessiveAveragesGreedyEnergy();
                break;
            case "BSA-GS":
                result = runBiobjectiveSuccessiveAveragesGreedySlope();
                break;
        }
        
        executionTime.finalizeCounter();
        
        result.energyEvaluationMode = Parameters.energyEvaluationMode;
        
        if(result != null){
            result.executionTime = executionTime.getExecutionTimeS();
        }
        
        return result;
    }
    
    private Result runBiobjectiveSuccessiveAveragesGreedyEnergy() throws Exception{
        BiobjectiveSuccessiveAveragesGreedyEnergy biobjectiveSuccessiveAveragesGreedyEnergy = new BiobjectiveSuccessiveAveragesGreedyEnergy();
        Result result = biobjectiveSuccessiveAveragesGreedyEnergy.run(this.travels);
        result.timeOfGasolineVehicles = Evaluation.timeByFlowsOfGasolineVehicles(result.travels, result.pathFlows);
        result.timeOfElectricVehicles = Evaluation.timeByFlowsOfElectricVehicles(result.travels, result.pathFlows);
        result.energyOfElectricVehicles = Evaluation.energyByFlowsOfElectricVehicles(result.travels, result.pathFlows);
        result.energyOfGasolineVehicles = Evaluation.energyByFlowsOfGasolineVehicles(result.travels, result.pathFlows);
        result.energyCost = (((result.energyOfElectricVehicles * (this.totalAmount * (Parameters.percentElectricVehicles / 100))) + (result.energyOfGasolineVehicles * (this.totalAmount * ((100 - Parameters.percentElectricVehicles) / 100)))) / this.totalAmount);
        result.timeCost = (((result.timeOfElectricVehicles * (this.totalAmount * (Parameters.percentElectricVehicles / 100))) + (result.timeOfGasolineVehicles * (this.totalAmount * ((100 - Parameters.percentElectricVehicles) / 100)))) / this.totalAmount);

        if(Parameters.percentElectricVehicles == 0){
            result.energyCost = result.energyOfGasolineVehicles;
            result.timeCost = result.timeOfGasolineVehicles;
        }
        if(Parameters.percentElectricVehicles == 100){
            result.energyCost = result.energyOfElectricVehicles;
            result.timeCost = result.timeOfElectricVehicles;
        }

        return result;
    }
    
    private Result runBiobjectiveSuccessiveAveragesGreedySlope() throws Exception{
        BiobjectiveSuccessiveAveragesGreedySlope biobjectiveSuccessiveAveragesGreedySlope = new BiobjectiveSuccessiveAveragesGreedySlope();
        Result result = biobjectiveSuccessiveAveragesGreedySlope.run(this.travels);
        result.timeOfGasolineVehicles = Evaluation.timeByFlowsOfGasolineVehicles(result.travels, result.pathFlows);
        result.timeOfElectricVehicles = Evaluation.timeByFlowsOfElectricVehicles(result.travels, result.pathFlows);
        result.energyOfElectricVehicles = Evaluation.energyByFlowsOfElectricVehicles(result.travels, result.pathFlows);
        result.energyOfGasolineVehicles = Evaluation.energyByFlowsOfGasolineVehicles(result.travels, result.pathFlows);
        result.energyCost = (((result.energyOfElectricVehicles * (this.totalAmount * (Parameters.percentElectricVehicles / 100))) + (result.energyOfGasolineVehicles * (this.totalAmount * ((100 - Parameters.percentElectricVehicles) / 100)))) / this.totalAmount);
        result.timeCost = (((result.timeOfElectricVehicles * (this.totalAmount * (Parameters.percentElectricVehicles / 100))) + (result.timeOfGasolineVehicles * (this.totalAmount * ((100 - Parameters.percentElectricVehicles) / 100)))) / this.totalAmount);

        if(Parameters.percentElectricVehicles == 0){
            result.energyCost = result.energyOfGasolineVehicles;
            result.timeCost = result.timeOfGasolineVehicles;
        }
        if(Parameters.percentElectricVehicles == 100){
            result.energyCost = result.energyOfElectricVehicles;
            result.timeCost = result.timeOfElectricVehicles;
        }

        return result;
    }
    
    private Result runSuccessiveAverages() throws Exception{
        SuccessiveAverages successiveAverages = new SuccessiveAverages();
        Result result = successiveAverages.run(this.travels);
        result.timeCost = Evaluation.time(result.travels);

        if(Parameters.singleObjective.equalsIgnoreCase("ENERGY")){
            result.energyCost = Evaluation.energyByFlows(result.travels, result.pathFlows, "electric");
            result.timeOfElectricVehicles = result.timeCost;
            result.energyOfElectricVehicles = result.energyCost;
            result.timeOfGasolineVehicles = Double.NaN;
            result.energyOfGasolineVehicles = Double.NaN;
        } else {
            result.energyCost = Evaluation.energyByFlows(result.travels, result.pathFlows, "gasoline");
            result.timeOfGasolineVehicles = result.timeCost;
            result.energyOfGasolineVehicles = result.energyCost;
            result.timeOfElectricVehicles = Double.NaN;
            result.energyOfElectricVehicles = Double.NaN;
        }
        return result;
    }
    
    private void setEtas(){
        if(Parameters.energyEvaluationMode.equalsIgnoreCase("Tank-to-wheel")){
            Parameters.eta_in = Parameters.eta_in_electric_tank_to_wheel;
            Parameters.eta_out = Parameters.eta_out_electric_tank_to_wheel;
        }
        if(Parameters.energyEvaluationMode.equalsIgnoreCase("Well-to-wheel")){
            Parameters.eta_in = Parameters.eta_in_electric_well_to_wheel;
            Parameters.eta_out = Parameters.eta_out_electric_well_to_wheel;
        }
    }
    
    private void importData(){
        DataFiles.readData(Parameters.network);
        this.travels = DataFiles.readOdMatrix(Parameters.network + ".od.xml");
        
        if(Parameters.variateDemand){
            List<Travel> newTravels = new ArrayList<Travel>();
            for(int i = 0; i < Parameters.numberOfODPairs; i++){
                newTravels.add(travels.get(i));
            }
            
            double amount = Parameters.variateTotalDemand / Parameters.numberOfODPairs;
            for(Travel travel : newTravels){
                travel.amount = amount;
            }
            
            travels = newTravels;    
        }
        
        for(Travel travel: travels){
            this.totalAmount += travel.amount;
        }
        
        if(Parameters.perturbElevation){
            for(int i = 0; i < Graph.vElevations.length; i++)
                Graph.vElevations[i] *= Parameters.perturbationValue;
        }
    }
}
