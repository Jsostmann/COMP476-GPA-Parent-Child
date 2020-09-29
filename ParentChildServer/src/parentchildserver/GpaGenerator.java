/*
   Grade Generator for client
*/

package parentchildserver;

import java.util.Random;
import java.util.HashMap;

public class GpaGenerator
{
    public static String generateGpaData() {
        final HashMap<Integer, String> gradeMap = new HashMap<Integer, String>();
        populateMap(gradeMap);
        String grades = "";
        final Random rand = new Random();
        int credits = 0;
        final double currentGPA = rand.nextDouble() * 1.5 + 2.5;
        final int totalCredits = 15 + rand.nextInt(90);
        int count;
        int crd;
        for (count = 0; credits < 12; credits += crd, ++count) {
            final String grd = gradeMap.get(rand.nextInt(gradeMap.size()));
            crd = rand.nextInt(10);
            if (crd <= 5) {
                crd = 3;
            }
            else if (crd <= 7) {
                crd = 1;
            }
            else {
                crd = 4;
            }
            grades = grades + grd + "," + crd + ",";
        }
        grades = count + "," + grades + String.format("%3.2f", currentGPA) + "," + totalCredits;
        return grades;
    }
    
    private static void populateMap(final HashMap<Integer, String> gradeMap) {
        gradeMap.put(18, "A");
        gradeMap.put(17, "A-");
        gradeMap.put(0, "A");
        gradeMap.put(1, "A-");
        gradeMap.put(2, "B+");
        gradeMap.put(3, "B");
        gradeMap.put(4, "B-");
        gradeMap.put(5, "B+");
        gradeMap.put(6, "B");
        gradeMap.put(7, "B-");
        gradeMap.put(8, "B+");
        gradeMap.put(9, "B");
        gradeMap.put(10, "B-");
        gradeMap.put(11, "C+");
        gradeMap.put(12, "C");
        gradeMap.put(13, "C-");
        gradeMap.put(14, "D+");
        gradeMap.put(15, "D");
        gradeMap.put(16, "F");
    }
}
