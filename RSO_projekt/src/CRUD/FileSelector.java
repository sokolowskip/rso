package CRUD;

import java.util.List;

import bson.BSONDocument;

public interface FileSelector {
	List<FileInfo> getFiles(BSONDocument selector);
}
