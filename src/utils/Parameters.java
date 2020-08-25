/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author marcelo
 */
public class Parameters {
    
    //General parameters
    public static String method;
    public static String network;
    public static String singleObjective;
    public static String firstObjective;
    public static String secondObjective;
    
    //Electric vehicles
    public static double maxCapacity = Double.MAX_VALUE;
    public static double remainingStorageCapacity = 10000000000d;
    
    //Energy function
    public static double eta_in = 0.7;
    public static double eta_out = 0.9;
    public static double eta_in_electric_well_to_wheel = 0.7;
    public static double eta_out_electric_well_to_wheel = 0.27;
    public static double eta_in_electric_tank_to_wheel = 0.7;
    public static double eta_out_electric_tank_to_wheel = 0.9;
    public static double eta_in_gasoline_well_to_wheel = 0d;
    public static double eta_out_gasoline_well_to_wheel = 0.18;
    public static double eta_in_gasoline_tank_to_wheel = 0d;
    public static double eta_out_gasoline_tank_to_wheel = 0.2;
    public static String energyEvaluationMode;
    
    //Heterogeneity
    public static double[] admissibleExtraTimeValues;
    public static double[] admissibleExtraTimeDistribution;
    public static double percentElectricVehicles;
    
    //Incremental Assignment
    public static double[] fractions; // {0.4, 0.3, 0.2, 0.1}
    
    //Successive Averages
    public static String stopCriteria; // convergence or numberofiterations
    public static double convergenceEpsilon;
    public static int numberOfIterations;
    
    //LETC method
    public static double minSlopeAdditionalTimePercentage;
    public static double minSlopeEnergySavingsPercentage;
    public static double minSlopeAdditionalTime;
    public static double minSlopeEnergySavings;
    
    //Result parameters
    public static String resultFileName;
    public static String resultFileExtension = "csv";
    public static boolean generateOutputVisualization;
    
    //Log parameters
    public static boolean generateLog;
    public static String logFileName;
    public static String logFileExtension = "txt";
    
    //Variation of demand
    public static boolean variateDemand = false;
    public static double numberOfODPairs;
    public static double variateTotalDemand;
    
    //Perturbation of elevation values
    public static boolean perturbElevation = false;
    public static double[] perturbationValues;
    public static double perturbationValue;
    
    //Analyzer
    public static boolean analyze = false;
}