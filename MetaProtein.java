package mscanlib.ms.mass.bipartite;

import java.util.HashMap;

import mscanlib.ms.msms.MsMsProteinHit;

public class MetaProtein {

	public final static int ID_LENGTH=6;
	
	private String			mId="";
	private String			mName="";
	public HashMap<String, MsMsProteinHit>	mProteinsList=null;
	
	public MetaProtein()
	{
		this.mProteinsList=new HashMap<String, MsMsProteinHit>();
	}
	
	public MetaProtein(int id)
	{
		this.mId=this.nr2id(id);
		this.setName();
		this.mProteinsList=new HashMap<String, MsMsProteinHit>();
	}
	
	private String nr2id(int id)
	{
		StringBuffer	str=null;
		String			numStr=null;
		int				lenDiff=0;

		str=new StringBuffer("MB");
		numStr=String.valueOf(id);
		lenDiff=MetaProtein.ID_LENGTH-numStr.length();

		for (int i=0;i<lenDiff;i++)
			str.append("0");
		str.append(numStr);

		return(str.toString());
	}
	
	
	public void setName()
	{
		StringBuffer	str=null;

		str=new StringBuffer("MetaProtein ");
		str.append(this.mId);
		this.mName=str.toString();
	}
	
	/**
	 * Metoda zwracajaca identyfikator rodziny
	 *
	 * @return      identyfikator rodziny
	 */
	public String getId()
	{
		return(this.mId);
	}
	
	
	public boolean addProtein(MsMsProteinHit protein)
	{
		if(this.mProteinsList.containsValue(protein))
			return(false);
		else
		{
//			protein.addFamily(this);
			this.mProteinsList.put(protein.getId(), protein);
		}
		return(true);
	}
	
	/**
	 * Metoda zwracajaca liczbe bialek w rodzinie
	 *
	 * @return	liczba bialek
	 */
	public int getProteinCount()
	{
		return(this.mProteinsList.size());
	}
	
	
	public MsMsProteinHit getProtein(int index)
	{
		MsMsProteinHit currentProtein=null;

		if (index>=0 && index<this.mProteinsList.size())
			currentProtein=(MsMsProteinHit)this.mProteinsList.values().toArray()[index];

		return(currentProtein);
	}
}
