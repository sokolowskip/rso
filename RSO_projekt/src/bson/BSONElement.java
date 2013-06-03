package bson;

public class BSONElement<T> 
{
	String name;
	T data;
	BSONtype type;
	
	public BSONtype getType() {
		return type;
	}

	public void setType(BSONtype type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	public BSONElement() 
	{
		name = "";
	}
}