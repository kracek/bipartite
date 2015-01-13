package mscanlib.ms.mass.bipartite;

import java.util.HashMap;
import java.util.Iterator;

import mscanlib.ms.msms.MsMsProteinHit;

/** Klasa reprezentujaca metabialko
 * @author pawel.kracki
 *
 */
public class MetaProtein {

	public final static int ID_LENGTH=6;
	
	private String			mId="";
	private String			mName="";
	public HashMap<String, MsMsProteinHit>	mProteinsMap=null;
	
	/**
	 * Domyslny konstruktor
	 */
	public MetaProtein()
	{
		this.mProteinsMap=new HashMap<String, MsMsProteinHit>();
	}
	
	/** Konstruktor
	 * @param id id metabialka
	 */
	public MetaProtein(int id)
	{
		this.mId=this.nr2id(id);
		this.setName();
		this.mProteinsMap=new HashMap<String, MsMsProteinHit>();
	}
	
	/** Konstruktor
	 * @param proteinHit MsMsProteinHit, który znajduje sie w podanym Metabialku
	 */
	public MetaProtein(MsMsProteinHit proteinHit) {
		
		this.mProteinsMap=new HashMap<String, MsMsProteinHit>();
		this.mProteinsMap.put(proteinHit.getId(), proteinHit);
		
	}

	/** Metoda tworzy identyfikator z liczby
	 * @param id
	 * @return przerobiony identyfikator
	 */
	private String nr2id(int id)
	{
		StringBuffer	str=null;
		String			numStr=null;
		int				lenDiff=0;

		str=new StringBuffer("MP");
		numStr=String.valueOf(id);
		lenDiff=MetaProtein.ID_LENGTH-numStr.length();

		for (int i=0;i<lenDiff;i++)
			str.append("0");
		str.append(numStr);

		return(str.toString());
	}
	
	
	/**
	 *  Metoda dodaje Prefiks do nazwy
	 */
	public void setName()
	{
		StringBuffer	str=null;

		str=new StringBuffer("MetaProtein ");
		str.append(this.mId);
		this.mName=str.toString();
	}
	
	/**
	 * Metoda zwracajaca identyfikator metabialka
	 *
	 * @return      identyfikator metabialka
	 */
	public String getId()
	{
		return(this.mId);
	}
	
	
	/** Metoda dodaje MsMsProteinHit do listy w MetaProtein
	 * @param protein
	 * @return true if ok
	 */
	public boolean addProtein(MsMsProteinHit protein)
	{
		if(this.mProteinsMap.containsValue(protein))
			return(false);
		else
		{
			this.mProteinsMap.put(protein.getId(), protein);
		}
		return(true);
	}
	
	/**
	 * Metoda zwracajaca liczbe bialek w metabialku
	 *
	 * @return	liczba bialek
	 */
	public int getProteinCount()
	{
		return(this.mProteinsMap.size());
	}
	
	
	/** 
	 * Metoda zwraca MsMsProteinHit pod podanym indeksem
	 * @param index
	 * @return
	 */
	public MsMsProteinHit getProtein(int index)
	{
		MsMsProteinHit currentProtein=null;

		if (index>=0 && index<this.mProteinsMap.size())
			currentProtein=(MsMsProteinHit)this.mProteinsMap.values().toArray()[index];

		return(currentProtein);
	}
	
	/** Metoda zwraca nazwe metabialka
	 * @return
	 */
	public String getName() {
		return mName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		Iterator <MsMsProteinHit> iterator = mProteinsMap.values().iterator();
		String list = "";
		while (iterator.hasNext()){
			list = list + ", " + iterator.next().getId();
		}
		return list;
	}
}
