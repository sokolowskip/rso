package bson;

import java.util.ArrayList;
import java.util.List;

public class BSONDocument {
	List<BSONElement<?>> elems;
	
	public BSONDocument()
	{
		elems = new ArrayList<BSONElement<?>>();
	}
}
