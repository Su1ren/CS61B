package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;'";
    private static final double CONCERT_A = 440.0;

    private static double getFrequency(int index) {
        return Math.pow(2, (index - 24) / 12.0) * CONCERT_A;
    }

    public static void main(String[] args) {
        GuitarString[] guitarStrings = new GuitarString[KEYBOARD.length()];

        for (int i = 0; i < KEYBOARD.length(); ++i) {
            guitarStrings[i] = new GuitarString(getFrequency(i));
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index >= 0) {
                    guitarStrings[index].pluck();
                }
            }

            double sample = 0.0;
            for (GuitarString string : guitarStrings) {
                sample += string.sample();
            }

            StdAudio.play(sample);

            for (GuitarString guitarString : guitarStrings) {
                guitarString.tic();
            }
        }
    }
}
