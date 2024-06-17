import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Simulation of a taxi service offered by supermarket chains QnQ and Shopify using a Graph Data Structure.
 * This program allows users to request taxis and simulates the response based on shortest path calculations.
 *
 * Features:
 * - Taxi requests can be declined randomly by drivers.
 * - Calculates shortest paths between clients, shops, and taxis based on a pre-defined graph structure.
 *
 * @author Siyabonga Madondo
 * @version 17/06/2024
 */

public class TaxiSimulator extends Graph
{
    /** Constant for the booking fare paid by clients for QnQ Taxis.*/
    public static final double QNQ_BOOKING_FARE = 14.50;
    /** Constant for the booking fare paid by clients for Shopify Taxis.*/
    public static final double SHOPIFY_BOOKING_FARE = 16.00;
    /** Variable which stores the number of vertices in this simulation.*/
    private int numNodes;
    /** Variable which stores the chronological order of calls.*/
    private String calls;

    /**
     * Constructs a new SimulatorTwo object and initializes the simulation graph.
    */
    public TaxiSimulator(){
        createSimulationGraph();
    }
    
    /**
     * Create a new Graph for this Simulation.
     * Extention of SimulationOne which restricts Taxis to operating between shops of their own company.
    */
    private void createSimulationGraph(){
        // Step 1 - Reading the file input and adding nodes to the Simulation Graph.
        Scanner input = null;
        try {
            input = new Scanner(new FileInputStream("Input.txt"));
        } catch (FileNotFoundException e){
            System.out.println("File not found.");
        }
        this.numNodes = input.nextInt();  
        input.nextLine();
        for (int i = 0; i < this.numNodes; i++){
            String line = input.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(line);
            String source = tokenizer.nextToken();  // Read the source node.
            // Read the destination nodes and their associated costs from the input and add the edges to the graph.
            while (tokenizer.hasMoreTokens()){
                String destination = tokenizer.nextToken();
                int cost = Integer.parseInt(tokenizer.nextToken());
                addEdge(source, destination, cost);
            }
        }
        // Step 2 - Labeling all of the Nodes as either a Client or Shop in this simulation.
        // Assuming all nodes are initially of type 'None' - Meaning it is neither a taxi or a client.
        for (String name : vertexMap.keySet()){
            Vertex node = vertexMap.get(name);
            node.type = "None";
        }
        // Part 1 - Label the all the QnQ Shop nodes.
        input.nextLine();
        String qnqShops = input.nextLine();
        StringTokenizer tokenizer = new StringTokenizer(qnqShops);
        // Initially assume that all shops and taxis are 'QnQ Shops' - So that all shops have a type associated.
        while (tokenizer.hasMoreTokens()) {
            String startName = tokenizer.nextToken();
            // If the given node is found in the Vertex Map, update it to a QnQ Shop.
            if (vertexMap.containsKey(startName)) {
                Vertex node = vertexMap.get(startName);
                node.type = "Shop"; // Mark the node as a QnQ Shop.
                node.companyName = "QnQ";
            }
        }
        // Part 2 - Label the all the Shopify Shop nodes.
        input.nextLine();
        String shopifyShops = input.nextLine();
        tokenizer = new StringTokenizer(shopifyShops);
        while (tokenizer.hasMoreTokens()) {
            String startName = tokenizer.nextToken();
            // If the given node is found in the Vertex Map, update it to a Shopify Shop.
            if (vertexMap.containsKey(startName)) {
                Vertex node = vertexMap.get(startName);
                node.type = "Shop"; // Mark the node as a Shopify Shop.
                node.companyName = "Shopify";
            }
        }
        // Part 3 - Label of the Clients in this Simulation.
        input.nextLine();
        this.calls = input.nextLine();
        tokenizer = new StringTokenizer(calls);
        while (tokenizer.hasMoreTokens()) {
            String startName = tokenizer.nextToken();
            // If the given node is found in the Vertex Map, update it to a Client.
            if (vertexMap.containsKey(startName)) {
                Vertex node = vertexMap.get(startName);
                node.type = "Client"; // Mark the node as a Client.
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
        else if (destination.dist == INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        return destination.dist;
    }

    /**
     * Obtain the cost of the shortest path trip between the given start and end nodes.
     * @param startName The name associated with the start node.
     * @param destinationName The name associated with the destination node.
     * @throws NoSuchElementException If either the start or destination vertex is not found.
     * @return The cost associated with the shortest path between the start and destination nodes.
     */
    public double getCost(String startName, String destinationName) throws NoSuchElementException {
        Vertex start = vertexMap.get(startName);
        Vertex destination = vertexMap.get(destinationName);
        if(start == null || destination == null){
            throw new NoSuchElementException("Start or destination vertex not found.");
        }
        dijkstra(startName); // Calculate the shortest paths from the start node to all other nodes.
        if (destination.dist == INFINITY){
            return Double.POSITIVE_INFINITY;
        }
        return destination.dist;
    }

    /**
     * Find the nearest QnQ or Shopify Taxi to the given Client, depending on the input.
     * @param client The name assosiated with the target client.
     * @param company The name of the shop company.
     * @return The name associated with the nearest Taxi to the given Client.
    */
    public ArrayList<Vertex> findNearestTaxi(String client, String company){
        double minimumCost = Double.POSITIVE_INFINITY;
        ArrayList<Vertex> nearestTaxi = new ArrayList<>();  // Stores a list of the nearest taxis.
        // Loop through the Graph and identify all Taxis - Taxis are found at QnQ Shops.
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop") && vertexMap.get(name).companyName.equalsIgnoreCase(company)){
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
    public ArrayList<Vertex> findNearestShop(String client, String company){
        double minimumCost = Double.POSITIVE_INFINITY;
        ArrayList<Vertex> nearestShop = new ArrayList<>();  
        // Loop through the graph and identify all QnQ Shops .
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop") && vertexMap.get(name).companyName.equalsIgnoreCase(company)){
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
     * Find the nearest QnQ or Shopify Taxi to the given Client, depending on the input.
     * @param client The name assosiated with the target client.
     * @param company The name of the shop company.
     * @return The name associated with the nearest Taxi to the given Client.
    */
    private Vertex findTaxi(String client, String company){
        double minimumCost = Double.POSITIVE_INFINITY;
        Vertex nearestTaxi = null;  // Stores a list of the nearest taxis.
        // Loop through the Graph and identify all Taxis - Taxis are found at QnQ Shops.
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop") && vertexMap.get(name).companyName.equalsIgnoreCase(company)){
                dijkstra(name);  // Calculate the shortest path from this Shop (Taxi) to the Client.
                // If the cost of this trip is less than the current minimum trip, change the minimum trip and add to the nearest taxi array.
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
    private Vertex findShop(String client, String company){
        double minimumCost = Double.POSITIVE_INFINITY;
        Vertex nearestShop = null;  
        // Loop through the graph and identify all QnQ Shops .
        for (String name : vertexMap.keySet()){
            if (vertexMap.get(name).type.equalsIgnoreCase("Shop") && vertexMap.get(name).companyName.equalsIgnoreCase(company)){
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
     * Obtain the Taxi Fare to be paid by the client upon completion.
     * The Booking Price for QnQ Taxis is R14.50 and Shopify Taxis is R16.
     * Customers Pay 20% of the Pick-Up Cost for QnQ Taxis and 15% for Shopify Taxis for driver petrol.
     * Customers Pay 100% of the Drop-Off Cost for both QnQ Taxis and Shopify Taxis.
     * @param pickUpCost The cost of the trip taken by the taxi to pick up the client.
     * @param dropOffCost The cost of the trip taken by the taxi to drop the client off.
     * @param company The company chosen by the client.
     * @return The fare that the client is required to pay.
    */
    public double getTaxiFare(double pickUpCost, double dropOffCost, String company){
        double taxiFare = 0;
        if (company.equalsIgnoreCase("QnQ")){
            taxiFare = QNQ_BOOKING_FARE + (0.2 * pickUpCost) + dropOffCost;
        } else if (company.equalsIgnoreCase("Shopify")){
            taxiFare = SHOPIFY_BOOKING_FARE + (0.15 * pickUpCost) + dropOffCost;
        }
        return taxiFare;
    }

    /**
     * Determine if the driver will accept or decline the client call.
     * There is a 30% chance of a driver declining the call.
    */
    public static boolean driverAcceptsCall(){
        // Generate a random number between 0 and 1 and determine if the number lies below .
        return Math.random() > 0.3;
    }

    /**
     * Obtain the minimum path to the given destination node.
     * @param startName The name associated with the start node.
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
     * Print a String Representation of this Simulation, showing all vertices and their adjacent vertices.
     * Testing method which prints the structure of the simulation graph.
    */
    private void printGraph(){
        for (int i = 0; i < vertexMap.size(); i++){
            if (vertexMap.size() > 0){
                if (vertexMap.get(Integer.toString(i)).type.equalsIgnoreCase("Client")){
                    System.out.print("Vertex " + i + " (" + vertexMap.get(Integer.toString(i)).type + ") is connected to: ");
                } else {
                    System.out.print("Vertex " + i + " (" + vertexMap.get(Integer.toString(i)).companyName  + " " + vertexMap.get(Integer.toString(i)).type + ") is connected to: ");
                }
                for (int j=0; j<vertexMap.get(Integer.toString(i)).adj.size(); j++){
                    System.out.print(vertexMap.get(Integer.toString(i)).adj.get(j) + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Run the Simulation using the given user input.
     * @param args None.
    */
    public static void main(String[] args){
        // Initialize a new Simulation and process the client calls.
        TaxiSimulator s = new TaxiSimulator();
        StringTokenizer calls = new StringTokenizer(s.calls);

        while(calls.hasMoreTokens()){
            String client = calls.nextToken();
            String company = calls.nextToken();

            System.out.println("client " + client);
            System.out.println("company " + company.toLowerCase());

            ArrayList<Vertex> nearestTaxi = s.findNearestTaxi(client, company);
            ArrayList<Vertex> nearestShop = s.findNearestShop(client, company);
            Boolean driverAcceptsCall = driverAcceptsCall();

            // If no nearby preferred taxi can be found, display that the client cannot be helped.
            if (s.findTaxi(client, company) == null || s.findShop(client, company) == null){
                System.out.println("cannot be helped");
                continue;
            }

            // Display the nearest preferred taxi, assuming that the taxi has been found.
            // Account for the possibility that the driver might decline the call.
            for (Vertex taxi : nearestTaxi){
                if (taxi.duplicatePathsFound){
                    System.out.println("taxi" + " " + taxi.name);
                    if (driverAcceptsCall){
                        System.out.printf("multiple solutions cost %.0f\n", s.getCost(taxi.name));
                    } else {
                        System.out.printf("taxi driver declined the call0\n");
                        break;
                    }
                } else {
                    System.out.println("taxi" + " " + taxi.name);
                    if (driverAcceptsCall){
                        s.displayPath(taxi.name,client);    
                    } else {
                        System.out.printf("taxi driver declined the call :(\n");
                        break;
                    }  
                }   
            }
            // If the driver does not accept the call, stop processing this client.
            if (!driverAcceptsCall){
                continue;
            }

            nearestTaxi = s.findNearestTaxi(client, company);
            nearestShop = s.findNearestShop(client, company);
            // Display the nearest preferred shop, assuming that the shop has been found.
            for (Vertex shop : nearestShop){
                if (shop.duplicatePathsFound){
                    System.out.println("shop" + " " + shop.name);
                    System.out.printf("multiple solutions cost %.0f\n", s.getCost(shop.name));
                } else {
                    System.out.println("shop" + " " + shop.name);
                    s.displayPath(client, shop.name);
                }
            }

            nearestTaxi = s.findNearestTaxi(client, company);
            nearestShop = s.findNearestShop(client, company);
            double fare = 0;
            // Calculate the amount due by this customer, assuming the trip has been completed.
            for (Vertex taxi : nearestTaxi){
                for (Vertex shop : nearestShop){
                    fare += s.getTaxiFare(s.getCost(taxi.name, client), s.getCost(client, shop.name), company);
                }
            }
            System.out.printf("amount due for this client is R%.2f\n", fare);   
        }  
    }
 }