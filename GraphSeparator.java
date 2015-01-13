package mscanlib.ms.mass.bipartite;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

public class GraphSeparator {

	private ProPepGraph graph;
	private ProPepNode node;
	private Vector<TreeSet<ProPepNode>> subGraphList = null;

	public Vector<Vector<ProPepNode>> subGraphVector = null;
	public int counter = 0;

	/**
	 * Konstruktor
	 * 
	 * @param graph
	 *            graf dla ktorego bedziemy tworzyc podgrafy
	 */
	public GraphSeparator(ProPepGraph graph) {
		this.graph = graph;
		subGraphList = new Vector<TreeSet<ProPepNode>>();
		subGraphVector = new Vector<Vector<ProPepNode>>();

	}

	/**
	 * Metoda znajdujaca podgrafy w grafie
	 */
	public void findSubgraphs() {
		Collection<ProPepNode> nodeSet = this.graph.getNodeSet();
		Iterator<ProPepNode> nodeIterator = nodeSet.iterator();

		while (nodeIterator.hasNext()) {

			node = nodeIterator.next();
			if (!node.hasAttribute("visit")) {
				dfs(node);
				counter++;
			}

		}
		Iterator<TreeSet<ProPepNode>> subGraphIterator = subGraphList
				.iterator();
		while (subGraphIterator.hasNext()) {
			subGraphVector.addElement(new Vector<ProPepNode>(subGraphIterator
					.next()));
		}

	}

	/**
	 * metoda implementujaca algorytm przeszukiwania wglab dla danego wezla
	 * startowego
	 * 
	 * @param node
	 *            wezel startowy
	 */
	public void dfs(ProPepNode node) {

		node.addAttribute("visit", counter);
		this.addNodetoSubgraph(counter, node);

		Iterator<ProPepNode> nodeIterator = node.getNeighborNodeIterator();

		while (nodeIterator.hasNext()) {
			node = nodeIterator.next();
			if (!node.hasAttribute("visit")) {
				dfs(node);
			}
		}

	}

	/**
	 * metoda dodaje wezel do subgrafu o podanym id
	 * 
	 * @param counter
	 *            index w vectorze subgrafow
	 * @param node
	 *            dodawany wezel
	 */
	public void addNodetoSubgraph(int counter, ProPepNode node) {
		try {
			subGraphList.get(counter);

		} catch (Exception E) {
			subGraphList.add(new TreeSet<ProPepNode>());
		}

		if (!subGraphList.get(counter).contains(node)) {
			subGraphList.get(counter).add(node);
		}
	}

	/**
	 * metoda grupuje wezly bialkowe w metabialkowe, jezeli maja takie same
	 * sasiedztwo wywoluje metode groupNodes(Vector<Vector<ProPepNode>>
	 * subGraphVector)
	 */
	void groupNodes() {
		this.groupNodes(this.subGraphVector);
	}

	/**
	 * metoda grupuje wezly bialkowe w metabialkowe, jezeli maja takie same
	 * sasiedztwo
	 * 
	 * @param subGraphVector
	 *            vector zawierajacy zbiory wezlow dla podgrafów
	 */
	void groupNodes(Vector<Vector<ProPepNode>> subGraphVector) {
		Vector<ProPepNode> currSet;
		Iterator<Vector<ProPepNode>> iterator = subGraphVector.iterator();

		while (iterator.hasNext()) {
			currSet = iterator.next();
			int nodeCounter = 1;
			ProPepNode node0 = null;
			ProPepNode node1;
			int proteinCount = GraphSeparator.countMetaProteins(currSet);

			for (int j = 0; j < proteinCount; j++) {
				Iterator<ProPepNode> nodeIterator;
				nodeIterator = currSet.iterator();

				for (int i = 0; i < nodeCounter; i++) {

					node0 = nodeIterator.next();
				}
				while (nodeIterator.hasNext()) {
					node1 = nodeIterator.next();
					if (node1.hasAttribute("peptideHit"))
						break;
					if (checkNodeGroup(node0, node1)) {
						addToNode(node0, node1);
						nodeIterator.remove();
						graph.removeNode(node1);
						proteinCount--;
					}
				}
				nodeCounter++;
			}
		}

	}

	/**
	 * Metoda laczaca 2 wezly (meta)bialkowe w wezel metabialkowy
	 * 
	 * @param node0
	 *            pierwszy wezel
	 * @param node1
	 *            drugi wezel
	 */
	void addToNode(ProPepNode node0, ProPepNode node1) {
		// Vector<MsMsProteinHit> proteinGroup;
		MetaProtein metaProtein;
		if (node0.hasAttribute("proteinHit")) {

			// proteinGroup = new Vector<MsMsProteinHit>();
			// proteinGroup.add(node0.getProteinHit());
			// proteinGroup.add(node1.getProteinHit());
			// node0.addAttribute("proteinGroup", proteinGroup);

			metaProtein = new MetaProtein(node0.getProteinHit());
			metaProtein.addProtein(node1.getProteinHit());
			node0.addAttribute("proteinGroup", metaProtein);
			node0.setAttribute("ui.class", "metaProtein");
			node0.removeAttribute("proteinHit");

		} else if (node0.hasAttribute("proteinGroup")) {
			// proteinGroup = node0.getAttribute("proteinGroup");
			// proteinGroup.add(node1.getProteinHit());

			metaProtein = node0.getAttribute("proteinGroup");
			metaProtein.addProtein(node1.getProteinHit());
		}
		// wypadaloby zmienic nazwe noda;
	}

	/**
	 * Funkcja sprawdzajaca czy dwa wezly bialkowe mozna zgrupowac (czy maja
	 * tych samych sasiadow)
	 * 
	 * @param node0
	 *            pierwszy wezel
	 * @param node1
	 *            drugi wezel
	 * @return true jezeli wezly maja takie samo sasiedztwo, false w przeciwnym
	 *         razie
	 */
	boolean checkNodeGroup(ProPepNode node0, ProPepNode node1) {

		if (node0.getDegree() != node1.getDegree()
				|| node0.hasAttribute("peptideHit")
				|| node1.hasAttribute("peptideHit"))
			return false;

		ProPepNode nodeCmp;

		if (node0.getDegree() == node1.getDegree()) {
			Iterator<ProPepNode> neighborNodeIterator = node0
					.getNeighborNodeIterator();

			while (neighborNodeIterator.hasNext()) {
				nodeCmp = neighborNodeIterator.next();
				if (!node1.hasEdgeBetween(nodeCmp)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Funkcja liczaca ilosc bialek w podgrafie (zbiorze wezlow)
	 * 
	 * @param nodeSet
	 *            zbior wezlow podgrafu
	 * @return ilosc wezlow
	 */
	public static int countMetaProteins(Vector<ProPepNode> nodeSet) {
		int counter = 0;

		Iterator<ProPepNode> iterator = nodeSet.iterator();
		ProPepNode node;
		while (iterator.hasNext()) {
			node = iterator.next();
			if (node.hasAttribute("proteinHit")
					|| node.hasAttribute("proteinGroup"))
				counter++;
		}
		return counter;
	}
}
