
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

 
/**
 * Java + MongoDB Hello world Example
 * 
 */
public class App {
  public static void main(String[] args) {
 
    try {
 
	/**** Connect to MongoDB ****/
	// Since 2.10.0, uses MongoClient
	MongoClient mongo = new MongoClient("localhost", 27017);
 
	/**** Get database ****/
	// if database doesn't exists, MongoDB will create it for you
	DB db = mongo.getDB("testdb");
 
	/**** Get collection / table from 'testdb' ****/
	// if collection doesn't exists, MongoDB will create it for you
	DBCollection table = db.getCollection("user");
 
	/**** Insert ****/
	Random generator = new Random();	
	int[] inserts = {100, 1000, 10000, 20000};
	int[] amounts = {1, 5, 10, 20};
	int[][] results = new int[inserts.length][amounts.length];
	
	for (int k = 0; k < amounts.length; k++)
	{
		for (int j = 0; j < inserts.length; j++)
		{
			long start = System.currentTimeMillis();
			for (int i = 0; i < inserts[j]; i++)
			{
				BasicDBObject document = new BasicDBObject();
				for (int l = 0; l < amounts[k]; l++)
				{
					document.put("string", new Long(generator.nextLong()).toString());
					document.put("number", generator.nextInt());
					document.put("date", new Date(generator.nextLong()));
				}
				table.insert(document);
			}
		    long end = System.currentTimeMillis();
		    System.out.println(inserts[j] + " insertow po " + 3 * amounts[k] + " wartosci zajelo " + (end - start) + " milisekund");
		    results[j][k] = (int)(end - start);
		}
	}
	
	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("results.txt"));
	for (int i = 0; i < inserts.length; i++)
	{
		for (int j = 0; j < amounts.length; j++)
		{
			writer.write(new Integer(results[i][j]).toString());
			writer.write(" ");
		}
		writer.write("\n");
	}
	writer.close();
	
    } catch (UnknownHostException e) {
	e.printStackTrace();
    } catch (MongoException e) {
	e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 
  }
}