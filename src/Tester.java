import WAVLCore.WAVLTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Leon on 06/05/2017.
 */
public class Tester {


    public void testWAVLTreeClass() {}

    public static void main(String[] args) {

        WAVLTree tree = new WAVLTree();

        boolean testClass = false; // Should we test the class functionality or the asymptotic performance

        // WAVLTree Test:

        if (testClass) {

            WAVLClassTester tester = new WAVLClassTester();
            try {
                tester.run(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else { // Tests to submit
            AsymptoticTests tester = new AsymptoticTests();
            tester.run();

        }

    }
}
