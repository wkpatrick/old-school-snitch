package ch.oldschoolsnit;

import ch.oldschoolsnit.models.VecUtil;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class VecUtilTest
{
	@Test
	public void testMin()
	{
		var values = new ArrayList<Float>();
		values.addAll(Arrays.asList(0f, 0f, 0f, 1f, 1f, 1f, 2f, 2f, 2f));
		Float[] expected = {0.0f, 0.0f, 0.0f};
		assertArrayEquals(expected, VecUtil.GetMin(values));
	}

	@Test
	public void testMax()
	{
		var values = new ArrayList<Float>();
		values.addAll(Arrays.asList(0f, 2f, 0f, 1f, 0f, 1f, 2f, 1f, 2f));
		Float[] expected = {2f, 2f, 2f};
		assertArrayEquals(expected, VecUtil.GetMax(values));
	}
}
