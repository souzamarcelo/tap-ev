
package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author marcelo
 */
public class ResultFile {
    
    private String fileName;
    private BufferedWriter writer;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy--HH-mm");
    
    public ResultFile(String fileName){
        
        Date date = new Date();
        String fileProperties = formatter.format(date);
        
        this.fileName = "results/" + fileName + "_" + fileProperties + "." + Parameters.resultFileExtension;
        
        try {
            File file = new File(this.fileName);
            FileWriter fileWriter = new FileWriter(file, true);
            writer = new BufferedWriter(fileWriter);
        } catch(Exception e){
            System.err.println("Error on open/create result file: " + e.getMessage());
        }
    }
    
    public void writeContent(String content){
        try {
            writer.write(content);
            writer.newLine();
            writer.flush();
        } catch(Exception e){
            System.err.println("Error on write result file: " + e.getMessage());
        }
    }
}
