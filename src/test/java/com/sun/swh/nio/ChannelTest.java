package com.sun.swh.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Auther: swh
 * @Date: 2020/6/25 10:40
 * @Description:
 */
public class ChannelTest {

    /**
     * 测试通过文件输入输出流的getChannel方法获取到nio的Channel
     * 实现的是通过nio复制一个文件。
     */
    @Test
    public void test() {
        try (FileInputStream inputStream = new FileInputStream("D:\\任务.xlsx");
             FileChannel inChannel = inputStream.getChannel();
             FileOutputStream outputStream = new FileOutputStream("D:\\任务1.xlsx");
             FileChannel outChannel = outputStream.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (inChannel.read(buffer) != -1) {
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接使用对应的的Channel的静态open方法来获取channel
     */
    @Test
    public void test1() {
        try (FileChannel inChannel = FileChannel.open(Paths.get("D:\\任务.xlsx"), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get("D:\\任务2.xlsx"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE)) {
            MappedByteBuffer inMap = inChannel.map(FileChannel.MapMode.READ_ONLY,0,inChannel.size());
            MappedByteBuffer outMap = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
            byte[] data = new byte[inMap.limit()];
            inMap.get(data);
            outMap.put(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Channel间直接传输，不通过中间的Buffer对象
     */
    @Test
    public void test2() {
        try (FileChannel inChannel = FileChannel.open(Paths.get("D:\\任务.xlsx"), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get("D:\\任务3.xlsx"),StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE)) {
            inChannel.transferTo(0,inChannel.size(),outChannel);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
