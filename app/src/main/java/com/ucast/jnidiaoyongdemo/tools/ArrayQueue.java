package com.ucast.jnidiaoyongdemo.tools;

public class ArrayQueue<T> {
    private T[] elements;
    private int front;// 队头
    private int rear;// 队尾
    /**
     * The minimum capacity that we'll use for a newly created deque. Must be a
     * power of 2.
     */
    private static final int MIN_INITIAL_CAPACITY = 8;

    /**
     * 队列扩容的算法 队列的容量扩大为原来的两倍 Doubles the capacity of this deque. Call only when
     * full, i.e., when head and tail have wrapped around to become equal.
     */
    private void doubleCapacity() {
        assert front == rear;
        int p = front;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Sorry, deque too big");
        }
        Object[] a = new Object[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = (T[]) a;
        front = 0;
        rear = n;
    }

    /**
     * 默认存储空间大小为16
     */
    public ArrayQueue() {
        elements = (T[]) new Object[16];
    }

    /**
     * 确定队列内部数组实际的大小 对于一个给定长度，先判断是否小于定义的最小长度， 如果小于，则使用定义的最小长度作为数组的长度。
     * 否则，找到比给定长度大的最小的2的幂数
     *
     * @param numElements
     */
    public ArrayQueue(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>> 1);
            initialCapacity |= (initialCapacity >>> 2);
            initialCapacity |= (initialCapacity >>> 4);
            initialCapacity |= (initialCapacity >>> 8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0) // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
        }
        elements = (T[]) new Object[initialCapacity];
    }

    /**
     * 入队 入队要求队列不能满
     *
     * @param item
     */
    public void enqueue(T item) {

        if (item == null) {
            throw new NullPointerException();
        }
        elements[rear] = item; // 入队
        rear = (rear + 1) % elements.length; // 移动尾指针
        if (rear == front) { // 如果队列已满，扩容
            doubleCapacity();
        }
    }

    /**
     * 出队
     *
     * @return
     */
    public T dequeue() {
        int f = front;
        @SuppressWarnings("unchecked")
        T result = (T) elements[f];
        // Element is null if deque empty
        if (result == null)
            return null;
        elements[f] = null; // Must null out slot
        front = (f + 1) % elements.length; // 元素出对后头指针向后移位
        return result;
    }

    /**
     * @return
     */
    public int size() {
        return (rear - front + elements.length) % elements.length;
    }

    /**
     * 遍历算法 移动front指针，直到front指针追上rear指针
     */
    public void traverse() {
        int i = front, j = rear;
        while (i != j) {
            System.out.println(elements[i]);
            i = (i + 1) % elements.length;
        }
    }

    /**
     * 判断队列为空的条件是front == rear
     *
     * @return
     */
    public boolean isEmpty() {
        return front == rear;
    }
}
