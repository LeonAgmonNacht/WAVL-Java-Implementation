import WAVLCore.WAVLTree;

import java.util.Random;

/**
 * Created by Leon on 03/06/2017.
 */
public class AsymptoticTests {

        public static void PrintTree(WAVLTree t){
            String output = "";
            int[] keysArray = new int[t.size()];
            keysArray = t.keysToArray();
            for (int j = 0; j < keysArray.length; j++) {
                output += " " + keysArray[j];
            }
            System.out.println(output);
            System.out.println("min:" + t.min() + " " +"max:"+ t.max());
        }

        public static void run() {
            WAVLTree tree = new WAVLTree();
            int j = 100000;
            int num;
            int sum = 0;
            int max = 0;
            int temp = 0;
            for (int i = 0; i < j; i++) {
                num = -10000000 + (int)(Math.random() * ((10000000 + 10000000) + 1));
                temp = tree.insert(num, Integer.toString(num));
                sum += temp;
                max = Integer.max(max, temp);
            }
            System.out.println("number of tests: " + j + " total balancing: " + sum + " avg: " + (float)sum/j + " max: " + max);

            sum = 0;
            max = 0;
            temp = 0;

            int[] array = new int[tree.size()];
            array = tree.keysToArray();

            ShuffleArray(array);

            for (int i = 0; i < array.length; i++) {
                temp = tree.delete(array[i]);
                sum += temp;
                max = Integer.max(max, temp);
                System.out.println(i);

            }
            System.out.println("number of tests: " + j + " total balancing: " + sum + " avg: " + (float)sum/j + " max: " + max);
        }

        public static void ShuffleArray(int[] array) {
            int index, temp;
            Random random = new Random();
            for (int i = array.length - 1; i > 0; i--) {
                index = random.nextInt(i + 1);
                temp = array[index];
                array[index] = array[i];
                array[i] = temp;
            }
        }
}
