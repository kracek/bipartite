package mscanlib.ms.mass.bipartite;

import java.util.HashMap;

import mscanlib.ms.msms.MsMsPeptideHit;

public class MetaPeptide {

	public final static int ID_LENGTH=6;
	
	private String			mId="";
	private String			mName="";
	public HashMap<String, MsMsPeptideHit>	mPeptideList=null;
	
	public MetaPeptide()
	{
		this.mPeptideList=new HashMap<String, MsMsPeptideHit>();
	}
	
	public MetaPeptide(int id)
	{
		this.mId=this.nr2id(id);
		this.setName();
		this.mPeptideList=new HashMap<String, MsMsPeptideHit>();
	}
	
	private String nr2id(int id)
	{
		StringBuffer	str=null;
		String			numStr=null;
		int				lenDiff=0;

		str=new StringBuffer("MP");
		numStr=String.valueOf(id);
		lenDiff=MetaPeptide.ID_LENGTH-numStr.length();

		for (int i=0;i<lenDiff;i++)
			str.append("0");
		str.append(numStr);

		return(str.toString());
	}
	
	
	public void setName()
	{
		StringBuffer	str=null;

		str=new StringBuffer("MetaPeptide ");
		str.append(this.mId);
		this.mName=str.toString();
	}
	
	/**
	 * Metoda zwracajaca identyfikator metapeptydu
	 *
	 * @return      identyfikator metapaptydu
	 */
	public String getId()
	{
		return(this.mId);
	}
	
	
	public boolean addProtein(MsMsPeptideHit peptide)
	{
		if(this.mPeptideList.containsValue(peptide))
			return(false);
		else
		{
			this.mPeptideList.put(peptide.getSequence().toString(), peptide);
		}
		return(true);
	}
	
	/**
	 * Metoda zwracajaca liczbe peptydow w metapeptydzie
	 *
	 * @return	liczba peptydow
	 */
	public int getPeptideCount()
	{
		return(this.mPeptideList.size());
	}
	
	
	public MsMsPeptideHit getPeptide(int index)
	{
		MsMsPeptideHit currentPeptide=null;

		if (index>=0 && index<this.mPeptideList.size())
			currentPeptide=(MsMsPeptideHit)this.mPeptideList.values().toArray()[index];

		return(currentPeptide);
	}
}
