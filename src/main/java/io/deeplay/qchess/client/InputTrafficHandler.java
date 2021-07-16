package io.deeplay.qchess.client;

import java.io.BufferedReader;
import java.io.IOException;

public class InputTrafficHandler extends Thread {
    private final BufferedReader in;
    private boolean stop;
    private boolean fail;

    public InputTrafficHandler(BufferedReader in) {
        this.in = in;
    }

    public void terminate() {
        stop = true;
    }

    public boolean isFail() {
        return fail;
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                inputTrafficHandlerLoop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error occurred while listening to input traffic!");
            fail = true;
        }
    }

    private void inputTrafficHandlerLoop() throws IOException {
        if (in.ready()) {
            String str = in.readLine();
            System.out.println(str);
        }
    }
}
