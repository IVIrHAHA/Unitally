package com.example.unitally.objects;

/*
  Description:

 */

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.unitally.tools.UnitallyValues;

@Entity(tableName = "unit_table")
public class Unit implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String unit_name;

    @ColumnInfo(name = "amount")
    public double count_amount;

    @ColumnInfo(name = "worth")
    public double COUNT_WORTH;

    @ColumnInfo(name = "subunits")
    public ArrayList<Unit> unis;

    @ColumnInfo(name = "ids")
    public long mUnitId;

    @ColumnInfo(name = "symbol")
    public String mSymbol;

    @ColumnInfo(name = "symbol_pos")
    public boolean mPreSymbol;

    @ColumnInfo(name = "column")
    public Category mCategory;

    @Ignore
    public int mLabel;

/*						Constructors						*/
    public Unit() {
        unit_name="";
        count_amount=0;
        COUNT_WORTH=1;
        unis = new ArrayList<Unit>();
        mUnitId=0;
        mSymbol = "";
        mPreSymbol=false;
        mCategory = new Category(UnitallyValues.CATEGORY_DEFAULT_NAME);

        mLabel = 0;
    }

    public Unit(@NonNull String name_of_unit)
    {
        unit_name=name_of_unit.toLowerCase();
        count_amount=0;
        COUNT_WORTH=1;
        mSymbol = "";

        unis=new ArrayList<Unit>();
        mUnitId = generateKey();
        mPreSymbol = false;
        mCategory = new Category(UnitallyValues.CATEGORY_DEFAULT_NAME);

        mLabel = 0;
    }

    //Used for both making a clone and making a subunit
    private Unit(String name, double count, double worth, String symbol,
                 boolean symPos, ArrayList<Unit> subs, Category category, int label)
    {
        unit_name=name;
        this.count_amount=count;
        this.COUNT_WORTH=worth;
        mUnitId = generateKey();
        mSymbol = symbol;
        mPreSymbol = symPos;
        mCategory = category;

        unis=subs;
        mLabel = label;
    }

    //Making of a Subunit
    private Unit(Unit subunit, double worth)
    {
        this(subunit.getName(), subunit.getCount(), worth, subunit.getSymbol(),
                subunit.isSymbolBefore(), subunit.unis, subunit.getCategory(), subunit.getLabel());
    }

    //Making a deep clone
    private Unit(Unit unit)
    {
        this(unit.getName(), unit.getCount(), unit.getWorth(), unit.getSymbol(),
                unit.isSymbolBefore(), new ArrayList<Unit>(), unit.getCategory(), unit.getLabel());
    }

    @NonNull
    public String getName() {
        return unit_name;
    }

    public double getCount() {
        return count_amount;
    }

    public double getWorth() {
        return COUNT_WORTH;
    }

    public ArrayList<Unit> getSubunits() {
        return unis;
    }

    public ArrayList<Unit> getAllSubunits() {
        return getTotal();
    }

    public String getCSstring() {
        return mPreSymbol ? (mSymbol + " " + count_amount) : (count_amount + " " + mSymbol);
    }

    public void setName(String name) {
        unit_name = name;
        mUnitId = generateKey();
    }

    public void setLabel(int label) {
        mLabel = label;
    }

    public int getLabel() {
        return mLabel;
    }

    public String getSymbol() {return mSymbol;}

    /**
     * Returns the Category associated with the unit
     *
     * @return Category Object or "Miscellaneous" Category
     */
    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public void setSymbol(String symbol) {mSymbol = symbol;}

    public boolean isSymbolBefore() {
        return mPreSymbol;
    }

    public void setSymbolPos(boolean before) {
        mPreSymbol = before;
    }

    public void setWorth(double worth) {
        COUNT_WORTH = worth;
    }

    /*						  CALCULATIONS 						*/

    /**
     * Updates the count value throughout the Unit tree.
     * This method must be called to update after incrementing or
     * decrementing this Unit.
     */

    public void calculate()
    {
        calculateSubs(count_amount);
    }

    /**
     * 	Updates the branching subunits, recursively by passing current
     * 	count_amount to all subunits and then updating subunit's
     * 	count_amount. Lastly, updates the subunit's changes into the
     * 	subunit list.
     *
     * @param parent_count of parent Unit
     */
    private void calculateSubs(double parent_count)
    {
        Unit child;

        //Check all subunits
        for(int i=0; i<unis.size(); i++)
        {
            //get first subunit in subunits' list
            child=unis.get(i);

            //update subunit count_amount with parent count_amount
            child.count_amount=parent_count*child.COUNT_WORTH;

            //Recursively call the rest of the tree
            child.calculate();

            //updating list with updated subunit
            unis.set(i, child);
        }

    }

    /**
     * Combines all similar Units and adds them together.
     *
     * @return Array containing all unique Unit objects
     */
    public ArrayList<Unit> getTotal()
    {
        return gatherSubunits(new ArrayList<Unit>());
    }

    /**
     * Using recursion, this method is initially passed an empty ArrayList.
     * This unit compares itself to all Units above and around itself. However,
     * begins at the bottom most Unit of the Unit Tree.
     *
     * @param collection Initially empty, it is then substantiated with Unique units
     * @return	ArrayList containing unique subunits
     */
    private ArrayList<Unit> gatherSubunits(ArrayList<Unit> collection)
    {
        if(!unis.isEmpty())
        {
            //Check all subunits
            for (Unit uni : unis) collection = uni.gatherSubunits(collection);
        }

        return checkSelf(collection);
    }

    /**
     * Compare self with all unique subunits. If itself is not found in the
     * Unique collection list, then add itself to it.
     *
     * @param collection List containing Unique Units
     * @return	Unique Unit Collection list
     */
    private ArrayList<Unit> checkSelf(ArrayList<Unit> collection)
    {
        boolean found_identical=false;
        Unit tester;

        for(int i=0; i<collection.size();i++)
        {
            tester=collection.get(i);

            //This Unit matches a unit already in the collection list
            //Add their values together
            if(tester.equals(this))
            {
                found_identical=true;
                tester.increment_decrement(this.count_amount);

                collection.set(i, tester);
            }
        }

        //Found unique, add copy to the collection
        if(!found_identical)
        {
            collection.add(copy());
        }

        return collection;
    }

    /**
     * Collection containing all Units being utilized within this Unit
     *
     * @param list Empty ArrayList
     * @return	List containing all Units. (Not sorted)
     */
    public ArrayList<Unit> getIter(ArrayList<Unit> list)
    {
        list.add(this.copy());
        if(!unis.isEmpty())
        {
            for(int i=0; i<unis.size();i++)
            {
                list=unis.get(i).getIter(list);
            }
        }
        return list;
    }

/*							SETTERS							*/
    /**
     * Set count amount.
     *
     * @param count
     */
    public void setCount(double count)
    {
        count_amount=count;
    }

/*							GETTERS							*/

    public long getID() {
        return mUnitId;
    }
    /**
     * Clones the subunits of a parent Unit
     *
     * @param list	Empty ArrayList
     * @return	list containing all subunits and any lower level units
     */
    private ArrayList<Unit> cloneSubs(ArrayList<Unit> list)
    {
        Unit sub_clone;

        for(int i=0; i<unis.size();i++)
        {
            sub_clone=unis.get(i).copy();

            list.add(sub_clone);
        }

        return list;
    }

    public Unit copy()
    {
        //Clones the head unit
        Unit clone=new Unit(this);

        clone.unis=cloneSubs(new ArrayList<Unit>());

        return clone;
    }

/*							MUTATORS						*/
    private long generateKey() {
        long id ;
         id = unit_name.hashCode();

        return id;
    }

    /**
     * Will always add to the existing count amount of this object.
     *
     * @param num can be negative or positive
     */
    public void increment_decrement(double num)
    {
        count_amount+=num;
    }

    /**
     * Adds a dependent Unit to this Unit
     *
     * @param subunit Unit object to become dependent.
     * @param count	Worth of the dependent unit
     */
    public void addSubunit(Unit subunit, double count) {
        if(!unis.contains(subunit)) {
            if(subunit.getLabel() == UnitWrapper.USER_ADDED_LABEL) {
                unis.add(0, new Unit(subunit, count));
            }
            else
                unis.add(new Unit(subunit, count));
        }
    }

    public void addSubunit(Unit subunit) {
        if(!unis.contains(subunit)) {
            if(subunit.getLabel() == UnitWrapper.USER_ADDED_LABEL) {
                unis.add(0, new Unit(subunit, subunit.getCount()));
            }
            else
                unis.add(new Unit(subunit, subunit.getCount()));
        }
    }

    public Unit getSubunit(@NonNull String name) {
        Unit unit;
        for(int i = 0; i<unis.size(); i++) {
            unit = unis.get(i);

            if(unit.getName().equals(name)) {
                return unit;
            }
        }
        return null;
    }

    /**
     * Removes and returns a subunit as a Unit object.
     *
     * @param name of unit to be removed
     * @return	Unit with the corresponding name. Otherwise null.
     */
    public Unit removeSubunit(String name)
    {
        Unit exiled;

        if(name != null)
        {
            for(int i=0; i<unis.size(); i++)
            {
                exiled=unis.get(i);

                if(exiled.getName().equals(name))
                {
                    return unis.remove(i);

                }
            }
        }
        return null;
    }

    /**
     * Zeros out count_amount. Useful for making templates.
     */
    public void zero() {
        count_amount=0;
        calculate();
    }

/*							GENERIC							*/

    // TODO: Remove hashCode dependency on symbol parameters
    @Override
    public int hashCode() {
        int hash = unit_name.hashCode();

        if(mSymbol != null) {
            hash += mSymbol.hashCode();
            hash += (mPreSymbol ? 1 : 0);
        }

        hash += (mCategory.hashCode()%unit_name.hashCode());
        hash *= 31;

        return hash;
    }

    /**
     *
     * @param obj
     * @return True if and only if Unit has the same name as obj.
     */
    @Override
    public boolean equals(Object obj) {

        try {
            if(obj.getClass() == getClass()) {

                Unit object_to_compare = (Unit) obj;

                String name1 = object_to_compare.getName().toLowerCase();
                String name2 = unit_name.toLowerCase();

                if (name1.equals(name2)) {
                    return true;
                }
            }
        }
        catch(Exception e)
        {
            return false;
        }

        return false;
    }

    @Override
    public String toString() {
        String line="";

        if(!unis.isEmpty())
        {
            //Header
            line=unit_name +"\t- " +count_amount;

            if(COUNT_WORTH != 1)
                line+="\t@" + COUNT_WORTH;

            //line+="   Subunits:\t";
            for(int i=0; i<unis.size();i++)
            {
                Unit temp=unis.get(i);

                line+="\n\t"+temp;
            }
            line+="\n";
        }
        else
        {
            line+="\t>: "+unit_name + " - " +count_amount;
            line+="\t@" + COUNT_WORTH;
        }
        return line;
    }

    public boolean isLeaf() {
        if(unis.isEmpty())
            return true;

        else
            return false;
    }
}//End of Class