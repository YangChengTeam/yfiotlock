package com.yc.yfiotlock;

import com.yc.yfiotlock.helper.TOTP;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TOTPTest {
    @Test
    public void topt_isCorrect() {
        String seed32 = "3132333435363738393031323334353637383930"
                + "313233343536373839303132";
        assertEquals("345572", TOTP.generateTOTP256(seed32, Long.toHexString(1619419670).toUpperCase(), "6"));
        assertEquals("854843", TOTP.generateTOTP256(seed32, Long.toHexString(1619419668).toUpperCase(), "6"));

        assertEquals("163508", TOTP.generateTOTP256("5431449435363738393031323334353637383930313233343536373839303132", Long.toHexString(1622789172).toUpperCase(), "6"));
        assertEquals("144673", TOTP.generateTOTP256("0989778835363738393031323334353637383930313233343536373839303132", Long.toHexString(1622793252).toUpperCase(), "6"));

        StringBuilder prefix = new StringBuilder();
        byte[] bytes = "21019900".getBytes();
        for (int i = 0; i < bytes.length; i++) {
            prefix.append(bytes[i] % 10);
        }
        assertEquals("09897788", prefix.toString());
    }


}
