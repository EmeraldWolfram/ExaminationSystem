/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

import java.util.LinkedList;
import java.util.Queue;


/**
 *
 * @author Krissy
 */
public class ServerToClient {
    public static Queue queue = new LinkedList() ;
    
    public static void enqueue(Object object)
    {
        queue.add(object);
        System.out.println("size: "+queue.size());
    }

    public static Object dequeue()
    {
        System.out.println("Ready to dequeue");
        while(queue.isEmpty()){
        };
//        System.out.println("check: "+(String)(queue.peek()));
        return queue.poll();
    }
}
