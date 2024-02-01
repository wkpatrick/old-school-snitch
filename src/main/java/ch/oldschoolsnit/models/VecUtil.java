package ch.oldschoolsnit.models;

import java.util.ArrayList;
import java.util.Collections;

public class VecUtil
{
	public static Float[] GetMin(ArrayList<Float> values)
	{
		var list1 = new ArrayList<Float>();
		var list2 = new ArrayList<Float>();
		var list3 = new ArrayList<Float>();

		for (int i = 0; i < 9; i++)
		{
			switch (i % 3)
			{
				case 0:
					list1.add(values.get(i));
					break;
				case 1:
					list2.add(values.get(i));
					break;
				case 2:
					list3.add(values.get(i));
					break;
			}
		}

		var retVal = new Float[3];
		retVal[0] = Collections.min(list1);
		retVal[1] = Collections.min(list2);
		retVal[2] = Collections.min(list3);
		return retVal;
	}

	public static Float[] GetMax(ArrayList<Float> values)
	{
		var list1 = new ArrayList<Float>();
		var list2 = new ArrayList<Float>();
		var list3 = new ArrayList<Float>();

		for (int i = 0; i < 9; i++)
		{
			switch (i % 3)
			{
				case 0:
					list1.add(values.get(i));
					break;
				case 1:
					list2.add(values.get(i));
					break;
				case 2:
					list3.add(values.get(i));
					break;
			}
		}

		var retVal = new Float[3];
		retVal[0] = Collections.max(list1);
		retVal[1] = Collections.max(list2);
		retVal[2] = Collections.max(list3);
		return retVal;
	}
}
