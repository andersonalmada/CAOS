/**
 * Copyright (c) 2000 - 2015 The Legion of the Bouncy Castle Inc. (http://www.bouncycastle.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package br.ufc.great.caos.api.util;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.PaddedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Simple and objective class to encrypt and decrypt data
 */
@SuppressWarnings("deprecation")
public class BCEncryptor {

	private BufferedBlockCipher cipher;
	private KeyParameter key;

	// Initialize the cryptographic engine.
	// The key array should be at least 8 bytes long.
	private BCEncryptor(byte[] key) {
		cipher = new PaddedBlockCipher(new CBCBlockCipher(new BlowfishEngine()));
		this.key = new KeyParameter(key);
	}

	// Initialize the cryptographic engine.
	// The string should be at least 8 chars long.
	public BCEncryptor(String key) {
		this(key.getBytes());
	}

	// Private routine that does the gritty work.
	private byte[] callCipher(byte[] data) throws CryptoException {
		int size = cipher.getOutputSize(data.length);
		byte[] result = new byte[size];
		int olen = cipher.processBytes(data, 0, data.length, result, 0);
		olen += cipher.doFinal(result, olen);

		if (olen < size) {
			byte[] tmp = new byte[olen];
			System.arraycopy(result, 0, tmp, 0, olen);
			result = tmp;
		}

		return result;
	}

	// Encrypt arbitrary byte array, returning the
	// encrypted data in a different byte array.
	public synchronized byte[] encrypt(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}

		cipher.init(true, key);
		return callCipher(data);
	}

	// Decrypts arbitrary data.
	public synchronized byte[] decrypt(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}

		cipher.init(false, key);
		return callCipher(data);
	}

}