package com.kk.securityhttp.utils;

import com.kk.securityhttp.utils.security.Base64;
import com.kk.securityhttp.utils.security.Md5;
import com.kk.securityhttp.utils.security.Rsa;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by zhangkai on 16/9/19.
 */
public class EncryptUtil {

    ///< rsa 分段加密
    public static String rsa(String publickey, String jsonStr) {
        String key = publickey;
        LogUtil.msg("publickey->" + Md5.md5(key));
        String result = null;
        String[] strs = sectionStr(jsonStr);
        result = Rsa.encrypt(jsonStr, key);
        LogUtil.msg("客户端请求加密数据->" + result);
        return result;
    }

    ///< rsa字符分段
    @SuppressWarnings("null")
    public static String[] sectionStr(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String[] strs = null;
        int length = str.length();
        if (length > 256) {
            int len = length / 256 + (length % 256 > 0 ? 1 : 0);
            strs = new String[len];
            for (int i = 0, j = 0; i < length; i += 1) {
                int start = i * 128;
                int last = (i + 1) * 128;
                if (last > length) {
                    last = length;
                }
                if (j >= len) {
                    break;
                }
                strs[j++] = str.substring(start, last);
            }
        }
        return strs;
    }

    ///< 数据压缩
    public static byte[] compress(String data) {
        try {
            byte[] bytes = data.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            compress(bais, baos);
            byte[] output = baos.toByteArray();

            baos.flush();
            baos.close();
            bais.close();

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void compress(InputStream is, OutputStream os)
            throws Exception {
        GZIPOutputStream gos = new GZIPOutputStream(os);
        int count;
        byte data[] = new byte[1024];
        while ((count = is.read(data, 0, data.length)) != -1) {
            gos.write(data, 0, count);
        }
        gos.finish();
        gos.close();
    }

    ///< 数据解压
    public static String unzip(InputStream in) {
        // Open the compressed stream
        GZIPInputStream gin;
        try {
            if (in == null) {
                LogUtil.msg("服务器没有返回数据->null");
                return null;
            }
            gin = new GZIPInputStream(new BufferedInputStream(in));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Transfer bytes from the compressed stream to the output stream
            byte[] buf = new byte[1024];
            int len;
            while ((len = gin.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            gin.close();
            out.close();
            String result = new String(out.toByteArray());
            LogUtil.msg("服务器返回数据->" + result);
            return result;
        } catch (IOException e) {
            LogUtil.msg("服务器返回数据解压异常:" + e.getMessage(), LogUtil.W);
            e.printStackTrace();
        }
        return null;
    }
}
