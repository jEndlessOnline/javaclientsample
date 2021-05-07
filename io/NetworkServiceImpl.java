package com.endlessonline.client.io;

import com.endlessonline.client.events.*;
import com.endlessonline.client.events.connection.ConnectionFailedEvent;
import com.endlessonline.client.events.connection.ConnectionLostEvent;
import com.endlessonline.client.events.connection.ConnectionSuccessEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkServiceImpl implements NetworkService, Runnable {

    private final Listener listener;
    private final List<Byte> buffer;

    private String host = "";
    private int port;

    private State state;
    private Socket socket;
    private OutputStream outStream;
    private InputStream inputStream;
    private EOSerializer processor;
    private List<EOWriter> queue;
    private Thread connectReadThread;
    private Thread writeThread;

    public NetworkServiceImpl(Listener listener) {
        this.listener = listener;
        this.buffer = new ArrayList<>();
        this.state = State.DISCONNECTED;
    }

    private synchronized void setState(State state) {
        this.state = state;
    }

    private synchronized State getState() {
        return this.state;
    }

    private void read(int length) throws IOException {
        this.buffer.clear();
        for (int i=0; i<length; i++) {
            int read = this.inputStream.read();
            if (read == -1) throw new IOException();
            this.buffer.add((byte)read);
        }
    }

    private int readLength() throws IOException {
        this.read(2);
        return EOSerializer.bytesToNumber(this.buffer.get(0) & 0xFF, this.buffer.get(1) & 0xFF);
    }

    private EOReader readPacket(int length)  throws IOException {
        this.read(length);
        synchronized (this.processor) {
            return this.processor.decode(new ArrayList<>(this.buffer));
        }
    }

    public void init(int s1, int s2, int encode, int decode) {
        synchronized (this.processor) {
            this.processor.initialize(s1, s2, encode, decode);
        }
    }

    public void setSequence(int s1, int s2) {
        synchronized (this.processor) {
            this.processor.setNewSequenceStart(s1, s2);
        }

        this.send(PacketFactory.keepAlive());
    }

    @Override
    public void connect(String host, int port) {
        if (this.getState() != State.DISCONNECTED) {
            return;
        }

        synchronized (this) {
            this.host = host;
            this.port = port;
        }

        this.setState(State.CONNECTING);
        this.connectReadThread = new Thread(this, NetworkServiceImpl.class.getSimpleName());
        this.connectReadThread.start();
    }

    @Override
    public void send(EOWriter packet) {
        if (this.getState() != State.CONNECTED) return;
        synchronized (this.queue) {
            this.queue.add(packet);
            this.queue.notify();
        }
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(this.host, this.port);
            this.outStream = this.socket.getOutputStream();
            this.inputStream = this.socket.getInputStream();
            this.listener.onEvent(new ConnectionSuccessEvent());
            this.setState(State.CONNECTED);
        } catch (IOException e) {
            this.listener.onEvent(new ConnectionFailedEvent());
            this.setState(State.DISCONNECTED);
            return;
        }

        this.processor = new EOSerializer();
        this.queue = new ArrayList<>();
        this.writeThread = new Thread(new NetworkWriteThread(), NetworkWriteThread.class.getSimpleName());
        this.writeThread.start();
        while (this.getState() == State.CONNECTED) {
            try {
                final EOReader reader = this.readPacket(this.readLength());
                Event event = EventFactory.fromReader(reader);
                if (event == null) {
                    event = new UnknownEvent(reader);
                }

                this.listener.onEvent(event);
            } catch (IOException e) {
                this.setState(State.DISCONNECTING);
            }
        }

        try {
            this.writeThread.interrupt();
            this.writeThread.join();
        } catch (InterruptedException e) {
            this.writeThread.interrupt();
        }

        this.setState(State.DISCONNECTED);
        this.listener.onEvent(new ConnectionLostEvent());
    }

    @Override
    public void close() {
        if (this.getState() != State.CONNECTING && this.getState() != State.CONNECTED) {
            return;
        }

        this.setState(State.DISCONNECTING);
        this.connectReadThread.interrupt();
    }

    public class NetworkWriteThread implements Runnable {

        private EOWriter getNextPacket() throws InterruptedException {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    queue.wait();
                }

                return queue.remove(0);
            }
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    EOWriter builder = this.getNextPacket();
                    synchronized (processor) {
                        List<Byte> array = processor.encode(builder);
                        for (int i=0; i<array.size(); i++) {
                            outStream.write(array.get(i));
                        }
                        outStream.flush();
                    }

                } catch (IOException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    setState(State.DISCONNECTED);
                }
            }
        }
    }

    public enum State {
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
    }
}
