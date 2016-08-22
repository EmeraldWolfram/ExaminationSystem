/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import static java.lang.Thread.sleep;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krissy
 */
public class ClientToServer {
    public Queue queue = new LinkedList() ;

    public void enqueue(Object object)
    {
        queue.add(object);
        System.out.println("size: "+queue.size());
    }

    public Object dequeue()
    {
        System.out.println("Ready to dequeue");
        while(queue.isEmpty()){
        };
//        System.out.println("check: "+(String)(queue.peek()));
        return queue.poll();
    }
}
