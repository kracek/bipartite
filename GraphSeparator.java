package mscanlib.ms.mass.bipartite;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;


public class GraphSeparator {

	ProPepGraph graph;
	public Vector< TreeSet<ProPepNode> >	subGraphList=null;
	Collection<ProPepNode> nodeSet = null;
	int counter=0;
	
	
	public GraphSeparator(ProPepGraph graph){
		this.graph = graph;
		nodeSet = this.graph.getNodeSet();
		subGraphList = new Vector< TreeSet<ProPepNode> >();
	}
	
	public  void dfs(){
		Iterator<ProPepNode> nodeIterator = nodeSet.iterator();
		
		ProPepNode node;
		
		while (nodeIterator.hasNext()){
			
		node = nodeIterator.next();
		if (!node.hasAttribute("visit")){
			dfs(node); 
			counter++;
		}
		
				//node.getDepthFirstIterator() 
		
		}
		// set blue color
		
		nodeIterator= subGraphList.get(0).iterator();
		
		nodeIterator.next();
		nodeIterator.next();
		nodeIterator.next();
		
		
		while (nodeIterator.hasNext()){
			node = nodeIterator.next();
			String tmp = node.getAttribute("ui.class");
			System.out.println(tmp + " " + node);
		node.setAttribute("ui.class", "marked, " + tmp);
		}
		
	}
	
   public void addNodetoSubgraph(int counter, ProPepNode node){

	  try {subGraphList.get(counter); 
	  }catch (Exception E){

		   subGraphList.add(new TreeSet<ProPepNode> ());
	   }
	   
	   if (!subGraphList.get(counter).contains(node)){

	   subGraphList.get(counter).add(node);
   }
   }
	
	public void dfs(ProPepNode node){
		node.setAttribute("visit", counter);
		this.addNodetoSubgraph(counter, node);
		
		Iterator<ProPepNode> nodeIterator = node.getNeighborNodeIterator();
			
		while (nodeIterator.hasNext()){
			node = nodeIterator.next();
		if (!node.hasAttribute("visit")){
			dfs(node);
		}
		}
		
	}
	
}
