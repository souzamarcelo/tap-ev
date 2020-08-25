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
public class EmpiricalAnalyzer {
    
    //Execution time
    public double totalTime = 0d;
    public double spTime = 0d;
    public double spPercentage = 0d;
    public double msaTime = 0d;
    public double msaPercentage = 0d;
    
    //(M)MSA method
    public int msaIterations = 0;
    
    //BLC method
    public double blcVisitedVertices = 0;
    public double blcMeanLabels = 0d;
    public double blcNonDominatedPaths = 0;
    public double blcRuns = 0;
}
