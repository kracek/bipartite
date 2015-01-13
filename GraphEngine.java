package mscanlib.ms.mass.bipartite;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import mscanlib.MScanException;
import mscanlib.ms.exp.Experiment;
import mscanlib.ms.exp.Sample;
import mscanlib.ms.exp.SampleTools;
import mscanlib.ms.exp.io.ExpFileReader;
import mscanlib.ms.mass.MassTools;
import mscanlib.ms.msms.MsMsPeptideHit;
import mscanlib.ms.msms.MsMsProteinHit;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.ViewerListener;
import org.graphstream.ui.swingViewer.ViewerPipe;

public class GraphEngine {

	Experiment experiment = null; // obiekt przechowujacy dane z eksperymentu
	ProPepNode node;

	String filename = "C:\\Users\\pawel.kracki\\Desktop\\mgr\\Histonynew\\Histony.exp";
	Sample mergedSample = null;
	LinkedHashMap<String, MsMsProteinHit> proteinHash = null;

	/**
	 * Metoda tworzaca ID krawedzi na podstawie identyfikatorow wezlow
	 * 
	 * @param id1
	 *            identyfikator wezla 1
	 * @param id3
	 *            identyfikator wezla 2
	 * 
	 * @return identyfikator krawedzi
	 */
	private static String createEdgeId(String id1, String id2) {
		StringBuffer edgeId = null;

		edgeId = new StringBuffer(id1);
		edgeId.append("_");
		edgeId.append(id2);

		return (edgeId.toString());
	}

	/**
	 * Metoda odczytujaca plik z opisem eksperymentu
	 * 
	 * @param filename
	 *            nazwa pliku
	 * 
	 * @return obiekt reprezentujacy dane z eksperymentu
	 * 
	 * @throws MScanException
	 */
	public static Experiment readExperiment(String filename)
			throws MScanException {
		Experiment experiment = null;
		ExpFileReader reader = null;

		System.out.println("Reading experiment file: " + filename);

		reader = new ExpFileReader(filename, -1, -1);
		reader.readFile();
		experiment = reader.getExperiment();

		return (experiment);
	}

	/**
	 * Konstruktor
	 */
	public GraphEngine() {

		try {
			/*
			 * Inicjalizacja map potrzebnych do odczytu eksperymentu
			 */
			MassTools.initMaps();

			/*
			 * Odczyt eksperymentu
			 */
			if ((experiment = GraphEngine.readExperiment(filename)) != null) {
				System.out.println("Creating graph...");

				mergedSample = (experiment.getSamples().length > 1) ? SampleTools
						.mergeSamples("Merged sample", experiment.getSamples(),
								null, 0.0, 0.0) : experiment.getSamples()[0];
				proteinHash = mergedSample.getProteins();

			}

			
			/*
			 * Tworzenie grafu
			 */
			ProPepGraph graph = GraphEngine.createProPepGraph(proteinHash);
			
			System.out.println("Nodes after graph creation: "
					+ graph.getNodeCount());
			
			GraphSeparator graphSepar = new GraphSeparator(graph);
			/*
			 * Wyszukiwanie podgrafów
			 */
			graphSepar.findSubgraphs();
			System.out.println ("ilosc podgrafow: " + (graphSepar.subGraphVector.size()));
			graphSepar.groupNodes();
			
			System.out.println("Nodes after groupping: "
					+ graph.getNodeCount());
			
			Iterator<Vector<ProPepNode>> ieee = graphSepar.subGraphVector.iterator();
			while (ieee.hasNext()){
				Vector<ProPepNode> vect = ieee.next();
				Iterator<ProPepNode> iooo = vect.iterator();
			//	System.out.println(GraphSeparator.countMetaProteins(vect));
				while (iooo.hasNext()){
					ProPepNode node = iooo.next();
					if (node.hasAttribute("proteinGroup")) {
				//		System.out.println("Dodatek: " + (((MetaProtein) node.getAttribute("proteinGroup")).getProteinCount()-1));
					}
				}
	
			}
			
			/*
			 * Pokrycie wierzcholkow
			 */
			
			Vector<Vector<MetaProtein>> secik = VertexCover.findCover(graph, graphSepar.subGraphVector);
			
			
			Iterator <Vector<MetaProtein>> itek = secik.iterator();
			int a=0, b=0, c=0, d=0;;
			while (itek.hasNext()){
				Vector <MetaProtein> vect = itek.next();
			//System.out.println(vect.size());
				Iterator <MetaProtein> itek2 = vect.iterator();
				while (itek2.hasNext()){
				MetaProtein meta = itek2.next();
			//	System.out.println(" takie " + a + ", " + b + ": " + meta.toString() + "\t " + vect.size());
				b++;
				d=d+meta.getProteinCount();
				//System.out.println(a + ": " + meta.getProteinCount());
				}
				a++;
				c=c+vect.size();
			}
			System.out.println ("ogolna liczba metabialek po processingu: " + c + " bialek: " + d + " z podgrafow: " + a);
			
			System.out.println("Nodes count before plot: "					+ graph.getNodeCount());
			//GraphEngine.plotGraph(graph);
			GraphEngine.plotGraph(graph);
		} catch (Exception mse) {
			System.out.println("ERROR: " + mse.toString());
		}

	};

	public static void main(String[] args) {
		new GraphEngine();
	}

	static public ProPepGraph createProPepGraph(
			LinkedHashMap<String, MsMsProteinHit> proteinHash) {
		Iterator<Map.Entry<String, MsMsProteinHit>> iterator = null;
		MsMsProteinHit proteinHit = null;
		MsMsPeptideHit peptideHit = null;
		iterator = proteinHash.entrySet().iterator();
		System.out.print("Begin:");

		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		ProPepGraph graph = new ProPepGraph("Proteins");
		graph.setStrict(false);
		// graph.setAutoCreate(true);

		while (iterator.hasNext()) {
			proteinHit = iterator.next().getValue();

			if (proteinHit.getScore() == 0)
				continue;
			graph.addNode(proteinHit.getId().toString());
			graph.getNode(proteinHit.getId().toString()).setAttribute(
					"ui.label", proteinHit.getId().toString());
			graph.getNode(proteinHit.getId().toString()).setAttribute(
					"ui.class", "protein");
			graph.getNode(proteinHit.getId().toString()).addAttribute(
					"proteinHit", proteinHit);
			graph.getNode(proteinHit.getId().toString()).setAttribute(
					"ui.color", 0.5);

			for (int i = 0; i < proteinHit.getPeptidesCount(); i++) {
				peptideHit = proteinHit.getPeptide(i);

				graph.addNode(peptideHit.getSequence().toString());
				graph.getNode(peptideHit.getSequence().toString())
						.setAttribute("ui.class", "peptide");
				graph.getNode(peptideHit.getSequence().toString())
						.setAttribute("ui.color", 0.5);
				graph.getNode(peptideHit.getSequence().toString())
						.addAttribute("peptideHit", peptideHit);

				graph.addEdge(
						createEdgeId(proteinHit.getId().toString(), peptideHit
								.getSequence().toString()), proteinHit.getId()
								.toString(), peptideHit.getSequence()
								.toString());
			}

		}

		return graph;
	}

	static public void plotGraph(ProPepGraph graph) {
		String styleSheet =
		// "graph {  fill-mode: gradient-vertical; fill-color: purple, blue, green, yellow, orange, red; }"
		// +
		"node {" + "	size: 19px;" + "}" + "node.protein {" + "		size: 25px;"
				+ "		fill-color:yellow,orange;"
				+ "		fill-mode:gradient-radial;" + "}" + "node.peptide {"
				+ "		size: 15px;" + "		fill-color:orange,red;"
				+ "		fill-mode:gradient-radial;" + "}" + "node.metaProtein {"
				+ "		size: 25px;" + "		fill-color:green,blue;"
				+ "		fill-mode:gradient-radial;" + "}" + "node.marked {"
				+ "       fill-color: blue,green;"
				+ "		fill-mode:gradient-radial;" + "}";

		graph.addAttribute("ui.stylesheet", styleSheet);

		Viewer viewer = graph.display();
		View view = viewer.getDefaultView();
		view.resizeFrame(800, 600);
		view.getCamera().setViewPercent(0.25);

		ViewerPipe fromViewer = viewer.newViewerPipe();
		Clicker clicker = new Clicker(graph);
		fromViewer.addViewerListener(clicker);
		fromViewer.addSink(graph);

		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);
		while (clicker.loop) {
			fromViewer.pump();
		}
	}

}

class Clicker implements ViewerListener {

	protected boolean loop = true;
	protected Graph graph;

	public Clicker(Graph graph0) {
		this.graph = graph0;
	}

	public void viewClosed(String id) {
		loop = false;
	}

	public void buttonPushed(String id) {
		System.out.println("Button pushed on node " + id);
		if (this.graph.getNode(id).hasAttribute("ui.label")
				&& ((String) this.graph.getNode(id).getAttribute("ui.class"))
						.equals("peptide"))
			this.graph.getNode(id).removeAttribute("ui.label");
		else
			this.graph.getNode(id).setAttribute("ui.label", id);

	}

	public void buttonReleased(String id) {
		System.out.println("Button released on node " + id);
	}

}
