package bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BSON 
{			
	List<Document<?>> docs;
	
	public BSON()
	{
		docs = new ArrayList<Document<?>>();
	}
	
	public int parseBSON(char[] data)
	{
		int len = data[0] + (data[1] << 8) + (data[2] << 16) + (data[3] << 24);
		
		docs = new ArrayList<Document<?>>();
		
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
			
			index = process(type, data, index, docs, name);
			
			if (index >= len - 1)
				break;
		}
		
		return index;
	}
	
	private int process(int type, char[] data, int index, List<Document<?>> docs, String name)
	{		
		switch (type)
		{
			case 0x01:
			{
				Document<Double> temp = new Document<Double>();
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
				Document<String> temp = new Document<String>();
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
				Document<BSON> temp = new Document<BSON>();
				temp.data = new BSON();
				temp.name = name;
				
				char[] data2 = Arrays.copyOfRange(data, index, data.length);
				int offset = temp.data.parseBSON(data2);
				index += offset + 1;
				
				docs.add(temp);
				break;
			}
			case 0x04:
			{
				Document<ArrayList<Document<?>>> temp = new Document<ArrayList<Document<?>>>();
				temp.data = new ArrayList<Document<?>>();
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
				Document<ObjectID> temp = new Document<ObjectID>();
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
				Document<Boolean> temp = new Document<Boolean>();
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
				Document<Long> temp = new Document<Long>();			
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
				Document<Byte> temp = new Document<Byte>();			
				temp.name = name;
				temp.data = null;
				
				docs.add(temp);
				break;	
			}
			case 0x10:
			{
				Document<Integer> temp = new Document<Integer>();			
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
