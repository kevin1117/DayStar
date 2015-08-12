import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.aop.Advice;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class SrcTest {

	public static void main(String[] args) {
		// Class c1 = DataSourceTransactionManager.class;
		// Class c2 = Advice.class;
		// System.out.println(String.class.getSimpleName());
		// Class c3 = HashMap.class;
		// HashMap map = new HashMap();
		// List list = new ArrayList();

		List<Integer> li = new ArrayList<Integer>();
		li.add(1);
		li.add(2);
		li.add(3);
		li.add(4);
		for (int i = 0; i < li.size(); i++) {
			System.out.println("====");
			li.remove(i);
			i--;
		}
		System.out.println(li.size());
		System.out.println("===ceshi master");
	}

}
