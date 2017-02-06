/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queue;

/**
 *
 * @author Krissy
 */
public class MsgProducer implements Runnable {
    public MsgProducer()
    {
        Thread producer = new Thread(this);
        producer.start();
    }

    public void run()
    {
        while(true)
        {
            //add to the queue some sort of unique object
        }
    }
}
