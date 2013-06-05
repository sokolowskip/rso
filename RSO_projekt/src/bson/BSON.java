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
		
		//4 bajty na d³ugoœæ BSONa
		int totalSize = 4;
		for (int i = 0; i < doc.getElems().size(); i++)
		{	
			byteList.add(getBytes(doc.getElems().get(i)));
			totalSize += byteList.get(i).length;
		}
		
		//na zerowy bajt na koncu
		totalSize += 1;
		
		byte[] res = new byte[totalSize];
		byte[] length = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(totalSize).array();
		
		System.arraycopy(length, 0, res, 0, 4);		
		int offset = 4;
		for (int i = 0; i < byteList.size(); i++)
		{
			System.arraycopy(byteList.get(i), 0, res, offset, byteList.get(i).length);
			offset += byteList.get(i).length;
		}
		
		res[res.length - 1] = 0;
		
		return res;
	}
	
	private static byte[] getBytes(BSONElement elem)
	{
		byte[] nameBytes = elem.getName().getBytes();
		
		switch(elem.getType())
		{
			case DOUBLE:
			{
				int offset = 0;
				Double data = (Double)elem.data;
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + nameBytes.length + 1 + 8];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				System.arraycopy(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(data).array(), 0, temp, offset, 8);
				
				return temp;
			}
			case STRING:
			{
				int offset = 0;
				String data = (String)elem.getData();
				byte[] dataBytes = data.getBytes();
				
				//bajty na: typ, nazwê, zerowy, d³ugoœæ stringa, string, zerowy
				byte[] temp = new byte[1 + nameBytes.length + 1 + 4 + dataBytes.length + 1];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				//dodajemy +1 na zerowy bajt
				byte[] length = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataBytes.length + 1).array();				
				System.arraycopy(length, 0, temp, offset, 4);	
				offset += 4;
				
				System.arraycopy(dataBytes, 0, temp, offset, dataBytes.length);
				offset += dataBytes.length;
				temp[offset] = 0;
				
				return temp;
			}
			case EMBEDDED:
			{
				int offset = 0;
				BSONDocument data = (BSONDocument)elem.data;
				byte[] dataBytes = getBSON(data);
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + dataBytes.length + 1 + dataBytes.length];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				System.arraycopy(dataBytes, 0, temp, offset, dataBytes.length);
				
				return temp;
			}
			case ARRAY:
			{
				int offset = 0;
				ArrayList<BSONElement<?>> data = (ArrayList<BSONElement<?>>)elem.data;
				ArrayList<byte[]> dataBytesArr = new ArrayList<byte[]>();
				
				int length = 4;
				for (int i = 0; i < data.size(); i++)
				{
					dataBytesArr.add(getBytes(data.get(i)));
					length += dataBytesArr.get(i).length;
				}
				
				int offset2 = 0;
				//dodatkowy bajt na zero na koñcu
				byte[] dataBytes = new byte[length + 1];
				
				System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(length).array(), 0, dataBytes, offset2, 4);
				offset2 += 4;
				
				for (int i = 0; i < dataBytesArr.size(); i++)
				{
					System.arraycopy(dataBytesArr.get(i), 0, dataBytes, offset2, dataBytesArr.get(i).length);
					offset2 += dataBytesArr.get(i).length;
				}
				
				dataBytes[dataBytes.length - 1] = 0;
				
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + dataBytes.length + 1 + dataBytes.length];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				System.arraycopy(dataBytes, 0, temp, offset, dataBytes.length);
				
				return temp;
			}
			case OBJECTID:
			{
				int offset = 0;
				ObjectID data = (ObjectID)elem.data;
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + nameBytes.length + 1 + 12];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				//te wartoœci nie s¹ de facto intami wiêc nie mo¿na u¿yæ ByeBuffera
				temp[offset] = (byte)((data.time >> 24) & 0xFF);
				temp[offset + 1] = (byte)((data.time >> 16) & 0xFF);
				temp[offset + 2] = (byte)((data.time >> 8) & 0xFF);
				temp[offset + 3] = (byte)((data.time >> 0) & 0xFF);
				offset += 4;

				temp[offset] = (byte)((data.machine >> 16) & 0xFF);
				temp[offset + 1] = (byte)((data.machine >> 8) & 0xFF);
				temp[offset + 2] = (byte)((data.machine >> 0) & 0xFF);
				offset += 3;
				
				temp[offset + 1] = (byte)((data.procID >> 8) & 0xFF);
				temp[offset + 2] = (byte)((data.procID >> 0) & 0xFF);
				offset += 2;	
				
				temp[offset] = (byte)((data.counter >> 16) & 0xFF);
				temp[offset + 1] = (byte)((data.counter >> 8) & 0xFF);
				temp[offset + 2] = (byte)((data.counter >> 0) & 0xFF);
				offset += 3;

				return temp;
			}
			case BOOL:
			{
				int offset = 0;
				Boolean data = (Boolean)elem.data;
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + nameBytes.length + 1 + 1];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				if (data)
					temp[offset] = 1; 
				else
					temp[offset] = 0;
				
				return temp;
			}
			case DATE:
			case TIMESTAMP:
			case LONG:
			{
				int offset = 0;
				Long data = (Long)elem.data;
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + nameBytes.length + 1 + 8];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				System.arraycopy(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array(), 0, temp, offset, 8);
				
				return temp;
			}
			case NULL:
			{
				//bajty na: typ, nazwê, zerowy
				byte[] temp = new byte[1 + nameBytes.length + 1];
				
				insertNameType(temp, elem.getType(), nameBytes);
				
				return temp;	
			}
			case INT:
			{
				int offset = 0;
				Integer data = (Integer)elem.data;
				//bajty na: typ, nazwê, zerowy, dane
				byte[] temp = new byte[1 + nameBytes.length + 1 + 4];
				
				offset += insertNameType(temp, elem.getType(), nameBytes);
				
				System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array(), 0, temp, offset, 4);
				
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
					String n = ""; 
					while (data[index2] != 0)
					{
						n += (char)data[index2]; 
						index2++;
					}
					index2++;
					
					index2 = parse(curType, data, index2, temp.data, n);
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
				temp.data.time = ((data[index] & 0xFF) << 24) + ((data[index + 1] & 0xFF) << 16) + ((data[index + 2] & 0xFF) << 8) + (data[index + 3] & 0xFF);//ByteBuffer.wrap(data).getInt(index);
				index += 4;
				temp.data.machine = ((data[index] & 0xFF) << 16) + ((data[index + 1] & 0xFF) << 8) + (data[index + 2] & 0xFF);
				index += 3;
				temp.data.procID = ((data[index] & 0xFF) << 8) + (data[index + 1] & 0xFF);
				index += 2;	
				temp.data.counter = ((data[index] & 0xFF) << 16) + ((data[index + 1] & 0xFF) << 8) + (data[index + 2] & 0xFF);
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
	
	private static int insertNameType(byte[] arr, BSONtype type,  byte[] nameBytes)
	{
		int offset = 0;
		arr[0] = (byte)type.type;
		offset = 1;
		
		System.arraycopy(nameBytes, 0, arr, offset, nameBytes.length);
		offset += nameBytes.length;
		arr[offset] = 0;
		offset++;
		
		return offset;
	}
}
