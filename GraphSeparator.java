package mscanlib.ms.mass.bipartite;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


public class GraphSeparator {

	ProPepGraph graph;
	public Vector< Vector<ProPepNode> >	subGraphList=null;
	Collection<ProPepNode> nodeSet = null;
	int counter=0;
	
	
	public GraphSeparator(ProPepGraph graph){
		this.graph = graph;
		nodeSet = this.graph.getNodeSet();
		subGraphList = new Vector< Vector<ProPepNode> >();
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
		
		nodeIterator= subGraphList.get(0).iterator();
		while (nodeIterator.hasNext()){
			node = nodeIterator.next();
			String tmp = node.getAttribute("ui.class");
		node.setAttribute("ui.class", "marked, " + tmp);
		}
		
	}
	
   public void addNodetoSubgraph(int counter, ProPepNode node){
	   System.out.println(node.toString() + " 1  " + Integer.toString(counter));
	  try {subGraphList.get(counter); 
	  }catch (Exception E){
		   subGraphList.add(new Vector<ProPepNode> ());
	   }
	   System.out.println(node.toString() + " 2  " + Integer.toString(counter));
	   
	   if (!subGraphList.get(counter).contains(node))
	   subGraphList.get(counter).add(node);
   }
	
	public void dfs(ProPepNode node){
		node.setAttribute("visit", counter);
		System.out.println(node.toString() + " " + Integer.toString(counter));
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
