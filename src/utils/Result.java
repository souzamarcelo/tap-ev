/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import structures.PathFlow;
import java.util.ArrayList;
import java.util.List;
import structures.Travel;

/**
 *
 * @author mdesouza
 */
public class Result {
    public String taMethod = "";
    public String objective = "";
    public double percentElectricVehicles;
    public String energyEvaluationMode;
    public double executionTime;
    
    public double timeCost;
    public double energyCost;
    public double timeOfGasolineVehicles;
    public double energyOfGasolineVehicles;
    public double energyOfElectricVehicles;
    public double timeOfElectricVehicles;
    
    public List<Travel> travels;
    public List<PathFlow> pathFlows;
    
    public EmpiricalAnalyzer analysis;
    
    public String toString(){
        String content = taMethod + "-" + percentElectricVehicles + "%  (" + executionTime + ")";
        return content;
    }
}
