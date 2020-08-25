/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cost_function;

import structures.Graph;
import utils.Parameters;

/**
 *
 * @author mdesouza
 */
public class EnergyModel {
    private double fr = 0.0386; //Rolling friction coefficient of the vehicle
    private double g = 9.8; //Gravity acceleration
    private double p = 1.23; //Air density
    private double alpha = 0.3; //Air resistance coefficient
    private double A = 1; //Frontal area of the vehicle
    private double m = 1000; //Total mass of the vehicle and loads
    private double n_in = 0.7; //Energy conversion efficiency - input energy
    private double n_out = 0.9; //Energy conversion efficiency - output energy
    
    public double elevationFrom;
    public double elevationTo;
    public double previousVelocity;
    public double actualVelocity;
    public double distance;
    
    public EnergyModel(){}
    
    public double[] consumption() {
        
        this.n_in = Parameters.eta_in;
        this.n_out = Parameters.eta_out;
        
	double[] result = new double[1];
        
        //double cost = ((fr * m * g + (p * alpha * A * (previousVelocity * previousVelocity)) / 2)/n_out) * distance;
        double cost = ((fr * m * g + (p * alpha * A * (actualVelocity * actualVelocity)) / 2)/n_out) * distance;
        cost += eta(q(elevationTo) - q(elevationFrom));
        cost += eta(K(actualVelocity) - K(previousVelocity));
        
        if (Double.isNaN(cost)){
            System.out.println(alpha + " | " + previousVelocity + " | " + distance + " | " + elevationTo + " | " + elevationFrom + " | " + actualVelocity);
        }
        
        result[0] = cost;
        return result;
    }
	
    private double q(double elevation){
        return (elevation * this.m * this.g);
    }
    
    private double eta(double x){
        if(x >= 0)
            return (1 / this.n_out) * x;
        else
            return this.n_in * x;
    }

    private double K(double v){
        return (this.m * Math.pow(v, 2))/2;
    }
    
}
