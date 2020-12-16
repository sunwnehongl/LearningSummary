package com.shw.javafoundation.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @Description TODO
 * @Author sunwenhong
 * @Date 2020/12/16 22:40
 */
public class BufferTest {

    private static void readAndWriteTest() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        print(buffer, "allocate");

        buffer.put("ABCDEFG".getBytes());
        print(buffer, "put");

        buffer.flip();
        print(buffer, "flip");

        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        System.out.println("读取到的数据" + new String(data));
        print(buffer, "get");

    }

    private static void print(Buffer buffer, String option) {
        System.out.println("---------------"+ option +"-------------------");
        System.out.println("position:" + buffer.position());
        System.out.println("limit:" + buffer.limit());
        System.out.println("capacity:" + buffer.capacity());
    }

    public static void main(String[] args) {
        readAndWriteTest();
    }
}
