package crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

class SimpleCrypto {

	private static final String KEY = "1234567890123456" // 16 karakter

	static String encrypt(String value) {
		Cipher cipher = Cipher.getInstance("AES")
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY.bytes, "AES"))
		return Base64.encoder.encodeToString(cipher.doFinal(value.bytes))
	}

	static String decrypt(String encrypted) {
		Cipher cipher = Cipher.getInstance("AES")
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY.bytes, "AES"))
		return new String(cipher.doFinal(Base64.decoder.decode(encrypted)))
	}
}
