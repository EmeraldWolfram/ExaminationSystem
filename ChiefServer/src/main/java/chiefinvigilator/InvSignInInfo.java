/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chiefinvigilator;

import java.net.ServerSocket;

/**
 *
 * @author Krissy
 */
public class InvSignInInfo {
    ServerSocket socket;
        String challengeMessage;
        
        public InvSignInInfo(ServerSocket socket, String challengeMessage){
            this.socket = socket;
            this.challengeMessage = challengeMessage;
        }
        
        public ServerSocket getSocket(){
            return this.socket;
        }
        
        public String getChallengeMessage(){
            return this.challengeMessage;
        }
}
