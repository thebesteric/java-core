package org.example.thread.thread_design_pattern.immutable;

public class LineTest {

    public static void main(String[] args) {
        Point startPoint = new Point(10, 10);
        Point endPoint = new Point(20, 20);
        Line line = new Line(startPoint, endPoint);
        System.out.println(line);

        startPoint.x = 100; // 如果没有定义 final int x，则这里可以修改
        startPoint.y = 100; // 如果没有定义 final int y，则这里可以修改
        endPoint.x = 200;
        endPoint.y = 200;
        System.out.println(line);
    }

    // line 是 immutable 类么？
    static final class Line {
        private final Point startPoint; // 这里 final 修饰的对象，只是保证对象的地址不能做改变，无法保证对象内部的属性无法变更
        private final Point endPoint;

        public Line(final Point startPoint, final Point endPoint) {
            // startPoint.x = 100; // 如果没有定义 final int x，则这里可以修改
            // startPoint.y = 100; // 如果没有定义 final int y，则这里可以修改
            this.startPoint = new Point(startPoint.getX(), startPoint.getY()); // 这里是用 new Point(startPoint.x, startPoint.y)，确保不会被修改
            this.endPoint = new Point(endPoint.getX(), endPoint.getY());
        }

        public Line(int startX, int startY, int endX, int endY) {
            this.startPoint = new Point(startX, startY);
            this.endPoint = new Point(endX, endY);
        }

        public Point getStartPoint() {
            return startPoint;
        }

        public Point getEndPoint() {
            return endPoint;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "startPoint=" + startPoint +
                    ", endPoint=" + endPoint +
                    '}';
        }
    }


    static final class Point {
        private /*final*/ int x; // 使用 final 保证对象内部的属性是无法变更的，才服务 immutable 定义
        private /*final*/ int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
