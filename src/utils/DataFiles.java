/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import structures.Graph;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import structures.Travel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author mdesouza
 */
public class DataFiles {
    
    private static String fileName;
    
    public static void initializeArrays(){
        
        int verticesNumber = numberOfNodes();
        Graph.vertices = new int[verticesNumber];
        Graph.vIds = new String[verticesNumber];
        Graph.vElevations = new double[verticesNumber];
        Graph.vLatitudes = new double[verticesNumber];
        Graph.vLongitudes = new double[verticesNumber];

        int arcsNumber = numberOfArcs();
        Graph.arcs = new int[arcsNumber];
        Graph.aIds = new String[arcsNumber];
        Graph.aDistances = new double[arcsNumber];
        Graph.aFreeFlowTravelTimes = new double[arcsNumber];
        Graph.aFreeFlowVelocities = new double[arcsNumber];
        Graph.aCapacities = new double[arcsNumber];
        Graph.aAlphas = new double[arcsNumber];
        Graph.aBetas = new double[arcsNumber];
        Graph.aVolumes = new double[arcsNumber];
        Graph.aCalculatedTimes = new double[arcsNumber];
        Graph.aCalculatedVelocities = new double[arcsNumber];
        
    }
    
    public static void readData(String nameFile){
        fileName = nameFile + ".net.xml";
        initializeArrays();
        List<Node> nodeList = readNodes();
        List<Arc> arcList = readArcs(nodeList);
        
        Graph.vertices = new int[nodeList.size()];
        Graph.arcs = new int[arcList.size()];
        
        for(int i = 0; i < nodeList.size(); i++){
            Node node = nodeList.get(i);
            Graph.vIds[i] = node.id;
            Graph.vElevations[i] = node.elevation;
            Graph.vLatitudes[i] = node.latitude;
            Graph.vLongitudes[i] = node.longitude;
        }
        
        int index = 0;
        for(int i = 0; i < Graph.vertices.length; i++){
            Graph.vertices[i] = index;
            
            for(Arc arc : arcList){
                if(arc.from.id.equals(Graph.vIds[i])){
                    for(int j = 0; j < Graph.vertices.length; j++){
                        if(Graph.vIds[j].equals(arc.to.id)){
                            Graph.arcs[index] = j;
                            Graph.aIds[index] = arc.id;
                            Graph.aDistances[index] = arc.distance;
                            Graph.aCapacities[index] = arc.capacity;
                            Graph.aFreeFlowTravelTimes[index] = arc.freeFlowTravelTime;
                            Graph.aFreeFlowVelocities[index] = arc.freeFlowVelocity;
                            Graph.aAlphas[index] = arc.alpha;
                            Graph.aBetas[index] = arc.beta;
                            Graph.aVolumes[index] = arc.volume;
                            Graph.aCalculatedTimes[index] = arc.calculatedTime;
                            index++;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public static List<Node> readNodes(){
        
        List<Node> resultNodes = new ArrayList<Node>();
        
        try{
            File file = new File("networks/"+fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            NodeList list = doc.getElementsByTagName("node");
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                Node newNode = new Node();
                newNode.id = e.getAttribute("id");
                newNode.elevation = Double.parseDouble(e.getAttribute("elevation"));
                newNode.latitude = Double.parseDouble(e.getAttribute("lat"));
                newNode.longitude = Double.parseDouble(e.getAttribute("lon"));
                newNode.nodeArcs = new ArrayList<Arc>();
                newNode.predecessor = null;
                newNode.costToNode = 0d;
                
                resultNodes.add(newNode);
            }
            
        } catch (Exception e){
            System.err.println("Error on node read - [" + e.getMessage() + "]");
        }
        
        return resultNodes;
    }
    
    
    public static List<Arc> readArcs(List<Node> nodes){
        
        ManageNetwork mn = new ManageNetwork(nodes, null);
        List<Arc> resultArcs = new ArrayList<Arc>();
        
        try{
            File file = new File("networks/"+fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            NodeList list = doc.getElementsByTagName("edge");
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                
                Arc newArc = new Arc();
                newArc.id = e.getAttribute("id");
                newArc.alpha = Double.parseDouble(e.getAttribute("alpha"));
                newArc.beta = Double.parseDouble(e.getAttribute("beta"));
                newArc.calculatedTime = 0d;
                newArc.capacity = Double.parseDouble(e.getAttribute("capacity"));
                newArc.distance = Double.parseDouble(e.getAttribute("lenght"));
                newArc.freeFlowTravelTime = Double.parseDouble(e.getAttribute("fftime"));
                newArc.freeFlowVelocity = newArc.distance / (newArc.freeFlowTravelTime * 60);
                newArc.volume = 0d;
                newArc.from = mn.getNodeById(e.getAttribute("from"));
                newArc.to = mn.getNodeById(e.getAttribute("to"));
                
                resultArcs.add(newArc);
            }
            
        } catch (Exception e){
            System.err.println("Error on arc read - [" + e.getMessage() + "]");
        }
        
        for(Arc a: resultArcs){
            for(Node n: nodes){
                if(a.from.id.equals(n.id))
                    n.addArc(a);
            }
        }
        
        return resultArcs;
    }
    
    public static int numberOfNodes(){
        try{
            File file = new File("networks/"+fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            NodeList list = doc.getElementsByTagName("node");
            return list.getLength();
            
        } catch (Exception e){
            System.err.println("Error on node read - [" + e.getMessage() + "]");
        }
        
        return 0;
    }
    
    
    public static int numberOfArcs(){
        try{
            File file = new File("networks/"+fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            NodeList list = doc.getElementsByTagName("edge");
            return list.getLength();
            
        } catch (Exception e){
            System.err.println("Error on arc read - [" + e.getMessage() + "]");
        }
        
        return 0;
    }
    
    public static List<Travel> readOdMatrix(String nameFile){
        List<Travel> resultTravels = new ArrayList<Travel>();
        
        try{
            File file = new File("networks/"+nameFile);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            int counter = 0;
            NodeList list = doc.getElementsByTagName("od");
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                Travel newTravel = new Travel();
                newTravel.source = Integer.parseInt(e.getAttribute("origin")) - 1;
                newTravel.destination = Integer.parseInt(e.getAttribute("destination")) - 1;
                newTravel.amount = Double.parseDouble(e.getAttribute("trips"));
                newTravel.travelRoute = null;
                newTravel.id = counter;
                
                resultTravels.add(newTravel);
                counter++;
            }
            
        } catch (Exception e){
            System.err.println("Error on OD read - [" + e.getMessage() + "]");
        }
        
        return resultTravels;
    }
    
    
    private static class Arc {
        public String id;
        public Node from;
        public Node to;
        public double distance;
        public double freeFlowVelocity;
        public double freeFlowTravelTime;
        public double capacity;
        public double alpha;
        public double beta;
        public double volume;
        public double calculatedTime;

        public double getCalculatedVelocity(){
            return (this.distance / (this.calculatedTime * 60));
        }
    }
    
    private static class Node {
        public String id;
        public double elevation;
        public double latitude;
        public double longitude;
        public List<Arc> nodeArcs = new ArrayList<Arc>();
        public double costToNode = 0d;
        public Node predecessor = null;   

        public void addArc(Arc a){
            nodeArcs.add(a);
        }

        public List<Node> getSucessors(){
            List<Node> sucessors = new ArrayList<Node>();
            for(Arc a: nodeArcs){
                Node n = a.to;
                sucessors.add(n);
            }
            return sucessors;
        }
    }
    
    private static class ManageNetwork {
    
        public List<Node> nodes;
        public List<Arc> arcs;

        public ManageNetwork(List<Node> nodes, List<Arc> arcs){
            this.nodes = nodes;
            this.arcs = arcs;
        }

        public Node getNodeById(String nodeId){
            for(Node n: nodes){
                if(n.id.equals(nodeId))
                    return n;
            }
            return null;
        }

        public Arc getArcByNodes(Node from, Node to){
            for(Arc a: arcs){
                if(a.from.id.equals(from.id) && a.to.id.equals(to.id))
                    return a;
            }
            return null;
        }
    }
}
