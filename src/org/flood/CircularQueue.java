package org.flood;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A queue that after polling an element reinserts it at the end.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
public class CircularQueue<E> {

    Queue<E> queue = new LinkedList<E>();

    public E poll() {
        E element = queue.poll();
        add(element);
        return element;
    }

    public void add(E element) {
        queue.add(element);
    }

}
