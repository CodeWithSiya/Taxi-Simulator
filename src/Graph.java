import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GraphException( String name )
    {
        super( name );
    }
}

// Represents an edge in the graph.
class Edge
{
    public Vertex     dest;   // Second vertex in Edge
    public double     cost;   // Edge cost
    
    public Edge( Vertex d, double c )
    {
        dest = d;
        cost = c;
    }

    public String toString(){
        return this.dest +  " (Cost = " + this.cost + ")";
    }
}

// Represents an entry in the priority queue for Dijkstra's algorithm.
class Path implements Comparable<Path>
{
    public Vertex     dest;   // w
    public double     cost;   // d(w)
    ArrayList<Vertex> path;
    
    public Path( Vertex d, double c )
    {
        dest = d;
        cost = c;
    }
    
    public int compareTo( Path rhs )
    {
        double otherCost = rhs.cost;
        
        return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
    }

    public String toString(){
        return this.dest + " : " + this.cost;
    }
}

// Represents a vertex in the graph.
class Vertex
{
    public String     name;   // Vertex name
    public List<Edge> adj;    // Adjacent vertices
    public double     dist;   // Cost
    public Vertex     prev;   // Previous vertex on shortest path
    public int        scratch;  // Extra variable used in algorithm
    public String type;    // Variable which stores the type associated with this vertex
    public String companyName; // Variable which stores the Company Name associated with this Vertex. 
    public boolean duplicatePathsFound;  // Variable which stores if duplicate paths to this Vertex have been found.

    public Vertex( String nm ){ 
        this.name = nm; 
        this.adj = new LinkedList<Edge>( ); 
        reset(); 
    }

    public void reset( )
    //  { dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }    
    { dist = Graph.INFINITY; prev = null; scratch = 0;}
      
   // public PairingHeap.Position<Path> pos;  // Used for dijkstra2 (Chapter 23)

    // Return a String representation of this Vertex.
    @Override
    public String toString() {
        return name;
    }

    public int compareTo(Vertex other){
        return Double.compare(dist, other.dist);
    }
}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted
// void dijkstra( String s )    --> Single-source weighted
// void negative( String s )    --> Single-source negative weighted
// void acyclic( String s )     --> Single-source acyclic
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.

public class Graph
{
    public static final double INFINITY = Double.MAX_VALUE;
    public Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );

    /**
     * Add a new edge to the graph.
    */
    public void addEdge( String sourceName, String destName, double cost )
    {
        Vertex v = getVertex( sourceName );
        Vertex w = getVertex( destName );
        v.adj.add( new Edge( w, cost ) );
    }

    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
    */
    public void printPath( String destName )
    {
        Vertex w = vertexMap.get( destName );
        if( w == null )
            throw new NoSuchElementException( "Destination vertex not found.");
        else if( w.dist == INFINITY )
            System.out.println( destName + " is unreachable.");
        else
        {
            System.out.print( "(Cost is: " + w.dist + ") " );
            printPath( w );
            System.out.println( );
        }
    }

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    private Vertex getVertex( String vertexName )
    {
        Vertex v = vertexMap.get( vertexName );
        if( v == null )
        {
            v = new Vertex( vertexName );
            vertexMap.put( vertexName, v );
        }
        return v;
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist.
    */
    private void printPath( Vertex dest )
    {
        if( dest.prev != null )
        {
            printPath( dest.prev );
            System.out.print( " to " );
        }
        System.out.print( dest.name );
    }
    
    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
    */
    private void clearAll( )
    {
        for( Vertex v : vertexMap.values( ) )
            v.reset( );
    }

    /**
     * Single-source weighted shortest-path algorithm (Dijkstra's algorithm).
     * This implementation uses priority queues based on the binary heap.
     * @param startName The name associated with the starting vertex.
     * @throws NoSuchElementException If the starting vertex is not found.
     */
    public void dijkstra( String startName ){
        // Initialize priority queue and obtain the starting vertex.
        PriorityQueue<Path> pq = new PriorityQueue<Path>();
        Vertex start = vertexMap.get( startName );
        // Check if the starting vertex exists.
        if(start == null){
            throw new NoSuchElementException( "Start vertex not found" );
        }
        clearAll();  // Clear all previous information about the vertices in the graph.
        pq.add(new Path(start, 0)); 
        start.dist = 0;
        int nodesSeen = 0;
        // Process the vertices until the priority queue is empty or all vertices have been vistited.
        while(!pq.isEmpty() && nodesSeen < vertexMap.size()){
            // Get the vertex with the shortest distance from the priority queue.
            Path vrec = pq.remove( );
            Vertex v = vrec.dest;
            // Check if the vertex has already been processed.
            if( v.scratch != 0 ){
                continue;   // already processed v
            }   
            // Mark the vertex as processed.    
            v.scratch = 1;
            // Increment the number of nodes seen.
            nodesSeen++;
            // Iterate over the adjacent edges of the current vertex.
            for(Edge e : v.adj){
                Vertex w = e.dest;
                double cvw = e.cost;
                // Check for negative edges.
                if(cvw < 0){
                    throw new GraphException( "Graph has negative edges" );
                }    
                // Relax the edge if the shorter path is found
                if(w.dist > v.dist + cvw){
                    w.dist = v.dist +cvw;
                    w.prev = v;
                    // Add the vertex to the priority queue with the updates distance.
                    pq.add( new Path( w, w.dist ) );
                    // Reset the duplicatePathsFound variable.
                    w.duplicatePathsFound = false;
                }
                // If a duplicate path with the same distance is found, mark this node as having duplicate paths.
                else if (w.dist == v.dist + cvw){
                    w.duplicatePathsFound = true;
                }
            }
        }
    }
}