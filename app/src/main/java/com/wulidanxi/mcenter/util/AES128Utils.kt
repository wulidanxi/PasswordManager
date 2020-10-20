package com.wulidanxi.mcenter.util

import android.annotation.SuppressLint
import java.io.*
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES128Utils {
    private const val HEX = "0123456789ABCDEF"
    private const val keyLenght = 16
    private const val defaultV = "0"

    /**
     * 加密
     *
     * @param key
     * 密钥
     * @param src
     * 加密文本
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encrypt(key: String, src: String): String? {
        val rawKey = toMakeKey(key, keyLenght, defaultV).toByteArray() // key.getBytes();
        val result = encrypt(rawKey, src.toByteArray(charset("utf-8")))
        return toHex(result)
    }

    /**
     * 加密
     *
     * @param key
     * 密钥
     * @param src
     * 加密文本
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun encrypt2Java(key: String, src: String): String? {
        val rawKey = toMakeKey(key, keyLenght, defaultV).toByteArray() // key.getBytes();
        val result = encrypt2Java(rawKey, src.toByteArray(charset("utf-8")))
        return toHex(result)
    }

    /**
     * 解密
     *
     * @param key
     * 密钥
     * @param encrypted
     * 待揭秘文本
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decrypt(key: String, encrypted: String): String? {
        val rawKey = toMakeKey(key, keyLenght, defaultV).toByteArray() // key.getBytes();
        val enc = toByte(encrypted)
        val result = decrypt(rawKey, enc)
        return String(result, charset("UTF-8"))
    }

    /**
     * 密钥key ,默认补的数字，补全16位数，以保证安全补全至少16位长度,android和ios对接通过
     * @param str
     * @param strLength
     * @param val
     * @return
     */
    private fun toMakeKey(str: String, strLength: Int, `val`: String): String {
        var strLoc = str
        var strLen = strLoc.length
        if (strLen < strLength) {
            while (strLen < strLength) {
                val buffer = StringBuffer()
                buffer.append(strLoc).append(`val`)
                strLoc = buffer.toString()
                strLen = strLoc.length
            }
        }
        return strLoc
    }

    /**
     * 真正的加密过程
     * 1.通过密钥得到一个密钥专用的对象SecretKeySpec
     * 2.Cipher 加密算法，加密模式和填充方式三部分或指定加密算 (可以只用写算法然后用默认的其他方式)Cipher.getInstance("AES");
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    private fun encrypt(key: ByteArray, src: ByteArray): ByteArray {
        val seedKeySpec = SecretKeySpec(key, "AES")
        val cipher: Cipher = Cipher.getInstance("AES")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            seedKeySpec,
            IvParameterSpec(ByteArray(cipher.blockSize))
        )
        return cipher.doFinal(src)
    }

    /**
     * 真正的加密过程
     *
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun encrypt2Java(key: ByteArray, src: ByteArray): ByteArray {
        val seedKeySpec = SecretKeySpec(key, "AES")
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            seedKeySpec,
            IvParameterSpec(ByteArray(cipher.blockSize))
        )
        return cipher.doFinal(src)
    }

    /**
     * 真正的解密过程
     *
     * @param key
     * @param encrypted
     * @return
     * @throws Exception
     */
    @SuppressLint("GetInstance")
    private fun decrypt(key: ByteArray, encrypted: ByteArray): ByteArray {
        val seedKeySpec = SecretKeySpec(key, "AES")
        val cipher: Cipher = Cipher.getInstance("AES")
        try {
            cipher.init(
                Cipher.DECRYPT_MODE,
                seedKeySpec,
                IvParameterSpec(ByteArray(cipher.blockSize))
            )
            return cipher.doFinal(encrypted)
        }catch (e : Exception) {
            return "error" as ByteArray
        }


    }

    fun toHex(txt: String): String? {
        return toHex(txt.toByteArray())
    }

    fun fromHex(hex: String): String? {
        return String(toByte(hex))
    }


    /**
     * 把16进制转化为字节数组
     * @param hexString
     * @return
     */
    private fun toByte(hexString: String): ByteArray {
        val len = hexString.length / 2
        val result = ByteArray(len)
        for (i in 0 until len) result[i] =
            Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).toByte()
        return result
    }


    /**
     * 二进制转字符,转成了16进制
     * 0123456789abcdefg
     * @param buf
     * @return
     */
    private fun toHex(buf: ByteArray?): String? {
        if (buf == null) return ""
        val result = StringBuffer(2 * buf.size)
        for (i in buf.indices) {
            appendHex(result, buf[i])
        }
        return result.toString()
    }

    private fun appendHex(sb: StringBuffer, b: Byte) {
        val i = b.toInt()
        sb.append(HEX[i shr 4 and 0x0f]).append(HEX[i and 0x0f])
    }

    /**
     * 初始化 AES Cipher
     * @param sKey
     * @param cipherMode
     * @return
     */
    @SuppressLint("GetInstance")
    private fun initAESCipher(sKey: String, cipherMode: Int): Cipher? {
        // 创建Key gen
        // KeyGenerator keyGenerator = null;
        var cipher: Cipher? = null
        try {
            val rawKey = toMakeKey(sKey, keyLenght, defaultV).toByteArray()
            val seedKeySpec = SecretKeySpec(rawKey, "AES")
            cipher = Cipher.getInstance("AES")
            cipher.init(cipherMode, seedKeySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace() // To change body of catch statement use File |
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace() // To change body of catch statement use File |
        } catch (e: InvalidKeyException) {
            e.printStackTrace() // To change body of catch statement use File |
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        return cipher
    }

    /**
     * 对文件进行AES加密
     * @param sourceFile
     * @param fileType
     * @param sKey
     * @return
     */
    fun encryptFile(sourceFile: File?, toFile: String, dir: String, sKey: String): File? {
        // 新建临时加密文件
        var encrypfile: File? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = FileInputStream(sourceFile)
            encrypfile = File(dir + toFile)
            outputStream = FileOutputStream(encrypfile)
            val cipher: Cipher? = initAESCipher(sKey, Cipher.ENCRYPT_MODE)
            // 以加密流写入文件
            val cipherInputStream = CipherInputStream(inputStream, cipher)
            val cache = ByteArray(1024)
            var nRead: Int
            while (cipherInputStream.read(cache).also { nRead = it } != -1) {
                outputStream.write(cache, 0, nRead)
                outputStream.flush()
            }
            cipherInputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace() // To change body of catch statement use File |
            // Settings | File Templates.
        } catch (e: IOException) {
            e.printStackTrace() // To change body of catch statement use File |
            // Settings | File Templates.
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace() // To change body of catch statement use
                // File | Settings | File Templates.
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace() // To change body of catch statement use
                // File | Settings | File Templates.
            }
        }
        return encrypfile
    }

    /**
     * AES方式解密文件
     * @param sourceFile
     * @return
     */
    fun decryptFile(sourceFile: File?, toFile: String, dir: String, sKey: String): File? {
        var decryptFile: File? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            decryptFile = File(dir + toFile)
            val cipher: Cipher? = initAESCipher(sKey, Cipher.DECRYPT_MODE)
            inputStream = FileInputStream(sourceFile)
            outputStream = FileOutputStream(decryptFile)
            val cipherOutputStream = CipherOutputStream(outputStream, cipher)
            val buffer = ByteArray(1024)
            var r: Int
            while (inputStream.read(buffer).also { r = it } >= 0) {
                cipherOutputStream.write(buffer, 0, r)
            }
            cipherOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace() // To change body of catch statement use File |
            // Settings | File Templates.
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace() // To change body of catch statement use
                // File | Settings | File Templates.
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace() // To change body of catch statement use
                // File | Settings | File Templates.
            }
        }
        return decryptFile
    }

}