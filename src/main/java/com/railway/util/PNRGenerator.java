package com.railway.util;

import java.util.Random;

public class PNRGenerator {
    
    private static final String PREFIX = "PNR"; // 3 chars
    private static final Random random = new Random();

    /**
     * FIXED: Generates a shorter PNR to fit inside VARCHAR(10).
     * Old logic: PNR + Timestamp (13) + Random (3) = 19 chars (Too Long)
     * New logic: PNR + Random (6) = 9 chars (Fits perfectly)
     * @return 
     */
    public static String generatePNR() {
        // Generate a random 6-digit number (100000 to 999999)
        int randomNum = 100000 + random.nextInt(900000);
        
        // Example Result: "PNR123456" (9 Characters)
        return PREFIX + randomNum;
    }

    // Keeping this method just in case other parts of the app use it
    public static String generateSimplePNR() {
        return generatePNR();
    }
}
