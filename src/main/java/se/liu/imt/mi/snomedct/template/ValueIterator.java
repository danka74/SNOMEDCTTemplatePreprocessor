package se.liu.imt.mi.snomedct.template;

import java.util.Iterator;

import org.json.JSONArray;

public class ValueIterator implements Iterator<String> {

	private Object object;
	private boolean isArray;
	private Iterator<Object> innerIterator;
	private boolean hasIterated;

	public ValueIterator(Object object) {
		this.object = object;
		hasIterated = false;
		isArray = (object != null && object.getClass() == JSONArray.class);
		if (isArray)
			innerIterator = ((JSONArray) object).iterator();
	}

	@Override
	public boolean hasNext() {
		if (object == null)
			return false;
		if (isArray)
			return innerIterator.hasNext();
		return !hasIterated;
	}

	@Override
	public String next() {
		if(object == null)
			return new String("<null>"); // should not happen, for debugging
		if (isArray)
			return innerIterator.next().toString();
		else {
			hasIterated = true;
			return object.toString();
		}
	}

	public int length() {
		if (isArray)
			return ((JSONArray) object).length();
		else
			return object == null ? 0 : 1;
	}

}
