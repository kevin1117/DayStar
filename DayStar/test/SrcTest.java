import org.aopalliance.aop.Advice;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class SrcTest {

	public static void main(String[] args) {
		Class c1 = DataSourceTransactionManager.class;
		Class c2= Advice.class;
	}
}
