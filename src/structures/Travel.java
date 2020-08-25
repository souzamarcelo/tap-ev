/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package structures;

import java.util.List;

/**
 *
 * @author mdesouza
 */
public class Travel {
    public int id;
    
    public int source;
    public int destination;
    public double amount;
    
    public Route travelRoute;
    public List<Route> travelRoutes;
    public double lessTime;
    
    public String vehicleType;
}
