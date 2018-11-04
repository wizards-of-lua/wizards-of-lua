package net.wizardsoflua.file;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

  public String createRandomPassword() {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[16];
    random.nextBytes(bytes);
    return encodeBase64(bytes);
  }

  public String encrypt(UUID salt, String password, String input) throws BadPaddingException {
    try {
      IvParameterSpec iv = toInitializationVector(salt);
      SecretKeySpec keySpec = toKey(password);

      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

      byte[] encrypted = cipher.doFinal(input.getBytes());

      String result = encodeBase64(encrypted);
      return result;
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
        | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  public String decrypt(UUID salt, String password, String input) throws BadPaddingException {
    try {
      IvParameterSpec iv = toInitializationVector(salt);
      SecretKeySpec keySpec = toKey(password);

      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

      byte[] decodedInput = decodeBase64(input);
      byte[] decrypted = cipher.doFinal(decodedInput);

      String result = new String(decrypted);
      return result;
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
        | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  private static SecretKeySpec toKey(String password) {
    byte[] passwordBytes = decodeBase64(password);
    return new SecretKeySpec(passwordBytes, ALGORITHM);
  }

  private static IvParameterSpec toInitializationVector(UUID salt) {
    byte[] saltBytes = toBytes(salt);
    return new IvParameterSpec(saltBytes);
  }

  private static byte[] toBytes(UUID uuid) {
    ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    return buffer.array();
  }

  private static String encodeBase64(byte[] input) {
    return new String(Base64.getUrlEncoder().encode(input)).replace("=", "");
  }

  private static byte[] decodeBase64(String input) {
    int len = input.length();
    for (int i = 0; i < 4 - (len % 4); ++i) {
      input += "=";
    }
    return Base64.getUrlDecoder().decode(input.getBytes());
  }

}
