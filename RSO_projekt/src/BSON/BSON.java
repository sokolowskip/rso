package BSON;

import java.util.ArrayList;
import java.util.List;


public class BSON 
{			
	ObjectID objID;
	List<Document<?>> docs;
	
	public void parseBSON(char[] data)
	{
		int len = data[0] + (data[1] << 8) + (data[2] << 16) + (data[3] << 24);
		
		objID = new ObjectID();
		docs = new ArrayList<Document<?>>();
		
		int index = 4;
		while (true)
		{
			int type = data[index]; 
			index++;
			
			String name = "";
			while(data[index] != 0)
			{
				name += (char)data[index];
				index++;
			}
			index++;
			
			switch (type)
			{
				case 0x02:
				{
					Document<String> temp = new Document<String>();
					temp.data = "";
					temp.name = name;
					
					int curLen = data[index] + (data[index + 1] << 8) + (data[index + 2] << 16) + (data[index + 3] << 24);
					index += 4;
					
					for (int i = 0; i < curLen - 1; i++)
					{
						temp.data += (char)data[index];
						index++;
					}
					index++;
					
					docs.add(temp);
					break;
				}
				case 0x07:	
				{
					objID.name = name;
					
					objID.time = (data[index] << 24) + (data[index + 1] << 16) + (data[index + 2] << 8) + data[index + 3];
					index += 4;
					objID.machine = (data[index] << 16) + (data[index + 1] << 8) + data[index + 2];
					index += 3;
					objID.procID = (data[index] << 8) + data[index + 1];
					index += 2;	
					objID.counter = (data[index] << 16) + (data[index + 1] << 8) + data[index + 2];
					index += 3;
					break; 
				}
				case 0x09:
				{
					Document<Long> temp = new Document<Long>();			
					temp.name = name;
					
					temp.data = (long)0;
					for (int i = 0; i < 8; i++)
						temp.data += (long)(data[index + i] << 8 * i);
					index += 8;
					
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
			if (index >= len - 1)
				break;
		}
	}
}
