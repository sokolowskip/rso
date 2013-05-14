package bson;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class BSON 
{
	
	public static int parseBSON(char[] data, BSONDocument doc)
	{
		int len = data[0] + (data[1] << 8) + (data[2] << 16) + (data[3] << 24);
	
		
		int index = 4;
		while (true)
		{
			int type = data[index]; 
			index++;
			
			String name = "";
			while(data[index] != 0)
			{
				name += data[index];
				index++;
			}
			index++;
			
			index = process(type, data, index, doc.elems, name);
			
			if (index >= len - 1)
				break;
		}
		
		return index;
	}
	
	private static int process(int type, char[] data, int index, List<BSONElement<?>> docs, String name)
	{		
		switch (type)
		{
			case 0x01:
			{
				BSONElement<Double> temp = new BSONElement<Double>();
				temp.name = name;
				
				long temp2 = 0;
				for (int i = 0; i < 8; i++)
					temp2 += ((long)data[index + i] << 8 * i);				
				index += 8;				
				
				temp.data = Double.longBitsToDouble(temp2);
				docs.add(temp);
				break;
			}
			case 0x02:
			{
				BSONElement<String> temp = new BSONElement<String>();
				temp.data = "";
				temp.name = name;
				
				int curLen = data[index] + (data[index + 1] << 8) + (data[index + 2] << 16) + (data[index + 3] << 24);
				index += 4;
				
				for (int i = 0; i < curLen - 1; i++)
				{
					temp.data += data[index];
					index++;
				}
				index++;
				
				docs.add(temp);
				break;
			}
			case 0x03:
			{
				BSONElement<BSONDocument> temp = new BSONElement<BSONDocument>();
				temp.data = new BSONDocument();
				temp.name = name;
				
				char[] data2 = Arrays.copyOfRange(data, index, data.length);
				int offset = parseBSON(data2, temp.data);
				index += offset + 1;
				
				docs.add(temp);
				break;
			}
			case 0x04:
			{
				BSONElement<ArrayList<BSONElement<?>>> temp = new BSONElement<ArrayList<BSONElement<?>>>();
				temp.data = new ArrayList<BSONElement<?>>();
				temp.name = name;
				
				int index2 = index;				
				int len = data[index2] + (data[index2 + 1] << 8) + (data[index2 + 2] << 16) + (data[index2 + 3] << 24);
				index2 += 4;				
				
				while (index2 < index + len - 1)
				{
					int curType = data[index2]; 
					index2++;
					String n = "";
					while (data[index2] != 0)
					{
						n += data[index2];
						index2++;
					}
					index2++;
					index2 = process(curType, data, index2, temp.data, "elem");
				}
				index2++;
				
				index = index2;
				docs.add(temp);
				break;
			}
			case 0x07:	
			{
				BSONElement<ObjectID> temp = new BSONElement<ObjectID>();
				temp.data = new ObjectID();
				temp.name = name;
				
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
			case 0x08:
			{
				BSONElement<Boolean> temp = new BSONElement<Boolean>();
				temp.name = name;
				
				if (data[index] == 1)
					temp.data = true;
				else
					temp.data = false;
				index++;
				
				docs.add(temp);
				break;
			}
			case 0x09:
			case 0x11:
			case 0x12:
			{
				BSONElement<Long> temp = new BSONElement<Long>();			
				temp.name = name;
				
				temp.data = (long)0;
				for (int i = 0; i < 8; i++)
					temp.data += ((long)data[index + i] << 8 * i);
				index += 8;
				
				docs.add(temp);
				break;	
			}
			case 0x0A:
			{
				BSONElement<Byte> temp = new BSONElement<Byte>();			
				temp.name = name;
				temp.data = null;
				
				docs.add(temp);
				break;	
			}
			case 0x10:
			{
				BSONElement<Integer> temp = new BSONElement<Integer>();			
				temp.name = name;
				
				temp.data = data[index] + (data[index + 1] << 8) + (data[index + 2] << 16) + (data[index + 3] << 24);
				index += 4;
				
				docs.add(temp);
				break;	
			}
		}
		
		return index;
	}
}
