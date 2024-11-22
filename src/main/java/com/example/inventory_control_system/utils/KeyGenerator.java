package com.example.inventory_control_system.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // Generate a secure 256-bit key
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Convert the key to a Base64-encoded string
        String base64EncodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Generated Base64-encoded key: " + base64EncodedKey);
    }
}
