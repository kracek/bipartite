package mscanlib.ms.mass.bipartite;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import mscanlib.ms.msms.MsMsPeptideHit;
import mscanlib.ms.msms.MsMsProteinHit;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AbstractNode;
import org.graphstream.graph.implementations.AdjacencyListNode;

public class ProPepNode extends AdjacencyListNode implements Comparable<ProPepNode>  {
	protected static class TwoEdges {
		AbstractEdge in, out;
	}
	
	protected HashMap<AbstractNode, TwoEdges> neighborMap;

	// *** Constructor ***

	protected ProPepNode(AbstractGraph graph, String id) {
		super(graph, id);
		neighborMap = new HashMap<AbstractNode, TwoEdges>(
				4 * INITIAL_EDGE_CAPACITY / 3 + 1);
	}

	// *** Helpers ***

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Edge> T locateEdge(Node opposite, char type) {
		TwoEdges ee = neighborMap.get(opposite);
		if (ee == null)
			return null;
		return (T)(type == I_EDGE ? ee.in : ee.out);
	}

	@Override
	protected void removeEdge(int i) {
		AbstractNode opposite = edges[i].getOpposite(this);
		TwoEdges ee = neighborMap.get(opposite);
		char type = edgeType(edges[i]);
		if (type != O_EDGE)
			ee.in = null;
		if (type != I_EDGE)
			ee.out = null;
		if (ee.in == null && ee.out == null)
			neighborMap.remove(opposite);
		super.removeEdge(i);
	}

	// *** Callbacks ***

	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		AbstractNode opposite = edge.getOpposite(this);
		TwoEdges ee = neighborMap.get(opposite);
		if (ee == null)
			ee = new TwoEdges();
		char type = edgeType(edge);
		if (type != O_EDGE) {
			if (ee.in != null)
				return false;
			ee.in = edge;
		}
		if (type != I_EDGE) {
			if (ee.out != null)
				return false;
			ee.out = edge;
		}
		neighborMap.put(opposite, ee);
		return super.addEdgeCallback(edge);
	}

	@Override
	protected void clearCallback() {
		neighborMap.clear();
		super.clearCallback();
	}

	// *** Others ***

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return (Iterator<T>) Collections.unmodifiableSet(neighborMap.keySet())
				.iterator();
	}

	@Override
	public int compareTo(ProPepNode node0) {
		int diff = -1;
		if (this.hasAttribute("proteinHit")) { 
				if ( node0.hasAttribute("proteinHit")) {
		//	diff = -( ((MsMsProteinHit) this.getAttribute("proteinHit")).getPeptidesCount() - ((MsMsProteinHit) node0.getAttribute("proteinHit")).getPeptidesCount() );
					diff = -(  this.getDegree() - node0.getDegree() );
				if ( diff == 0)
					diff = -1;
			}
				return diff; }
		return -diff;
	
	}
	
	public MsMsProteinHit getProteinHit(){
		
		return this.getAttribute("proteinHit");
	}
	
	public MsMsPeptideHit getPeptideHit() throws Exception{
		if (this.hasAttribute("peptideHit"))
		return this.getAttribute("peptideHit");
		else throw new Exception("Wrong class");
	}

}
