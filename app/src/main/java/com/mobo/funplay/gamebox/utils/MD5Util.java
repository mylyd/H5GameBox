package com.mobo.funplay.gamebox.utils;

import android.content.pm.Signature;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class MD5Util {

    public static String getFileMD5(File file) {

        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;

        byte buffer[] = new byte[1024];
        int len;

        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            return null;
        }
        return encodeHex(digest.digest());
    }


    public static String getStringMD5(String plainText) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
        } catch (Exception e) {
            return null;
        }
        return encodeHex(md.digest());

    }

    public static String getStreamMD5(InputStream in) {

        if (in == null) {
            return null;
        }

        MessageDigest md = null;

        byte buffer[] = new byte[1024];
        int len;

        try {

            md = MessageDigest.getInstance("MD5");

            while ((len = in.read(buffer, 0, 1024)) != -1) {

                md.update(buffer, 0, len);
            }

            in.close();
        } catch (Exception e) {

            return null;
        }

        return encodeHex(md.digest());

    }


    public static String encodeHex(byte[] data) {

        if (data == null) {

            return null;
        }

        final String HEXES = "0123456789abcdef";
        int len = data.length;
        StringBuilder hex = new StringBuilder(len * 2);

        for (int i = 0; i < len; ++i) {

            hex.append(HEXES.charAt((data[i] & 0xF0) >>> 4));
            hex.append(HEXES.charAt((data[i] & 0x0F)));
        }

        return hex.toString();
    }

    public String getMD5String(byte[] bytes) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(separator);
        }
        return hexString.toString();
    }

    /*
     * 获得apk的签名并产生和相对应的md5 hash
     *
     * @param signature
     * 		从 getInstalledPackages(PackageManager.GET_SIGNATURES) 获取
     * 		PackageInfo.signature
     *
     * 	数组下标	0 	证书MD5
     *  数组下标 1	证书描述
     */
    public String[] getPkgSignatureMD5(Signature[] signature) {
        if (signature.length == 0)
            return null;

        if (signature[0] == null)
            return null;

        byte[] cert = signature[0].toByteArray();
        if (cert.length <= 0)
            return null;

        InputStream input = new ByteArrayInputStream(cert);

        CertificateFactory cf = null;
        X509Certificate c = null;
        String SignatureMD5[] = new String[2];
        try {
            cf = CertificateFactory.getInstance("X509");

            c = (X509Certificate) cf.generateCertificate(input);
            byte[] encoded = c.getEncoded();

            String base64 = Base64.encodeToString(encoded, Base64.NO_WRAP);
            SignatureMD5[0] = getMD5String(base64.getBytes());
            SignatureMD5[1] = c.getIssuerDN().toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return SignatureMD5;
    }

    public static String getDexMd5(String apkPath) {
        File file = new File(apkPath);
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("classes.dex");
            InputStream istream = jarFile.getInputStream(je);
            return getStreamMD5(istream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
