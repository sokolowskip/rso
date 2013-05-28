package bson;

public enum BSONtype {
	DOUBLE(1), STRING(2), EMBEDDED(3), 
	ARRAY(4), OBJECTID(7), BOOL(8), 
	DATE(9), TIMESTAMP(0x11), LONG(0x12),
	NULL(10), INT(0x10), NONE(-1);
	
	int type;
	BSONtype(int type) 
	{
		this.type = type;
	}
	
	public boolean Compare(int id) { return id == type; }
	public static BSONtype fromInt(int id)
	{
		BSONtype[] types = BSONtype.values();
		
		for (int i = 0; i < types.length; i++)
		{
			if (types[i].Compare(id))
				return types[i];
		}
		
		return BSONtype.NONE;
	}
}
