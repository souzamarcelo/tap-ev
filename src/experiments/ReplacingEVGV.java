/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package experiments;

import ta.TrafficAssignment;
import utils.Parameters;
import utils.Result;
import utils.ResultFile;

/**
 *
 * @author mdesouza
 */
public class ReplacingEVGV {
    
    private ResultFile resultFile;
    
    private void setFixedParameters(String network){
        
        Parameters.network = network;
        Parameters.resultFileName = Parameters.network;
        Parameters.firstObjective = "TIME";
        Parameters.secondObjective = "ENERGY";
        
        double[] extraTimeValues = {0d, 10d, 20d, 30d, 40d, 50d, 60d, 70d, 80d, 90d, 100d, 110d, 120d, 130d, 140d, 150d, 190d, 250d, 400d};
        Parameters.admissibleExtraTimeValues = extraTimeValues;
        double[] extraTimeDistribution = {3.2, 11.0, 9.1, 9.1, 11.0, 16.2, 6.5, 11.0, 3.2, 5.2, 1.9, 1.3, 3.2, 1.3, 2.6, 1.3, 0.8, 0.8, 1.3};
        Parameters.admissibleExtraTimeDistribution = extraTimeDistribution;
        
        Parameters.generateLog = false;
        Parameters.generateOutputVisualization = false;
        Parameters.stopCriteria = "convergence";
        Parameters.convergenceEpsilon = 0.05;
        //Parameters.convergenceEpsilon = 1;
        Parameters.numberOfIterations = 1000;
        
        Parameters.minSlopeAdditionalTimePercentage = 50d;
        Parameters.minSlopeEnergySavingsPercentage = 45d;
    }
    
    public void run(String network) throws Exception{
        setFixedParameters(network);
        
        resultFile = new ResultFile(Parameters.resultFileName + "REPLACING");
        resultFile.writeContent("method;evaluation;% electric;time;energy;gas time;gas energy;elec time;elec energy;runtime [s]");
        
        Parameters.eta_in_electric_tank_to_wheel = 0.7;
        Parameters.eta_out_electric_tank_to_wheel = 0.9;
        
        Parameters.eta_in_gasoline_tank_to_wheel = 0.7;
        Parameters.eta_out_gasoline_tank_to_wheel = 0.9;
        
        TAPBiobjectiveGE();
        TAPBiobjectiveGS();
    }
    
    public void TAPBiobjectiveGS() throws Exception{
        String[] biobjectiveMethods = {"BSA-GS"};
        double[] percentsElectric = {0d, 100d};
        
        String[] evaluationModes = {"Tank-to-wheel"};
        Parameters.singleObjective = "TIME";
        
        for(String evaluationMode : evaluationModes){
            Parameters.energyEvaluationMode = evaluationMode;
            for(double percentageElectric: percentsElectric){
                for(String method: biobjectiveMethods){
                    Parameters.eta_in = 0.7;
                    Parameters.eta_out = 0.9;

                    Parameters.method = method;
                    Parameters.percentElectricVehicles = percentageElectric;
                    TrafficAssignment ta = new TrafficAssignment();
                    Result result = ta.run();
                    System.out.println(result.toString());
                    resultFile.writeContent(result.taMethod + ";" + result.energyEvaluationMode + ";" + result.percentElectricVehicles + ";" + result.timeCost + ";" + result.energyCost + ";" + result.timeOfGasolineVehicles + ";" + result.energyOfGasolineVehicles + ";" + result.timeOfElectricVehicles + ";" + result.energyOfElectricVehicles + ";" + result.executionTime);
                }
            }
            resultFile.writeContent("");
        }
    }
    
    public void TAPBiobjectiveGE() throws Exception{
        String[] biobjectiveMethods = {"BSA-GE"};
        double[] percentsElectric = {0d, 100d};
        String[] evaluationModes = {"Tank-to-wheel"};
        Parameters.singleObjective = "TIME";
        
        for(String evaluationMode : evaluationModes){
            Parameters.energyEvaluationMode = evaluationMode;
            for(double percentageElectric: percentsElectric){
                for(String method: biobjectiveMethods){
                    Parameters.method = method;
                    Parameters.percentElectricVehicles = percentageElectric;
                    TrafficAssignment ta = new TrafficAssignment();
                    Result result = ta.run();
                    System.out.println(result.toString());
                    resultFile.writeContent(result.taMethod + ";" + result.energyEvaluationMode + ";" + result.percentElectricVehicles + ";" + result.timeCost + ";" + result.energyCost + ";" + result.timeOfGasolineVehicles + ";" + result.energyOfGasolineVehicles + ";" + result.timeOfElectricVehicles + ";" + result.energyOfElectricVehicles + ";" + result.executionTime);
                }
            }
            resultFile.writeContent("");
        }
    }
}
