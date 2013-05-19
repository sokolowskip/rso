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
	
	private Boolean checkCondition(BSONElement<?> condition)
	{
		//TODO napisaæ
		return true;
	}
	
}
