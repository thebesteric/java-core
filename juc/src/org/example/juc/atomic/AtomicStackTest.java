package org.example.juc.atomic;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicStackTest {

    public static void main(String[] args) throws InterruptedException {
        MyStack myStack = new MyStack();

        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                myStack.push(new MyNode(i));
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 11; i <= 20; i++) {
                myStack.push(new MyNode(i));
            }
        }, "t2");

        Thread t3 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (!myStack.isEmpty()) {
                myStack.pop();
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        myStack.printStack();
    }

    static class MyStack {
        private final AtomicReference<MyNode> stackHead = new AtomicReference<>();

        /**
         * 入栈
         */
        public void push(MyNode node) {
            while (true) {
                MyNode head = this.stackHead.get();
                node.next = head;
                if (stackHead.compareAndSet(head, node)) {
                    System.out.println(Thread.currentThread().getName() + ": " + node + " -> 入栈成功");
                    return;
                }
            }
        }

        /**
         * 出栈
         */
        public MyNode pop() {
            while (true) {
                MyNode head = stackHead.get();
                if (head == null) {
                    System.out.println("栈顶元素为空");
                    return null;
                }
                MyNode next = head.next;
                if (stackHead.compareAndSet(head, next)) {
                    head.next = null;
                    System.out.println(Thread.currentThread().getName() + ": " + head + " <- 出栈成功");
                    return head;
                }
            }
        }

        public boolean isEmpty() {
            return stackHead.get() == null;
        }

        /**
         * 打印栈信息
         */
        public void printStack() {
            MyNode head = stackHead.get();
            while (head != null) {
                System.out.println(head);
                head = head.next;
            }
        }
    }

    static class MyNode {
        private int value;
        private MyNode next;

        public MyNode(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "MyNode(" + value + ")";
        }
    }
}
