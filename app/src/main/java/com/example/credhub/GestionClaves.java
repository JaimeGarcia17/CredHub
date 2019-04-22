package com.example.credhub;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Created by Jaime García on 05,abril,2019
 */
public class GestionClaves {

    public static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    public static final String KEY_ALIAS = "Jaime";
    public static KeyStore keyStore;
    public static String claveEncriptada, claveDesencriptada;

    public void loadKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateNewKeyPair( String alias, Context context )
            throws Exception {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);

        KeyPairGeneratorSpec spec = new
                KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal("CN=" + alias))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", ANDROID_KEYSTORE);
        gen.initialize(spec);
        gen.generateKeyPair();
    }

    public PrivateKey loadPrivateKey( String alias ) throws Exception {


        if (!keyStore.isKeyEntry(alias)) {
            Log.e("No existe", "Could not find key alias: " + alias);
            return null;
        }
        KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.e("No privada", " alias: " + alias + " is not a PrivateKey");
            return null;
        }
        return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
    }

    public String encryptString(String clave) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(KEY_ALIAS, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            String initialText = clave;

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(initialText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            claveEncriptada = (Base64.encodeToString(vals, Base64.DEFAULT));
            return claveEncriptada;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptString(String alias) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            //RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)privateKey;
            //RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            String cipherText = claveEncriptada;
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            claveDesencriptada = finalText;
            return claveDesencriptada;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}


