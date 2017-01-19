package com.company;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import org.apache.commons.codec.binary.Base64;

public class Main {

    public static final int pub = 0;
    public static final int priv = 1;
    public static final String LICENSE = "#{0}\nDescription=Confluence\nNumberOfUsers=-1\nCreationDate={1}\nContactName={2}\nconf.active=true\nContactEMail={3}\nEvaluation=false\nconf.LicenseTypeName=COMMERCIAL\nMaintenanceExpiryDate=2337-12-25\nconf.NumberOfClusterNodes=0\nSEN=YOU MAKE ME A SAD PANDA.\nOrganisation={4}\nServerID={5}\nLicenseID=LID\nLicenseExpiryDate=2337-12-25\nPurchaseDate={6}";
    public static final String[] DSA_KEY = {"MIHwMIGoBgcqhkjOOAQBMIGcAkEA/KaCzo4Syrom78z3EQ5SbbB4sF7ey80etKII864WF64B81uRpH5t9jQTxeEu0ImbzRMqzVDZkVG9xD7nN1kuFwIVAJYu3cw2nLqOuyYO5rahJtk0bjjFAkBnhHGyepz0TukaScUUfbGpqvJE8FpDTWSGkx0tFCcbnjUDC3H9c9oXkGmzLik1Yw4cIGI1TQ2iCmxBblC+eUykA0MAAkBrKJN92XEUFWggagAhhhNtFVc/Nh/JTnB3xsQ5azfHq7UcFtPEq0ohc3vGZ7OGEQS7Ym08DB6B1DtD93CwaNdX", "MIHGAgEAMIGoBgcqhkjOOAQBMIGcAkEA/KaCzo4Syrom78z3EQ5SbbB4sF7ey80etKII864WF64B81uRpH5t9jQTxeEu0ImbzRMqzVDZkVG9xD7nN1kuFwIVAJYu3cw2nLqOuyYO5rahJtk0bjjFAkBnhHGyepz0TukaScUUfbGpqvJE8FpDTWSGkx0tFCcbnjUDC3H9c9oXkGmzLik1Yw4cIGI1TQ2iCmxBblC+eUykBBYCFBJm/ZWcfH/gZJU3FHsqy/8aD68B"};




    public static void main(String[] args) {
        System.out.println("hello world");
        // write your code here
    }

    public final String genLicense(String username, String email, String org, String sid) {
        byte[] signature = null;

        String licStr = null;
        String date = new Date().toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(out);


        byte[] lic = MessageFormat.format("#{0}\nDescription=Confluence\nNumberOfUsers=-1\nCreationDate={1}\nContactName={2}\nconf.active=true\nContactEMail={3}\nEvaluation=false\nconf.LicenseTypeName=COMMERCIAL\nMaintenanceExpiryDate=2337-12-25\nconf.NumberOfClusterNodes=0\nSEN=YOU MAKE ME A SAD PANDA.\nOrganisation={4}\nServerID={5}\nLicenseID=LID\nLicenseExpiryDate=2337-12-25\nPurchaseDate={6}", new Object[]{date, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), username, email, org, sid, new SimpleDateFormat("yyyy-MM-dd").format(new Date())}).getBytes();


        byte[] zlic = zipLicense(lic);


        byte[] text = new byte[zlic.length + 5];


        text[0] = 13;
        text[1] = 14;
        text[2] = 12;
        text[3] = 10;
        text[4] = 15;


        System.arraycopy(zlic, 0, text, 5, zlic.length);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            byte[] rawPrivateKey = Base64.decodeBase64(DSA_KEY[1]);

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(rawPrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            Signature sig = Signature.getInstance("SHA1withDSA");
            sig.initSign(privateKey);
            sig.update(text);

            signature = sig.sign();
        } catch (SignatureException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        try {
            dOut.writeInt(text.length);
            dOut.write(text);
            dOut.write(signature);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        byte[] fullLic = out.toByteArray();

        licStr = new String(Base64.encodeBase64(fullLic));
        int len = licStr.length();
        licStr = licStr + "X02" + Integer.toString(len, 31);

        return split(licStr);
    }

    private final byte[] zipLicense(byte[] lic) {

        byte[] buf = new byte[64];

        ByteArrayInputStream bis = new ByteArrayInputStream(lic);
        DeflaterInputStream dis = new DeflaterInputStream(bis, new Deflater());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            int len;
            while ((len = dis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }

            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                bis.close();
                dis.close();
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bos.toByteArray();
    }


    private final String split(String licenseData)

    {
        if ((licenseData == null) || (licenseData.length() == 0)) {
            return licenseData;
        }
        char[] chars = licenseData.toCharArray();


        StringBuffer buf = new StringBuffer(chars.length + chars.length / 76);


        for (int i = 0; i < chars.length; i++) {
             buf.append(chars[i]);
            if ((i > 0) && (i % 76 == 0)) {


                buf.append('\n');


            }
        }
        return buf.toString();

    }

}
