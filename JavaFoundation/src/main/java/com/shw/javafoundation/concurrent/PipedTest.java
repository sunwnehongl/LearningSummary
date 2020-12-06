package com.shw.javafoundation.concurrent;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipedTest {

    public static void main(String[] args) throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        inputStream.connect(outputStream);

        InPutThread inPutThread = new InPutThread(inputStream);
        inPutThread.start();

        OutPutThread outPutThread = new OutPutThread(outputStream);
        outPutThread.start();
    }

}

class InPutThread extends Thread {
    private PipedInputStream inputStream;

    public InPutThread(PipedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void run() {
        int receive = 0;
        byte[] receiveBytes = new byte[1024];
        StringBuilder sb = new StringBuilder();
        try {
            while ((receive = inputStream.read(receiveBytes)) != -1) {
                sb.append(new String(receiveBytes));
            }
            System.out.println("读取到：" + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class OutPutThread extends Thread {
    private PipedOutputStream outputStream;

    public OutPutThread(PipedOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void run() {
        try {
            outputStream.write("第一次写出".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.write("第二次写出".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


