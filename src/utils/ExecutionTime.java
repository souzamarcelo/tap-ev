
package utils;

/**
 *
 * @author mdesouza
 */
public class ExecutionTime {
    
    private long initialTime;
    private long finalTime;
    
    public void initializeCounter(){
        initialTime = System.currentTimeMillis();
    }
    
    public void finalizeCounter(){
        finalTime = System.currentTimeMillis();
    }
    
    public long getExecutionTimeMs(){
        return (finalTime - initialTime);
    }
    
    public double getExecutionTimeS(){
        return (getExecutionTimeMs() / 1000d);
    }
    
    public double getExecutionTimeMin(){
        return (getExecutionTimeS() / 60d);
    }
}