/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encrypt;

import java.util.Base64;

/**
 * @author NeRooN
 */
public class Encrypter {

    /**
     * Method to encrypt a String
     *
     * @param password
     * @return Encrypted String
     */
    public static String getEncodedString(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    /**
     * Method to decrypt a String
     *
     * @param password
     * @return Decrypted String
     */
    public static String getDecryptedString(String password) {
        return new String(Base64.getMimeDecoder().decode(password));
    }

}
