/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cost_function;

/**
 *
 * @author mdesouza
 */
public class VDFTimeCostFunction {
    
    public static double getTime(double alpha, double beta, double capacity, double volume, double fftt){
        return fftt * (1 + (alpha * (Math.pow((volume / capacity), beta))));
    }
    
}
