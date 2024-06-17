import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Simulation of a very cheap taxi service offered by supermarket chain QnQ using a graph data structure.
 * @author Siyabonga Madondo
 * @version 30/03/2024
*/
@SuppressWarnings("unused")
public class SimulatorOne extends Graph
{
    /** Variable which stores the number of vertices in this simulation.*/
    private int numNodes;
    /** Variable which stores the number of Clients in this simulation.*/
    private int numClients;
    /** Variable which stores the number of QnQ shops in this simulation.*/
    private int numShops;
    /** Variable which stores the chronological order of calls.*/
    private String calls;

    /**
     * Create a new Simulation Object and initialize the instance variables.
    */
    public SimulatorOne(){
        createSimulationGraph();
    }

    /**
     * Create a Graph for this Simulation from the given input.
    */
    private void createSimulationGraph() {
        // Creating a new Scanner for user input.
        Scanner input = null;
        try {
            input = new Scanner(new FileInputStream("Input.txt"));
        } catch (FileNotFoundException e){
            System.out.println("File not found.");
        }
        // Step 1 - Creating the Graph from User Input with a specified number of nodes and associated costs.
        this.numNodes = input.nextInt();  
        input.nextLine();
        for (int i = 0; i < this.numNodes; i++) {
            String line = input.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(line);
            String source = tokenizer.nextToken();  // Read the source node.
            while (tokenizer.hasMoreTokens()) {
                // Read the destination and cost from the input and add the edge to the graph.
                String destination = tokenizer.nextToken();
                int cost = Integer.parseInt(tokenizer.nextToken());
                addEdge(source, destination, cost);
            }
        }
        // Step 2 - Getting information about the QnQ Shops in the Simulation.
        this.numClients = input.nextInt();
        input.nextLine();
        String client = input.nextLine();
        // Assuming all nodes are Initially of Type 'None'.
        for (String name : vertexMap.keySet()){
            Vertex node = vertexMap.get(name);
            node.type = "None"; // Assume all nodes are empty initially.
        }
        StringTokenizer tokenizer = new StringTokenizer(client);
        while (tokenizer.hasMoreTokens()) {
            String startName = tokenizer.nextToken();
            if (vertexMap.containsKey(startName)) {
                Vertex node = vertexMap.get(startName);
                node.type = "Shop"; // Mark the node as a Shop.
            }
        }
        // Step 3 - Getting information about the Shops in the Simulation.
        this.numShops = input.nextInt();
        input.nextLine();
        this.calls = input.nextLine();
        tokenizer = new StringTokenizer(calls);
        while (tokenizer.hasMoreTokens()) {
            String startName = tokenizer.nextToken();
            if (vertexMap.containsKey(startName)) {
                Vertex node = vertexMap.get(startName);
                node.type = "Client"; // Mark the node as a Shop.
            }
        }
    }

    /**
     * Obtain the cost of the shortest path trip to the given node.
     * @param destination The name associated with the destination node.
     * @throws NoSuchElementException If the destination vertex is not found.
     * @return The cost associated with the distance node. Return -1 if the destination node is not found.
    */
    public double getCost(String destinationName) throws NoSuchElementException {
        Vertex destination = vertexMap.get(destinationName);
        if(destination == null){
            throw new NoSuchElementException( "Destination vertex not found." );
        }
        else if(destination.dist == INFINITY){
            return Double.POSITIVE_INFINITY;
        }
        return destination.dist;
    }

    /**
     * Obtain the minimum path to the given destination node.
     * @param destination The name associated with the destination node.
     * @throws NoSuchElementException If the destination vertex is not found.
    */
    public void displayPath(String startName, String destinationName){
        Vertex start = vertexMap.get(startName);
        Vertex destination = vertexMap.get(destinationName);
        if(start == null || destination == null){
            throw new NoSuchElementException( "Client or Destination vertex not found.");
        }  
        dijkstra(startName);   // Calculate the shortest path from the start node to all other nodes nodes.
        if (destination.dist == INFINITY){
            System.out.println(destinationName+ " is unreachable from " + startName);
        } else {
            displayPath(destination);
            System.out.println();
        }
    }
    
    /**
     * Obtain the minimum path to the given destination node.
     * @param destination The destination node.
    */
    private void displayPath(Vertex destination){
        if (destination.prev != null){
            displayPath(destination.prev);
            System.out.print(" ");
        }
        System.out.print(destination.name);
    }

    /**
     * Find the nearest QnQ Taxi to the given Client.
     * @param client The name assosiated with the target client.
     * @return The name associated with the nearest Taxi to the given Client.
    */
    private ArrayList<Vertex> findNearestTaxi(String client){
        double minimumCost = Double.POSITIVE_INFINITY;
        ArrayList<Vertex> nearestTaxi = new ArrayList<>();  // Stores a list of the nearest taxis.
        // Loop through the Graph and identify all Taxis - Taxis are found at QnQ Shops.
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop")){
                dijkstra(name);  // Calculate the shortest path from this Shop (Taxi) to the Client.
                // If the cost of this trip is less than the current minimum trip, change the minimum trip and add to the nearest taxi array.
                if (getCost(client) < minimumCost){
                    minimumCost = getCost(client);
                    nearestTaxi.clear();    // Clear the nearest shop array if a shorter trip is found.
                    nearestTaxi.add(vertexMap.get(name));
                } else if (getCost(client) == minimumCost){
                    nearestTaxi.add(vertexMap.get(name));  // Add this vertex to the array of nearest paths if the costs are the same.
                }
            }
        }
        return nearestTaxi;
    }

    /**
     * Find the nearest QnQ Shop from the given Client.
     * @param client The name assosiated with the target client.
     * @return The name associated with the nearest Taxi to the given Client.
    */
    private ArrayList<Vertex> findNearestShop(String client){
        double minimumCost = Double.POSITIVE_INFINITY;
        ArrayList<Vertex> nearestShop = new ArrayList<>();  
        // Loop through the graph and identify all QnQ Shops .
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop")){
                dijkstra(client);  // Calculate the shortest path from this Client all the Shops in the Graph.
                // If the cost of this trip is less than the current minimum trip, change the minimum trip and add to the nearest shop array.
                if (getCost(name) < minimumCost){
                    minimumCost = getCost(name);
                    nearestShop.clear();    // Clear the nearest shop array if a shorter trip is found.
                    nearestShop.add(vertexMap.get(name));
                } else if (getCost(name) == minimumCost){
                    nearestShop.add(vertexMap.get(name));  // Add this vertex to the array of nearest paths if the costs are the same.
                }
            }
        }
        return nearestShop;
    }

    /**
     * Find the nearest QnQ Taxi to the given Client.
     * @param client The name assosiated with the target client.
     * @return The name associated with the nearest Taxi to the given Client.
    */
    public Vertex findTaxi(String client){
        double minimumCost = Double.POSITIVE_INFINITY;
        Vertex nearestTaxi = null;
        // Loop through the Graph and identify all Taxi's - Taxi's are found at QnQ Shops.
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop")){
                dijkstra(name);  // Calculate the shortest path from this Shop (Taxi) to the Clients.
                if (getCost(client) < minimumCost){
                    minimumCost = getCost(client);
                    nearestTaxi = vertexMap.get(name);
                }
            }
        }
        return nearestTaxi;
    }

    /**
     * Find the nearest QnQ Shop from the given Client.
     * @param client The name assosiated with the target client.
     * @return The name associated with the nearest Taxi to the given Client.
    */
    public Vertex findShop(String client){
        double minimumCost = Double.POSITIVE_INFINITY;
        Vertex nearestShop = null;  
        // Loop through the graph and identify all QnQ Shops .
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop")){
                dijkstra(client);  // Calculate the shortest path from this Client all the Shops in the Graph.
                if (getCost(name) < minimumCost){
                    minimumCost = getCost(name);
                    nearestShop = vertexMap.get(name);
                }
            }
        }
        return nearestShop;
    }

    /**
     * Testing method which prints the structure of the simulation graph.
    */
    private void printGraph(){
        for (int i = 0; i < vertexMap.size(); i++){
            if (vertexMap.size() > 0){
                System.out.print("Vertex " + i + " (" + vertexMap.get(Integer.toString(i)).type + ") is connected to: ");
                for (int j=0; j<vertexMap.get(Integer.toString(i)).adj.size(); j++){
                    System.out.print(vertexMap.get(Integer.toString(i)).adj.get(j) + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Create a new Simulation and run using the given user input.
     * @param args
    */
    public static void main(String[] args){
        SimulatorOne s = new SimulatorOne();
        StringTokenizer calls = new StringTokenizer(s.calls);
        while(calls.hasMoreTokens()){
            String client = calls.nextToken();
            System.out.println("client " + client);
            ArrayList<Vertex> nearestTaxi = s.findNearestTaxi(client);
            ArrayList<Vertex> nearestShop = s.findNearestShop(client);
            if (s.findTaxi(client) == null || s.findShop(client) == null){
                System.out.println("cannot be helped");
                continue;
            }
            for (Vertex taxi : nearestTaxi){
                if (taxi.duplicatePathsFound){
                    System.out.println("taxi" + " " + taxi.name);
                    System.out.printf("multiple solutions cost %.0f\n", s.getCost(taxi.name));
                } else {
                    System.out.println("taxi" + " " + taxi.name);
                    s.displayPath(taxi.name,client);
                }   
            }
            nearestTaxi = s.findNearestTaxi(client);
            nearestShop = s.findNearestShop(client);
            for (Vertex shop : nearestShop){
                if (shop.duplicatePathsFound){
                    System.out.println("shop" + " " + shop.name);
                    System.out.printf("multiple solutions cost %.0f\n", s.getCost(shop.name));
                } else {
                    System.out.println("shop" + " " + shop.name);
                    s.displayPath(client, shop.name);
                }
            }
        }  
    }
}