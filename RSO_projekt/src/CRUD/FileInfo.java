package CRUD;

import java.io.File;
import java.io.Serializable;

/**
 * Bedzie przesylana po sieci w srodku ShardInfo,
 * a wiec implementuje serializacje.
 *
 */

public class FileInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7353248128127631938L;
	public String name;
	public long size;
	
	public FileInfo(File document)
	{
		name = document.getAbsolutePath();
		//pomijamy nazwe katalogu domowego i exampleDB
		String split[] = name.split(".*/exampleDB");
		name = split[1];
		size = document.length();
	}

	public FileInfo(FileInfo fileInfo) {
		name = fileInfo.name;
		size = fileInfo.size;
	}
}
