package com.example.unitally;
import com.example.unitally.objects.Template;
import com.example.unitally.objects.Unit;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *  -convert to template
 *  -add count value to specific unit
 *  -load from template
 *  -add/remove Unit
 *  -add/remove multiplier
 *  -get specific unit
 *  
 * @author IVIrHAHA
 *
 */

public class ActiveCount 
{
	private static Hashtable<String, Unit> ActiveList=new Hashtable<String, Unit>();

	public static boolean addUnit(Unit unit) {
		if(ActiveList.containsKey(unit.getName())) {
			return false;
		}
		else {
			ActiveList.put(unit.getName(), unit);
			return true;
		}
	}
	
	public static Unit remove(String unitName) {
		if(contains(unitName)) {
			return ActiveList.remove(unitName);
		}
		else
			return null;
	}
	
	public static Template getAsTemplate(String templateName) {
		Template newTemplate=new Template(templateName);
		newTemplate.saveTemplate(ActiveList);
		
		return newTemplate;
	}
	
	public static void importTemplate(Template template) {
		ActiveList=template.getTemplate();   
	}
	
	public static void add_sub(String unitName, int amount) {
		Unit unit=ActiveList.get(unitName);
		
		if(unit != null) {
			unit.increment_decrement(amount);
		}
	}

	public static Unit[] getUnitArray() {
		Enumeration<Unit> unitEnumeration = ActiveList.elements();
		Unit allUnits[] = new Unit[ActiveList.size()];

		for(int i = 0; i< allUnits.length; i++) {
			allUnits[i] = unitEnumeration.nextElement();
		}

		return allUnits;
	}

	public static boolean contains(String unitName) {
		return ActiveList.containsKey(unitName);
	}
	
}
