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
    }
}
