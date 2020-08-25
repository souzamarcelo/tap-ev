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
public class SimpleEnergyModel {
    
    double distance;
    double elevationFrom;
    double elevationTo;
    double elevationDifference;
    
    public SimpleEnergyModel(){}
    
    public SimpleEnergyModel(double distance, double elevationFrom, double elevationTo){
        this.distance = distance;
        this.elevationFrom = elevationFrom;
        this.elevationTo = elevationTo;
        
        this.elevationDifference = elevationTo - elevationFrom;
    }
    
    public double consumption(){
        if(elevationDifference > 0)
            return (distance + 4 * elevationDifference);
        else
            return (distance + elevationDifference);
    }
}
