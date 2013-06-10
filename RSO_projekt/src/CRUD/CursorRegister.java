package CRUD;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import messages.QueryMessage;

public class CursorRegister {
	private static Map<Long, Cursor> cursorMap = new HashMap<Long, Cursor>();
	
	private static long cursorIdCounter = 0;
	
	/*public static Long createCursor(List<BSONDocument> documentList)
	{
		Cursor newCursor = new Cursor(documentList.iterator());
		Long newId = generateCursorId();
		
		cursorMap.put(newId, newCursor);
		
		return newId;
	}*/

	public static Cursor getCursor(Long id)
	{
		return cursorMap.get(id);
	}
	
	public static Long getNewCursorForQuery(QueryMessage queryMessage)
	{
		File[] fileArray = FileOperations.openCollection(queryMessage.fullCollectionName);
		Long newId = generateCursorId();
		Cursor newCursor = new Cursor(fileArray,newId, queryMessage.query, queryMessage.returnFieldSelector);
		
		cursorMap.put(newId, newCursor);
		return newId;
	}
	
	public static void deleteCursor(Long id)
	{
		cursorMap.remove(id);
	}
	
	private static Long generateCursorId()
	{
		return new Long(cursorIdCounter++);//inaczej?
	}
	
}
