package extraction;

import java.util.ArrayList;

import general.Data;
import general.PSS;
import general.Product;

public class Extrapolation {

	public static double Get_similarity(long product_id1, long product_id2) {
		ArrayList<Long> commonid = new ArrayList<Long>();
		if (product_id1 == product_id2)
			return 1;
		if (!(Data.productdb.containsKey(product_id1) && Data.productdb.containsKey(product_id2)))
			return 0;

		Product pro1 = Data.productdb.get(product_id1);
		commonid.add(product_id1);
		int depth1 = 2;
		int founddepth = -1;
		if (pro1.getParent() != 0) {
			do {
				commonid.add(pro1.getParent());
				pro1 = Data.productdb.get(pro1.getParent());
				depth1++;

			} while (pro1.getParent() != 0);
		}
		Product pro2 = Data.productdb.get(product_id2);
		int depth2 = 2;
		if (commonid.contains(product_id2))
			founddepth = 1;
		if (pro2.getParent() != 0) {
			do {
				if (founddepth == -1) {
					if (commonid.contains(pro2.getParent()))
						founddepth = depth2-1;
				}
				pro2 = Data.productdb.get(pro2.getParent());
				depth2++;
			} while (pro2.getParent() != 0);
		}
		double result = ((double) 2 * (founddepth == -1 ? 1 : depth2 - founddepth)) / ((double) (depth1 + depth2));
		return result;
	}
}
