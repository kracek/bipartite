package mscanlib.ms.mass.bipartite;

import org.graphstream.graph.Graph;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class ProPepGraph extends AdjacencyListGraph {

	/**
	 * Tworzy pusty graf.
	 * 
	 * @param id
	 *            Unikalny identyfikator grafu.
	 * @param strictChecking
	 *            If true zaden non-fatal error nie rzuca wyjatkiem.
	 * @param autoCreate
	 *            If true (and strict checking is false), wezly sa
	 *            automatycznie tworzone kiedy tworzona krawedz sie do niego odnosi,
	 *            nawet jezeli nie sa jeszcze wstawione w graf.
	 * @param initialNodeCapacity
	 *            Poczatkowa pojemnosc struktur danych dla wezlow. Uzyj tego
	 * 			  jesli znasz przyblizona maksymalna liczbe wezlow
	 *  		  grafu.Graf moze sie rozwijac poza tym limitem, ale realokacja 
	 *			  jest kosztowna operacja.
	 * @param initialEdgeCapacity
	 *            Poczatkowa pojemnosc struktur danych dla krawedzi. Uzyj tego
	 * 			  jesli znasz przyblizona maksymalna liczbe krawedzi
	 *  		  grafu.Graf moze sie rozwijac poza tym limitem, ale realokacja 
	 *			  jest kosztowna operacja.
	 */
	public ProPepGraph(String id, boolean strictChecking, boolean autoCreate,
			int initialNodeCapacity, int initialEdgeCapacity) {
		super(id, strictChecking, autoCreate, initialNodeCapacity,
				initialEdgeCapacity);
		// All we need to do is to change the node factory
		this.setNodeFactory(new NodeFactory<ProPepNode>() {
			public ProPepNode newInstance(String id, Graph graph) {
				return new ProPepNode((AbstractGraph) graph, id);
			}
		});
	}

	/**
	 * Tworzy pusty graf z domyslna pojemnoscia krawedzi i wezlow.
	 * 
	 * @param id
	 *            Unikalny identyfikator grafu.
	 * @param strictChecking
	 *            If true zaden non-fatal error nie rzuca wyjatkiem.
	 * @param autoCreate
	 *            If true (and strict checking is false), wezly sa
	 *            automatycznie tworzone kiedy tworzona krawedz sie do niego odnosi,
	 *            nawet jezeli nie sa jeszcze wstawione w graf.
	 */
	public ProPepGraph(String id, boolean strictChecking, boolean autoCreate) {
		this(id, strictChecking, autoCreate, DEFAULT_NODE_CAPACITY,
				DEFAULT_EDGE_CAPACITY);
	}

	/**
	 * Tworzy pusty graf z strict checking i bez auto-creation.
	 * 
	 * @param id
	 *            Unikalny identyfikator grafu.
	 */
	public ProPepGraph(String id) {
		this(id, true, false);
	}

}
