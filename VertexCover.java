package mscanlib.ms.mass.bipartite;

import java.util.Vector;

import mscanlib.ms.msms.MsMsProteinHit;

/** Klasa realizujace algorytm pokrycia wierzcholkowego
 * @author pawel.kracki
 *
 */
public class VertexCover {


	/** Metoda rozbija graf zgodnie z algorytmem pokrycia wierzcholkow
	 * @param graph graf do rozbicia
	 * @param graphSepar klasa reprezentujaca podgrafy
	 * @return vektor rodzin metabialek
	 */
	@SuppressWarnings("rawtypes")
	public static Vector<Vector<MetaProtein>> findCover(ProPepGraph graph,
			Vector<Vector<ProPepNode>> subGraphVector) {
		
		
		ProPepNode node;
		Vector<Vector<MetaProtein>> cover = new Vector<Vector<MetaProtein>>();

	//	graphSepar.groupNodes();

		int j = 0;
		while (j < subGraphVector.size()) {
			Vector<ProPepNode> subGraph = subGraphVector.get(j);
			
			Vector<MetaProtein> proteinVector = new Vector<MetaProtein>();
			cover.add(proteinVector);
			
			int proteinCount = GraphSeparator.countMetaProteins(subGraph);
			for (int i = 0; i < proteinCount;) {

				node = subGraph.get(0);
				if (node.hasAttribute("peptideHit")) {
					System.out.println("Peptide Found");
					break; // drugi warunek potrzebny tylko do wizualizacji de facto nie powinien tu wchodzic

				}
				// nie dodaje bialek, ktore maja 0 krawedzi po redukcji
				if (node.hasAttribute("proteinHit") && node.getDegree() > 0) {
					proteinVector.add(new MetaProtein((MsMsProteinHit) node
							.getAttribute("proteinHit")));
				}
				else if (node.hasAttribute("proteinGroup") && node.getDegree() > 0) {
					 proteinVector.add((MetaProtein) node.getAttribute("proteinGroup"));
				}else {
					if ((node.hasAttribute("proteinHit") || node.hasAttribute("proteinGroup")) && node.getDegree() == 0) 
						System.out.println("Node not saved: " + node.getId() );
				}

				@SuppressWarnings("unchecked")
				Vector<ProPepNode> set = new Vector(node.neighborMap.keySet());

				for (int k = 0; k < set.size(); k++) {

					ProPepNode node1 = set.get(k);
					// usun peptydy polaczone z bialkiem - krawedzie usuna sie z automatu
					subGraph.remove(node1);

					graph.removeNode(node1);
					
				}
				subGraph.remove(node);
				graph.removeNode(node);
				proteinCount--;
			}
		//	System.out.println("Po processingu: " + graphSepar.countMetaProteins(subGraph) + " , vector: " + proteinVector.size());
			j++;
		}

		return cover;
	}



}