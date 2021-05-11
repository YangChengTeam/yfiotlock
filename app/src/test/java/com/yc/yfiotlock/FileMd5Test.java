package com.yc.yfiotlock;

import com.yc.yfiotlock.download.DeviceDownloadManager;
import com.yc.yfiotlock.utils.CommonUtil;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class FileMd5Test {
    @Test
    public void fileMd5_Test(){
        String md5 = CommonUtil.file2MD5(new File("D:\\Desktop\\YF-L1xxx.bin"));
        assertEquals("1ccb5bdd88eb5412b5814342a72f2cba", md5);
    }
}
