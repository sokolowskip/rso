package bson;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public abstract class BSON 
{
	public static byte[] getBSON(BSONDocument doc)
	{
		List<byte[]> byteList = new ArrayList<byte[]>();
		
		for (int i = 0; i < doc.getElems().size(); i++)
		{	
			byteList.add(getBytes(doc.getElems().get(i)));
		}
		return byteList.get(0);
	}
	
	private static byte[] getBytes(BSONElement elem)
	{
		switch(elem.getType())
		{
			case OBJECTID:
			{
				byte[] temp = new byte[1 + elem.getName().getBytes().length + 1 + 12];
				temp[0] = 0x07;
				
				//System.arraycopy(arg0, arg1, arg2, arg3, arg4)
				return temp;
			}
			
			default:
				return new byte[0];
		}
	}
	
	public static int parseBSON(byte[] data, BSONDocument doc)
	{
		ByteBuffer wrapper = ByteBuffer.wrap(data, 0, 4);
		wrapper.order(ByteOrder.LITTLE_ENDIAN);
		int len = wrapper.getInt();
		int index = 4;
		
		try
		{			
			while (true)
			{
				int type = data[index]; 
				index++;
				
				int index2 = index;
				while(data[index2] != 0)
					index2++;
				
				String name = new String(data, index, index2 - index, "UTF-8");
				index = index2 + 1;
				
				index = parse(type, data, index, doc.elems, name);
				
				if (index >= len - 1)
					break;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			
		}

		return index;
	}
	
	private static int parse(int type, byte[] data, int index, List<BSONElement<?>> docs, String name) throws UnsupportedEncodingException
	{		
		BSONtype bsonType = BSONtype.fromInt(type);
		
		switch (bsonType)
		{
			//double
			case DOUBLE:
			{
				BSONElement<Double> temp = new BSONElement<Double>();
				temp.type = bsonType;
				temp.name = name;
				
				ByteBuffer wrapper = ByteBuffer.wrap(data, index, 8);
				wrapper.order(ByteOrder.LITTLE_ENDIAN);
				temp.data = wrapper.getDouble();
				
				index += 8;
				
				docs.add(temp);
				break;
			}
			//string
			case STRING:
			{
				BSONElement<String> temp = new BSONElement<String>();
				temp.type = bsonType;
				temp.name = name;
				
				ByteBuffer wrapper = ByteBuffer.wrap(data, index, 4);
				wrapper.order(ByteOrder.LITTLE_ENDIAN);
				int curLen = wrapper.getInt();
				
				index += 4;
				
				temp.data = new String(data, index, curLen - 1, "UTF-8");
				index += curLen;
				
				docs.add(temp);
				break;
			}
			//document (embedded)
			case EMBEDDED:
			{
				BSONElement<BSONDocument> temp = new BSONElement<BSONDocument>();
				temp.data = new BSONDocument();
				temp.type = bsonType;
				temp.name = name;
				
				byte[] data2 = Arrays.copyOfRange(data, index, data.length);
				int offset = parseBSON(data2, temp.data);
				index += offset + 1;
				
				docs.add(temp);
				break;
			}
			//document (array)
			case ARRAY:
			{
				BSONElement<ArrayList<BSONElement<?>>> temp = new BSONElement<ArrayList<BSONElement<?>>>();
				temp.data = new ArrayList<BSONElement<?>>();
				temp.type = bsonType;
				temp.name = name;
				
				int index2 = index;				
				ByteBuffer wrapper = ByteBuffer.wrap(data, index, 4);
				wrapper.order(ByteOrder.LITTLE_ENDIAN);
				int curLen = wrapper.getInt();
				index2 += 4;				
				
				while (index2 < index + curLen - 1)
				{
					int curType = data[index2]; 
					index2++;

					//czytanie indeksu w tablicy zapisanego w postaci stringa (indeks nie jest nigdzie u¿ywany)
					while (data[index2] != 0)
					{
						index2++;
					}
					index2++;
					
					index2 = parse(curType, data, index2, temp.data, "elem");
				}
				index2++;
				
				index = index2;
				docs.add(temp);
				break;
			}
			//ObjectId
			case OBJECTID:	
			{
				BSONElement<ObjectID> temp = new BSONElement<ObjectID>();
				temp.data = new ObjectID();
				temp.type = bsonType;
				temp.name = name;
				
				//nie u¿ywam bytebuffera poniewa¿ niektóre pola maj¹ po 3 bajty i nie odpowiadaj¹ ¿adnemu typowi z bytebuffera
				temp.data.time = (data[index] << 24) + (data[index + 1] << 16) + (data[index + 2] << 8) + data[index + 3];
				index += 4;
				temp.data.machine = (data[index] << 16) + (data[index + 1] << 8) + data[index + 2];
				index += 3;
				temp.data.procID = (data[index] << 8) + data[index + 1];
				index += 2;	
				temp.data.counter = (data[index] << 16) + (data[index + 1] << 8) + data[index + 2];
				index += 3;
				
				docs.add(temp);
				break; 
			}
			//Boolean
			case BOOL:
			{
				BSONElement<Boolean> temp = new BSONElement<Boolean>();
				temp.name = name;
				temp.type = bsonType;
				
				if (data[index] == 1)
					temp.data = true;
				else
					temp.data = false;
				index++;
				
				docs.add(temp);
				break;
			}
			//UTC datetime
			case DATE:
			//Timestamp
			case TIMESTAMP:
			//64-bit integer
			case LONG:
			{
				BSONElement<Long> temp = new BSONElement<Long>();			
				temp.name = name;
				temp.type = bsonType;
				
				ByteBuffer wrapper = ByteBuffer.wrap(data, index, 8);
				wrapper.order(ByteOrder.LITTLE_ENDIAN);
				temp.data = wrapper.getLong();
				
				index += 8;
				
				docs.add(temp);
				break;	
			}
			//Null Value
			case NULL:
			{
				BSONElement<Byte> temp = new BSONElement<Byte>();			
				temp.name = name;
				temp.type = bsonType;
				temp.data = null;
				
				docs.add(temp);
				break;	
			}
			//32-bit Integer
			case INT:
			{
				BSONElement<Integer> temp = new BSONElement<Integer>();			
				temp.name = name;
				temp.type = bsonType;
				
				ByteBuffer wrapper = ByteBuffer.wrap(data, index, 4);
				wrapper.order(ByteOrder.LITTLE_ENDIAN);
				temp.data = wrapper.getInt();
				index += 4;
				
				docs.add(temp);
				break;	
			}
			case NONE:
				break;
		}
		
		return index;
	}
}
