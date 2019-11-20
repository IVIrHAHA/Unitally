package com.example.unitally.objects;

import java.util.Enumeration;
import java.util.Hashtable;

public class Template extends UnitCounterObject{
	private Hashtable<String, Unit> template;
	
	public Template(String name) {
	    super(name);
		template=new Hashtable<>();
	}

	public void saveTemplate(Hashtable<String,Unit> template) {
		Unit temp;
		Enumeration<Unit> list=template.elements();
		
		while(list.hasMoreElements())
		{
			temp=list.nextElement().copy();
			temp.zero();
			
			template.put(temp.getName(), temp);
		}
	}
	
	public boolean addUnit(Unit unitObj) {
		if(template.containsKey(unitObj.getName()))
		{
			return false;
		}
		else
		{
			template.put(unitObj.getName(), unitObj);
			return true;
		}
	}
	
	public Unit removeUnit(String unitName) {
		if(template.contains(unitName))
		{
			return template.remove(unitName);
		}
		else
			return null;
	}
	
	public Hashtable<String,Unit> getTemplate() {
		return template;
	}
}
