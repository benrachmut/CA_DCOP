package Try;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		
		ArrayList<boolean[]> AA = getBoolArr(3);
		System.out.println();
	}
	static public ArrayList<boolean[]> getBoolArr(int length) {
        int numOptions = 1 << length;
        ArrayList<boolean[]> finalArray = new ArrayList<boolean[]>();
        for(int o=0;o<numOptions;o++) {
            boolean[] newArr = new boolean[length];
            for(int l=0;l<length;l++) {
                int val = ( 1<<l ) & o;
                newArr[l] = val>0;
            }
            finalArray.add(newArr);
        }
        return finalArray;
    }
}
