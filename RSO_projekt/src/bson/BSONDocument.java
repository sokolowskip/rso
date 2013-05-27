package bson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BSONDocument {
	List<BSONElement<?>> elems;
	
	public BSONDocument()
	{
		elems = new ArrayList<BSONElement<?>>();
	}
	
	public Boolean checkQuery(BSONDocument query)
	{
		Iterator<BSONElement<?>> iterator = query.elems.iterator();
		
		while(iterator.hasNext())
		{
			BSONElement<?> queryElement = iterator.next();
			if(!checkCondition(queryElement))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public List<BSONElement<?>> getElems() {
		return elems;
	}

	public void setElems(List<BSONElement<?>> elems) {
		this.elems = elems;
	}

	private Boolean checkCondition(BSONElement<?> condition)
	{
		//TODO napisaæ
		return true;
	}
	
}
