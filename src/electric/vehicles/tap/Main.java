
package electric.vehicles.tap;

import experiments.AnalyzeEmpiricalComplexity;
import experiments.ElevationPerturbation;
import experiments.MoreEfficiency;
import experiments.NoRecuperation;
import experiments.ReplacingEVGV;
import experiments.SAComplete;
import java.util.ArrayList;
import java.util.List;
import structures.Travel;

/**
 *
 * @author mdesouza
 */
public class Main {
    
    static List<Travel> travels = new ArrayList<Travel>();
    
    public static void main(String[] args) throws Exception{
        
        SAComplete saComplete = new SAComplete();
        //saComplete.run("grid5complete");
        //saComplete.run("siouxfalls");
        //saComplete.run("anaheim");
        
        NoRecuperation noRecuperation = new NoRecuperation();
        //noRecuperation.run("siouxfalls");

        ElevationPerturbation perturbation = new ElevationPerturbation();
        //perturbation.run("siouxfalls");
        
        MoreEfficiency moreEfficiency = new MoreEfficiency();
        //moreEfficiency.run("siouxfalls");
        
        ReplacingEVGV replacing = new ReplacingEVGV();
        //replacing.run("grid5complete");
        //replacing.run("siouxfalls");
        //replacing.run("anaheim");
        
        AnalyzeEmpiricalComplexity analyzeComplexity = new AnalyzeEmpiricalComplexity();
        analyzeComplexity.run("grid5complete");
        analyzeComplexity.run("siouxfalls");
        analyzeComplexity.run("anaheim");
    }
}