package bson;

import java.util.ArrayList;
import java.util.List;

public class BSONDocument {
	private List<BSONElement<?>> elements;
	
	public BSONDocument()
	{
		elements = new ArrayList<BSONElement<?>>();
	}
	
	public void addElement(BSONElement<?> element){
		elements.add(element);
	}
	
	public List<BSONElement<?>> getAllElements(){
		return elements;
	}
	
	public BSONElement<?> findByName(String name){
		for(BSONElement<?> e: elements)
			if(e.name == name)
				return e;
		return null;
	}
}
