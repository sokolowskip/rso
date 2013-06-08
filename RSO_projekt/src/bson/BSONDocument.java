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
	
	public boolean addElem(BSONElement<?> element)
	{
		return elems.add(element);
	}
	
	public List<BSONElement<?>> getElems() {
		return elems;
	}

	public void setElems(List<BSONElement<?>> elems) {
		this.elems = elems;
	}

	
}
