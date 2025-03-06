package com.bank.security.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class SM4Encryptor {
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public byte[] encrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
            SecretKeySpec sm4Key = new SecretKeySpec(key, "SM4");
            cipher.init(Cipher.ENCRYPT_MODE, sm4Key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("SM4加密失败", e);
        }
    }
} 