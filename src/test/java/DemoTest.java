import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class DemoTest {
    @Test
    public void test() {
        System.out.println(RandomUtils.nextInt(0, 2));
    }
}
