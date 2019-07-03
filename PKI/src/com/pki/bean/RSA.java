package com.pki.bean;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import com.pki.utils.Coder;

/**
 * RSA安全编码组件 参考:http://snowolf.iteye.com/blog/381767
 */
public class RSA {
	private static final String KEY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	// 公钥
	private static String PUBLIC_KEY;
	// 私钥
	private static String PRIVATE_KEY;

	public String getPUBLIC_KEY() {
		return PUBLIC_KEY;
	}

	public void setPUBLIC_KEY(String pUBLIC_KEY) {
		PUBLIC_KEY = pUBLIC_KEY;
	}

	public String getPRIVATE_KEY() {
		return PRIVATE_KEY;
	}

	public void setPRIVATE_KEY(String pRIVATE_KEY) {
		PRIVATE_KEY = pRIVATE_KEY;
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> initKey() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 随机生成密钥对
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			// 按照指定字符串生成密钥对
			// SecureRandom secureRandom = new SecureRandom("我是字符串".getBytes());
			// keyPairGen.initialize(1024, secureRandom);

			keyPairGen.initialize(1024);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			// 公钥
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			PUBLIC_KEY = Coder.encryptBASE64(publicKey.getEncoded());
			System.out.println("公钥" + PUBLIC_KEY);

			// 私钥
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			PRIVATE_KEY = Coder.encryptBASE64(privateKey.getEncoded());
			System.out.println("私钥" + PRIVATE_KEY);
			map.put("pubkey", PUBLIC_KEY);
			map.put("prikey", PRIVATE_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("初始化密钥异常" + e);
		}
		return map;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String encryptByPublicKey(String data, String key) {
		try {
			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Coder.decryptBASE64(key));
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);

			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] encryptedData = data.getBytes("utf-8");
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 117) {
					cache = cipher.doFinal(encryptedData, offSet, 117);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 117;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return Coder.encryptBASE64(decryptedData);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("公钥加密异常!");
		}
		return null;
	}

	/**
	 * 公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String decryptByPublicKey(String data, String key) {
		// 对密钥解密
		try {
			byte[] keyBytes = Coder.decryptBASE64(key);

			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);

			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);

			byte[] encryptedData = Coder.decryptBASE64(data);

			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 128) {
					cache = cipher.doFinal(encryptedData, offSet, 128);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 128;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("公钥解密异常");
		}
		return null;
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String encryptByPrivateKey(String data, String key) {
		try {

			// 对密钥解密
			byte[] keyBytes = Coder.decryptBASE64(key);

			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);

			byte[] encryptedData = data.getBytes("utf-8");
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 117) {
					cache = cipher.doFinal(encryptedData, offSet, 117);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 117;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return Coder.encryptBASE64(decryptedData);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("私钥加密异常");
		}
		return null;
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String decryptByPrivateKey(String data, String key) {
		try {
			// 对密钥解密
			byte[] keyBytes = Coder.decryptBASE64(key);

			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] encryptedData = Coder.decryptBASE64(data);

			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 128) {
					cache = cipher.doFinal(encryptedData, offSet, 128);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 128;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return new String(decryptedData, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("私钥解密异常");
		}
		return null;
	}

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public String sign(byte[] data, String privateKey) throws Exception {
		// 解密由base64编码的私钥
		byte[] keyBytes = Coder.decryptBASE64(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return Coder.encryptBASE64(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public boolean verify(byte[] data, String publicKey, String sign) throws Exception {

		// 解密由base64编码的公钥
		byte[] keyBytes = Coder.decryptBASE64(publicKey);

		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(Coder.decryptBASE64(sign));
	}

	private RSA() {

	}

	private static final RSA rsa = new RSA();

	public static RSA getInstance() {
		return rsa;
	}

	public static void main(String args[]) {
		RSA.getInstance().initKey();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < 1000) {
			sb.append("a");
			i++;
		}
		String a = sb.toString();
		System.out.println("私钥加密，公钥解密");
		String _tmp = RSA.getInstance().encryptByPrivateKey(a, PRIVATE_KEY);
		if (RSA.getInstance().decryptByPublicKey(_tmp, PUBLIC_KEY).equals(a)) {
			System.out.println("pass");
		}

		System.out.println("公钥加密，私钥解密");
		String _tmp1 = RSA.getInstance().encryptByPublicKey(a, PUBLIC_KEY);
		if (RSA.getInstance().decryptByPrivateKey(_tmp1, PRIVATE_KEY).equals(a)) {
			System.out.println("pass");
		}
	}
}
