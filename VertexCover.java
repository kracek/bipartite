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
				if (node.hasAttribute("peptideHit") || node.getDegree() < minDegree) break;
				
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