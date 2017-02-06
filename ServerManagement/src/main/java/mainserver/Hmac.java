/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainserver;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Krissy
 */
public class Hmac {
    
    public Hmac(){}
    
    public String encode(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        System.out.println("KEY: "+ key);
        System.out.println("Data: "+ data);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return DatatypeConverter.printBase64Binary(sha256_HMAC.doFinal(data.getBytes()))+"\n";
    }
}
