package com.yc.yfiotlock;

import com.yc.yfiotlock.utils.CR4Util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CR4Test {
    @Test
    public void cr4_isCorrect() {
        String key = "123458";
        byte[] bytes =  new byte[]{(byte)0x02, (byte)0x39, (byte)0x98, (byte)0x78,(byte) 0xAB, (byte)0x78,(byte)0xbc, (byte)0x98};
        assertArrayEquals(new byte[]{(byte)0xd8}, CR4Util.decrypt(key, bytes));
    }
}
