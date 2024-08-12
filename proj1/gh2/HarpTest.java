package gh2;

import edu.princeton.cs.introcs.StdAudio;
import org.junit.Test;

/**
 * &#064;source  gh2/TestGuitarString.java
 */
public class HarpTest {
    @Test
    public void testPluckTheAString() {
        Harp aString = new Harp(GuitarHeroLite.CONCERT_A);
        aString.pluck();
        for (int i = 0; i < 50000; i += 1) {
            StdAudio.play(aString.sample());
            aString.tic();
        }
    }
}
