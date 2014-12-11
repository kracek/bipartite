package mscanlib.ms.mass.bipartite;

import java.util.Collection;
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
import mscanlib.ui.plots.MScanGraphMouseManager;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.ViewerListener;
import org.graphstream.ui.swingViewer.ViewerPipe;




public class GraphEngine {

	static public ProPepGraph createProteinsGraph(LinkedHashMap<String,MsMsProteinHit> proteinHash,LinkedHashMap<String,MsMsPeptideHit> peptidesHash)
	{	
		ProPepGraph									graph=null;
		ProPepNode									node=null;
		Edge										edge=null;
		String										edgeId=null;		
		Iterator<Map.Entry<String,MsMsPeptideHit>>	peptideIterator=null;
		Iterator<Map.Entry<String,MsMsProteinHit>>	proteinIterator=null;
		MsMsPeptideHit								peptideHit=null;
		Vector<MsMsProteinHit>						peptideProteinsList=null;
		MsMsProteinHit								proteinHit1=null,proteinHit2=null;
		int											weight=-1;
		

		graph=new ProPepGraph("Proteins graph");
		graph.setAttribute("graph.type","protein");

		/*
		 * Utworzenie wezlow grafu dla kazdego bialka 
		 */
		proteinIterator=proteinHash.entrySet().iterator();
		while (proteinIterator.hasNext())
		{
			if (!(proteinHit1=proteinIterator.next().getValue()).isNA())
			{
				node=graph.addNode(proteinHit1.getId());
				node.setAttribute("protein",proteinHit1);
				node.setAttribute("weight",proteinHit1.getPeptidesCount());
			}
			
		}

		/*
		 * Iterowanie po peptydach i tworzenie krawdzi pomiedzy wezlami, ktorych bialka maja wspolne peptydy.
		 * Kod korzysta z faktu, ze obiekt MsMsPeptideHit przechowuje liste bialek (obiektow MsMsProteinHit),
		 * do ktorych dany peptyd zostal przypisany. 
		 */
		peptideIterator=peptidesHash.entrySet().iterator();
		while (peptideIterator.hasNext())
		{
			peptideHit=peptideIterator.next().getValue();
			
			peptideProteinsList=peptideHit.getProteinsList();
			
			for (int i=0;i<peptideProteinsList.size();i++)
			{
				proteinHit1=peptideProteinsList.get(i);
				for (int j=i+1;j<peptideProteinsList.size();j++)
				{
					proteinHit2=peptideProteinsList.get(j);

					edgeId=GraphEngine.getEdgeId(proteinHit1.getId(),proteinHit2.getId());
					
					/*
					 * Jezeli krawadz istnieje (sprawdzane sa wersje identyfikatora, np. P84243_P68431 i P68431_P84243),
					 * to jej waga zostaje zwiekszona o 1, jezeli nie istnieje, to jest dodawana
					 */
					if ((edge=graph.getEdge(edgeId))!=null)
					{
						weight=(Integer)edge.getAttribute("weight");
						edge.setAttribute("weight",weight+1);
					}
					else if ((edge=graph.getEdge(GraphEngine.getEdgeId(proteinHit2.getId(),proteinHit1.getId())))!=null)
					{
						weight=(Integer)edge.getAttribute("weight");
						edge.setAttribute("weight",weight+1);
					}
					else
					{
						edge=graph.addEdge(edgeId,proteinHit1.getId(),proteinHit2.getId());
						edge.setAttribute("weight",1);
					}
				}
			}
			
			
			
		}
		
		return(graph);
	}
	
	
	static public ProPepGraph createPeptidesGraph(LinkedHashMap<String,MsMsProteinHit> proteinHash,LinkedHashMap<String,MsMsPeptideHit> peptidesHash)
	{	
		ProPepGraph									graph=null;
		ProPepNode									node=null;
		Edge										edge=null;
		String										edgeId=null;		
		Iterator<Map.Entry<String,MsMsPeptideHit>>	peptideIterator=null;
		Iterator<Map.Entry<String,MsMsProteinHit>>	proteinIterator=null;
		MsMsPeptideHit								peptideHit1=null,peptideHit2=null;
		Vector<MsMsPeptideHit>						proteinPeptidesList=null;
		MsMsProteinHit								proteinHit=null;
		int											weight=-1;
		

		graph=new ProPepGraph("Peptides graph");
		graph.setAttribute("graph.type","peptide");

		/*
		 * Utworzenie wezlow grafu dla kazdego peptydu 
		 */
		peptideIterator = peptidesHash.entrySet().iterator();
		while (peptideIterator.hasNext())
		{
			if (!(peptideHit1=peptideIterator.next().getValue()).isNA())
			{
				try{
					node=graph.addNode(peptideHit1.getSequence().toString());
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
				System.out.println(peptideHit1.getSequence().toString());
				node.setAttribute("protein",peptideHit1);
				node.setAttribute("weight",peptideHit1.getProteinsCount());
			}
			
		}
		
		/*
		 * Iterowanie po bialkch i tworzenie krawdzi pomiedzy wezlami, ktorych peptydy sa wspolne.
		 * Kod korzysta z faktu, ze obiekt MsMsProteinHit przechowuje liste peptydow (obiektow MsMsPeptideHit),
		 * ktore skladaja sie na dane bialko. 
		 */
		proteinIterator=proteinHash.entrySet().iterator();
		while (proteinIterator.hasNext())
		{
			proteinHit=proteinIterator.next().getValue();
			
			proteinPeptidesList=proteinHit.getPeptidesList();
			
			for (int i=0;i<proteinPeptidesList.size();i++)
			{
				peptideHit1=proteinPeptidesList.get(i);
				for (int j=i+1;j<proteinPeptidesList.size();j++)
				{
					peptideHit2=proteinPeptidesList.get(j);

					edgeId=GraphEngine.getEdgeId(peptideHit1.getSequence(),peptideHit2.getSequence());
					
					/*
					 * Jezeli krawadz istnieje (sprawdzane sa wersje identyfikatora, np. P84243_P68431 i P68431_P84243),
					 * to jej waga zostaje zwiekszona o 1, jezeli nie istnieje, to jest dodawana
					 */
					if ((edge=graph.getEdge(edgeId))!=null)
					{
						weight=(Integer)edge.getAttribute("weight");
						edge.setAttribute("weight",weight+1);
					}
					else if ((edge=graph.getEdge(GraphEngine.getEdgeId(peptideHit2.getSequence(),peptideHit1.getSequence())))!=null)
					{
						weight=(Integer)edge.getAttribute("weight");
						edge.setAttribute("weight",weight+1);
					}
					else
					{
						edge=graph.addEdge(edgeId,peptideHit1.getSequence(),peptideHit2.getSequence());
						edge.setAttribute("weight",1);
					}
				}
			}
			
			
			
		}
		
		return(graph);
	}
	
	
	/**
	 * Metoda wyswietlajaca graf
	 * 
	 * @param graph	 graf zaleznosci bialko-bialko
	 */
	public static void plotProteinGraph(ProPepGraph graph)
	{
	    System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
	    
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet","url(config\\graph.css)");
		
		
	    Viewer viewer = graph.display();
	    View view = viewer.getDefaultView();
	    viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
	    view.resizeFrame(800, 600);
		view.setMouseManager(new MScanGraphMouseManager(viewer));
	}
	/**
	 * Metoda tworzaca ID krawedzi na podstawie identyfikatorow wezlow
	 * 
	 * @param id1 identyfikator wezla 1
	 * @param id3 identyfikator wezla 2
	 * 
	 * @return identyfikator krawedzi
	 */
	private static String getEdgeId(String id1, String id2)
	{
		StringBuffer edgeId=null;
	
		edgeId=new StringBuffer(id1);
		edgeId.append("_");
		edgeId.append(id2);
		
		return(edgeId.toString());
	}
	
	/**
	 * Metoda odczytujaca plik z opisem eksperymentu
	 * 
	 * @param filename	nazwa pliku
	 * 
	 * @return	obiekt reprezentujacy dane z eksperymentu	
	 * 
	 * @throws MScanException
	 */
	public static Experiment readExperiment(String filename) throws MScanException
	{
		Experiment		experiment=null;
		ExpFileReader	reader=null;
		
		System.out.println("Reading experiment file: " + filename);

		reader=new ExpFileReader(filename,-1,-1);
		reader.readFile();
		experiment=reader.getExperiment();
		
		return(experiment);
	}
	
	public static void main(String[] args)
	{
		Experiment								experiment=null;	//obiekt przechowujacy dane z eksperymentu
		ProPepGraph								peptide_graph=null;			//graf zaleznosci bialko-bialko wynikajacych z liczby wspolnych peptydow
		String filename		 = "C:\\Users\\pawel.kracki\\Desktop\\mgr\\Histonynew\\Histony.exp";
		Sample										mergedSample=null;
		LinkedHashMap<String,MsMsPeptideHit>		peptidesHash=null;
		LinkedHashMap<String,MsMsProteinHit>		proteinHash=null;
		System.out.println("SSDSD");
	
		try{
			/*
			 * Inicjalizacja map potrzebnych do odczytu eksperymentu
			 */
			MassTools.initMaps();
			
			/*
			 * Odczyt eksperymentu
			 */
			if ((experiment=GraphEngine.readExperiment(filename))!=null)
			{
				System.out.println("Creating graph...");
			
				/*
				 * Tworzenie grafu
				 */
				mergedSample=(experiment.getSamples().length>1)?SampleTools.mergeSamples("Merged sample",experiment.getSamples(),null,0.0,0.0):experiment.getSamples()[0];
				proteinHash=mergedSample.getProteins();
				peptidesHash=mergedSample.getPeptides();
				//GraphEngine.plotProteinGraph(protein_graph);
				peptide_graph = GraphEngine.createPeptidesGraph(proteinHash, peptidesHash);
				GraphEngine.plotProteinGraph(peptide_graph);
		}
			
		}catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		
		try{
			ProPepGraph graph =  GraphEngine.createProPepGraph(proteinHash, peptidesHash);
			
		}
		catch (Exception mse)
		{
			System.out.println("ERROR: " + mse.toString());
		}
		}
	 
	/**
	 * Metoda tworzy hashmape rodzin bialek (okiektow MsMsProteinFamily) na podstawie grafu zaleznosci bialek wynikajacych z liczby ich wspolnych peptydow.<br>
	 * <br>
	 * Do rodziny dodawane sa bialka ktore maja dokladnie ten sam zbior peptydow lub zbior peptydow jednego bialka zawiera sie z zbiorze peptydow drugiego.  
	 *  
	 * @param proteinsGraph			graf zaleznosci bialko-bialko (utworzony przez metode {@link mscanlib.ms.mass.families.MsMsProteinFamilyTools#createProteinsGraph})
	 * @param worker				worker (sluzy do aktualizacji paska postepu,moze byc null)
	 * @param minWorkerProgress		minimalna wartosc paska postepu
	 * @param maxWorkerProgress		maksymalna wartosc paska postepu
	 * 
	 * @return	lista rodzin bialek 
	 */
	public static LinkedHashMap<String,MetaProtein> createPeptideFamilies(ProPepGraph proteinsGraph)
	{
		LinkedHashMap<String,MetaProtein>	    metaPeptideHash=null;
		MetaProtein								metaPeptide=null;
		MsMsProteinHit							proteinHit1=null,proteinHit2=null;

		Collection<Node>						nodeSet=null;
		Iterator<Node>							nodeIterator=null;
		Node									node1=null,node2=null;
		Edge									edge=null;
		int										counter=0,node1Weight=0,node2Weight=0,edgeWeight=0,maxWeight=0;
		
		metaPeptideHash=new LinkedHashMap<String,MetaProtein>();

		nodeSet=proteinsGraph.getNodeSet();
		nodeIterator=nodeSet.iterator();
		
		while (nodeIterator.hasNext())
		{
			node1=nodeIterator.next();
			if (!node1.hasAttribute("visited"))
			{
				node1.setAttribute("visited",true);
				
				node1Weight=node1.getAttribute("weight");
				proteinHit1=new MsMsProteinHit((MsMsProteinHit)node1.getAttribute("protein"),true);
				proteinHit1.setScore(node1Weight);
				
				metaPeptide=new MetaProtein(counter++);
				metaPeptideHash.put(metaPeptide.getId(),metaPeptide);
				
				metaPeptide.addProtein(proteinHit1);

				if (node1.getDegree()>0)
				{
					maxWeight=node1Weight;
					for (int i=0;i<node1.getDegree();i++)
					{
						edge=node1.getEdge(i);
						node2=edge.getOpposite(node1);
						
						if (!node2.hasAttribute("visited"))
						{
							node2Weight=node2.getAttribute("weight");
							edgeWeight=edge.getAttribute("weight");
							
							if (node1Weight==edgeWeight || node2Weight==edgeWeight)
							{
								proteinHit2=new MsMsProteinHit((MsMsProteinHit)node2.getAttribute("protein"),true);
								proteinHit2.setScore(node2Weight);
								metaPeptide.addProtein(proteinHit2);
								
								node2.setAttribute("visited",true);
								if (node2Weight>maxWeight)
									maxWeight=node2Weight;
							}
						}
					}
					
					for (int i=0;i<metaPeptide.getProteinCount();i++)
					{
						if (metaPeptide.getProtein(i).getScore()<maxWeight)
							metaPeptide.getProtein(i).setSubset(true);
						metaPeptide.getProtein(i).setScore(0.0);
					}
				}
				
			}
		}

		return(metaPeptideHash);
	}	

static public ProPepGraph createProPepGraph(LinkedHashMap<String,MsMsProteinHit> proteinHash,LinkedHashMap<String,MsMsPeptideHit> peptidesHash){
	Iterator<Map.Entry<String,MsMsProteinHit>>	iterator=null;
	MsMsProteinHit								proteinHit=null;
	MsMsPeptideHit 								peptideHit=null;
	iterator=proteinHash.entrySet().iterator();
	System.out.print("Begin:");
	String styleSheet =
			//"graph {  fill-mode: gradient-vertical; fill-color: purple, blue, green, yellow, orange, red; }" +
		    "node {" +
		    "		size: 19px;" +
			    "}" +
		    "node.protein {" +
		    "		size: 25px;" +
		  	"		fill-color:yellow,orange;" +  
			"		fill-mode:gradient-radial;" +
				"}" +
				"node.peptide {" +
				"		size: 15px;" +
		  	"		fill-color:orange,red;" +  
			"		fill-mode:gradient-radial;" +
				"}" +			  	
		    "node.marked {" +
		    "       fill-color: blue;" +
		    "}";
	System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
	ProPepGraph graph = new ProPepGraph("Proteins");
	graph.setStrict(false);
    graph.setAutoCreate(true);
    graph.addAttribute("ui.stylesheet", styleSheet);
   // graph.display();
    Viewer viewer = graph.display();
    View view = viewer.getDefaultView();
    view.resizeFrame(800, 600);
    //view.getCamera().setViewCenter(440000, 2503000, 0);
    view.getCamera().setViewPercent(0.25);
    
    ViewerPipe fromViewer = viewer.newViewerPipe();
    Clicker clicker = new Clicker(graph);
    fromViewer.addViewerListener(clicker);
    fromViewer.addSink(graph);

    
	while(iterator.hasNext())
	{
		proteinHit=iterator.next().getValue();
		/*
		this.writeLine(writer,"Protein id", proteinHit.getId());
		this.writeLine(writer,"Protein name", proteinHit.getName());
		this.writeLine(writer,"Protein score", String.valueOf(proteinHit.getScore()));
		
		this.writeLine(writer,"Protein experimental peptides:");
		*/
		if (proteinHit.getScore()<300) continue;
	//	System.out.print("\nProteinID: " + proteinHit.getId().toString() + ": ");
		graph.addNode(proteinHit.getId().toString());
		graph.getNode(proteinHit.getId().toString()).setAttribute("ui.label",proteinHit.getId().toString());
		graph.getNode(proteinHit.getId().toString()).setAttribute("ui.class", "protein");
		
		graph.getNode(proteinHit.getId().toString()).setAttribute("ui.color", 0.5);
		for (int i=0;i<proteinHit.getPeptidesCount();i++)
		{
			peptideHit=proteinHit.getPeptide(i);
			
				
				graph.addNode(peptideHit.getSequence().toString());
				graph.getNode(peptideHit.getSequence().toString()).setAttribute("ui.class", "peptide");
				graph.getNode(peptideHit.getSequence().toString()).setAttribute("ui.color", 0.5);
				
			
			
				graph.addEdge(proteinHit.getId().toString() + "-" + 
				peptideHit.getSequence().toString(), proteinHit.getId().toString(), peptideHit.getSequence().toString());
			
			
	//		System.out.print(peptideHit.getSequence().toString() + " ");
			
			//line=new StringBuffer(peptideHit.getSequence());
			//line.append("\t");
			//line.append(peptideHit.getQuery(0).getMascotScore());
			//this.writeLine(writer,line.toString());
		}
		//this.writeLine(writer,"");
	}
	viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
	 while(clicker.loop) {
            fromViewer.pump();
	 }
	return graph;
	}

}

class Clicker implements ViewerListener {
	
    protected boolean loop = true;
    protected Graph graph;
    protected boolean family = false;
    protected Experiment experiment;
    protected String famID;
    
    public Clicker(Graph graph0){
    	this.graph = graph0;
    }
    
    public void viewClosed(String id) {
        loop = false;
    }
 
    public void buttonPushed(String id) {
        System.out.println("Button pushed on node "+id);
        if(this.graph.getNode(id).hasAttribute("ui.label") && ((String)this.graph.getNode(id).getAttribute("ui.class")).equals("peptide"))
        	this.graph.getNode(id).removeAttribute("ui.label");
        else
        	this.graph.getNode(id).setAttribute("ui.label", id);
      /*  if (!family && ((String)this.graph.getNode(id).getAttribute("ui.class")).equals("protein")){
        	Vector<ProteinsFamily>					families=null;
    		HashMap<String,Vector<ProteinsFamily>>	familiesMapping=null;
    		//pobranie i zapis informacji o rodzinach bialek
    				families=experiment.getFamilies();
    				this.famID = new String(id);
    				System.out.println("faimiD: " + famID);
    				//pobranie i zapis mapy id bialka -> rodziny
    				familiesMapping=ProteinsFamiliesTools.getProteins2FamiliesMapping(families);
    				
    				//Iterator<ProteinsFamily>	iterator2=null;
    				//Map.Entry<String,Vector<ProteinsFamily>>			entry=null;		
    				Vector<ProteinsFamily>								list=null;
    				list = familiesMapping.get(id);
    					for (int i=0;i<list.size();i++){
    						System.out.println(list.get(i).mProteinsList.size());
    						for(int j=0;j<list.get(i).mProteinsList.size();j++){
    							System.out.println(list.get(i).mProteinsList.get(j).getId());
    						if (list.get(i).mProteinsList.get(j).getId()!=null)
    						graph.getNode(list.get(i).mProteinsList.get(j).getId()).setAttribute("ui.class", "marked, protein");}
    						}
    					family = true;
    				}
        else if(family && ((String)this.graph.getNode(id).getAttribute("ui.class")).endsWith("protein")){
        	Vector<ProteinsFamily>					families=null;
		HashMap<String,Vector<ProteinsFamily>>	familiesMapping=null;
		//pobranie i zapis informacji o rodzinach bialek
				families=experiment.getFamilies();
				//pobranie i zapis mapy id bialka -> rodziny
				familiesMapping=ProteinsFamiliesTools.getProteins2FamiliesMapping(families);
				
				//Iterator<ProteinsFamily>	iterator2=null;
				//Map.Entry<String,Vector<ProteinsFamily>>			entry=null;		
				Vector<ProteinsFamily>								list=null;
				list = familiesMapping.get(famID);
					for (int i=0;i<list.size();i++){
						System.out.println(list.get(i).mProteinsList.size());
						for(int j=0;j<list.get(i).mProteinsList.size();j++){
							System.out.println(list.get(i).mProteinsList.get(j).getId()); 
						if (list.get(i).mProteinsList.get(j).getId()!=null)
						graph.getNode(list.get(i).mProteinsList.get(j).getId()).setAttribute("ui.class", "protein");}
						}
					family = false;
					if (!famID.equals(id)) this.buttonPushed(id);}
    				//graph.getNode("");   */
     }
        
    
 
    public void buttonReleased(String id) {
        System.out.println("Button released on node "+id);
    }


}
