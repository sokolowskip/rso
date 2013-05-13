package CRUD;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.mongodb.DBObject;

public class Delete {
	public void deleteObject(final Collection<DBObject> collection, final int id) {

		final Gson gson = new Gson();
		DBObject dbo = Iterables.find(collection, new Predicate<DBObject>() {
			public boolean apply(DBObject dbo) {
				// System.out.println(dbo);
				DataObject dataObject = gson.fromJson(dbo.toString(),
						DataObject.class);
				if (dataObject.getId() == id) {
					// System.out.println(id);
					// usun element
					collection.remove(dbo);
				}
				return dataObject.getId() == id;
			}
		});
	}
	
	public void deleteObject(final Collection<DBObject> collection, final String name) {

		final Gson gson = new Gson();
		DBObject dbo = Iterables.find(collection, new Predicate<DBObject>() {
			public boolean apply(DBObject dbo) {
				// System.out.println(dbo);
				DataObject dataObject = gson.fromJson(dbo.toString(),
						DataObject.class);
				if (dataObject.getName() == name) {
					// System.out.println(id);
					// usun element
					collection.remove(dbo);
				}
				return dataObject.getName() == name;
			}
		});
	}
	
	public void deleteObject(final Collection<DBObject> collection, final double value) {

		final Gson gson = new Gson();
		DBObject dbo = Iterables.find(collection, new Predicate<DBObject>() {
			public boolean apply(DBObject dbo) {
				// System.out.println(dbo);
				DataObject dataObject = gson.fromJson(dbo.toString(),
						DataObject.class);
				if (dataObject.getValue() == value) {
					// System.out.println(id);
					// usun element
					collection.remove(dbo);
				}
				return dataObject.getValue() == value;
			}
		});
	}
}
