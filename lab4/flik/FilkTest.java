package flik;

import static org.junit.Assert.*;
import org.junit.Test;

public class FilkTest {
    @Test
    public void testFilk() {
        for (int i = -500; i <= 500; ++i) {
            assertTrue(Flik.isSameNumber(i, i));
        }
    }
}
