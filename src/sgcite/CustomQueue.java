package sgcite;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom generic FIFO (First-In, First-Out) Queue implemented from scratch
 * using a singly linked node structure.
 * Designed to avoid unexpected crashes through explicit null guards and boundary checks.
 *
 * @param <T> The element type held by this queue.
 */
public class CustomQueue<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomQueue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Adds an element to the rear (tail) of the queue. O(1) time complexity.
     *
     * @param item Element to enqueue.
     * @return true if enqueued successfully, false if item was null.
     */
    public boolean enqueue(T item) {
        if (item == null) {
            return false;
        }

        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        this.size++;
        return true;
    }

    /**
     * Removes and returns the element at the front (head) of the queue. O(1) time complexity.
     * Safely returns null if the queue is empty, preventing NoSuchElementException crashes.
     *
     * @return The dequeued element, or null if the queue is empty.
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }

        T removedData = this.head.getData();
        this.head = this.head.getNext();
        this.size--;

        if (this.head == null) {
            this.tail = null;
        }

        return removedData;
    }

    /**
     * Retrieves, but does not remove, the element at the front of the queue.
     *
     * @return The front element, or null if empty.
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return this.head.getData();
    }

    /**
     * Checks whether the queue contains zero elements.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0 || this.head == null;
    }

    /**
     * Returns the total count of elements currently in the queue.
     *
     * @return Queue size.
     */
    public int size() {
        return this.size;
    }

    /**
     * Clears all elements from the queue.
     */
    public void clear() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Returns a snapshot of elements currently in the queue in arrival order.
     *
     * @return List containing all items in the queue.
     */
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = this.head;
        boolean hasNextNode = (current != null);
        while (hasNextNode) {
            if (current.getData() != null) {
                list.add(current.getData());
            }
            current = current.getNext();
            hasNextNode = (current != null);
        }
        return list;
    }
}
