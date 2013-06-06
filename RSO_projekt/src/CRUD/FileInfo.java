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
		size = document.length();
	}
}
