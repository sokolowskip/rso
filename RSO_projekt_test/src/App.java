
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
	long start = System.currentTimeMillis();
	for (int i = 0; i < 10000; i++)
	{
		BasicDBObject document = new BasicDBObject();
		document.put("string", new Long(generator.nextLong()).toString());
		document.put("number", generator.nextInt());
		document.put("date", new Date(generator.nextLong()));
	
		table.insert(document);
	}
    long end = System.currentTimeMillis();
    System.out.println("Inserty zajely " + (end - start) + " milisekund");
    
    } catch (UnknownHostException e) {
	e.printStackTrace();
    } catch (MongoException e) {
	e.printStackTrace();
    }
 
  }
}