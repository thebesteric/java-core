package org.example.thread.vola;

/**
 * int[][] arr = new int[5][5];
 * <br>
 *    j0 j1 j2 j3 j4
 * i0  1  2  3  4  5
 * i1  1  2  3  4  5
 * i2  1  2  3  4  5
 * i3  1  2  3  4  5
 * i4  1  2  3  4  5
 *
 */
public class CacheLineDemo {

    public static void main(String[] args) {

        int[][] arr = new int[5000][5000];

        long start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            for (int j = 0; j < 5000; j++) {
                arr[i][j] = 1; // 按行赋值
            }
        }
        long end = System.currentTimeMillis() - start;
        System.out.println("按行赋值，花费时间：" + end + "ms"); // 29ms

        start = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            for (int j = 0; j < 5000; j++) {
                arr[j][i] = 1; // 按行赋值
            }
        }
        end = System.currentTimeMillis() - start;
        System.out.println("按列赋值，花费时间：" + end + "ms"); // 345ms
    }

}
