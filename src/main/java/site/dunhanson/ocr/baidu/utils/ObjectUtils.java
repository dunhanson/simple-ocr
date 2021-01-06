package site.dunhanson.ocr.baidu.utils;

import java.io.*;

/**
 * @author dunhanson
 * 2020-06-20
 * 对象工具类
 */
public class ObjectUtils {
    /**
     * 对象转字节数组
     * 字节数组输出流转换成对象输出流
     * 对象输出流写入对象
     * 字节数组输出流转换成字节数组
     * @param object
     * @return byte[]
     * @throws IOException
     */
    public static byte[] toByteArray(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字节数组转对象
     * 字节数组转字节数组读取流
     * 字节数组读取流转对象读取流
     * 对象读取流转对象
     * @param bytes
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object toObject(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 二进制转对象
     * @param bytes
     * @param <T>
     * @return <T> T
     */
    public static <T> T toEntity(byte[] bytes) {
        return (T) toObject(bytes);
    }
}
