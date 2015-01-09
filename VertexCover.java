package mscanlib.ms.mass.bipartite;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import mscanlib.ms.msms.MsMsProteinHit;


public class VertexCover {
	ProPepNode node;
	int minDegree = 200;
	public Vector<Vector<MsMsProteinHit>>findCover( ProPepGraph graph, GraphSeparator graphSepar){
		Vector<Vector<MsMsProteinHit>> cover = new Vector<Vector<MsMsProteinHit>>();

		
		Iterator <TreeSet<ProPepNode>> iterator = graphSepar.subGraphList.iterator();
		while (iterator.hasNext()){
			TreeSet<ProPepNode> subGraph = iterator.next();
			Vector<MsMsProteinHit> proteinVector = new Vector<MsMsProteinHit>();
			cover.add(proteinVector);
			Iterator <ProPepNode> vertexIterator = subGraph.iterator();
			while (vertexIterator.hasNext()){
				node = vertexIterator.next();
				System.out.println(node.getDegree());
				
				
				if (node.hasAttribute("peptideHit") || node.getDegree() < minDegree) break;   // drugi warunek potrzebny tylko do wizualizacji
				
				proteinVector.add((MsMsProteinHit) node.getAttribute("proteinHit"));
				graph.removeNode(node);
			}
		}
		
		
		return cover;
	}
	
	TreeSet<ProPepNode> reorder(TreeSet<ProPepNode> oldSet){
		
		TreeSet<ProPepNode> newSet = new TreeSet<ProPepNode>();
		newSet.addAll(oldSet);
		return newSet;
	}
	
	void groupNodes (TreeSet<ProPepNode> currSet, GraphSeparator graphSepar){
		if (currSet.size()<2) return;
		
		
		Iterator <TreeSet<ProPepNode>> iterator = graphSepar.subGraphList.iterator();
		Iterator <ProPepNode> nodeIterator = currSet.iterator();
		ProPepNode node0;
		ProPepNode node1;

		while (nodeIterator.hasNext()){
			node0 = nodeIterator.next();
			node1 = nodeIterator.next();
			if (node0.hasAttribute("peptideHit") || node1.hasAttribute("peptideHit")) break;
			
			if (checkNodeGroup(node0,node1)){
				
				/// join nodes function
				
				Vector<MsMsProteinHit> proteinGroup = new Vector<MsMsProteinHit>();
				proteinGroup.add(node0.getProteinHit());
				proteinGroup.add(node1.getProteinHit());
				
				
				node0.addAttribute("proteinGroup", proteinGroup);
				node0.removeAttribute("proteinHit");
				node1.getGraph().removeNode(node1);
				
				//
			}
		}
	}
	
	boolean checkNodeGroup (ProPepNode node0, ProPepNode node1){
		
		if (node0.hasAttribute("peptideHit") || node1.hasAttribute("peptideHit")) return false;
		
		ProPepNode nodeCmp;
		
		if (node0.getDegree() == node1.getDegree()){
			Iterator <ProPepNode> neighborNodeIterator = node0.getNeighborNodeIterator();
		
			while (neighborNodeIterator.hasNext()){
				nodeCmp = neighborNodeIterator.next();
				if (!node1.hasEdgeBetween(nodeCmp)) {
					return false;
				}
			}
		}
	return true;
}

}


/*
		try{
			graph.write(new FileSinkDGS(), "tmp");
			
			this.temporaryGraph.read("tmp");
			new File("tmp").delete();
			}catch (Exception ex){
				System.out.println("error in write " + ex.toString());
			}
*/