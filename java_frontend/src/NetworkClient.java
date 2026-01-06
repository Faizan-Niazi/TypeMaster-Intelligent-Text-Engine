import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class NetworkClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final String host;
    private final int port;

    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    private synchronized void connect() {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(5000);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                    true
            );

            System.out.println("Connected to backend at " + host + ":" + port);

        } catch (IOException ex) {
            closeSilently();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            "Backend not running or connection failed",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE
                    )
            );
        }
    }

    private synchronized boolean isSocketAlive() {
        return socket != null && !socket.isClosed();
    }

    // ------------------ SYNC SEND ------------------
    public synchronized String send(String msg) {
        if (!isSocketAlive()) return "ERR";

        try {
            out.println(msg);
            out.flush();

            if (out.checkError()) {
                throw new IOException("Write failed");
            }

            StringBuilder response = new StringBuilder();
            String line;

            while (true) {
                line = in.readLine();

                // SERVER CLOSED CONNECTION
                if (line == null) {
                    closeSilently();
                    return "ERR";
                }

                if ("END".equals(line)) break;
                response.append(line).append("\n");
            }

            return response.toString().trim();

        } catch (IOException ex) {
            closeSilently();
            System.err.println("Communication error: " + ex.getMessage());
            return "ERR";
        }
    }

    // ------------------ ASYNC SEND ------------------
    public Future<String> sendAsync(String msg) {
        return exec.submit(() -> send(msg));
    }

    // ------------------ CLEAN SHUTDOWN ------------------
    public synchronized void shutdown() {
        exec.shutdownNow();
        closeSilently();
    }

    private void closeSilently() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
    public synchronized boolean testConnection() {
        if (socket == null || socket.isClosed()) {
            return false;
        }

        try {
            out.println("CHECK:test");
            out.flush();

            if (out.checkError()) {
                closeSilently();
                return false;
            }

            String line;
            boolean gotResponse = false;

            while (true) {
                line = in.readLine();

                // server closed connection
                if (line == null) {
                    closeSilently();
                    return false;
                }

                gotResponse = true;

                if ("END".equals(line)) {
                    break;
                }
            }

            return gotResponse;

        } catch (IOException e) {
            closeSilently();
            return false;
        }
    }

}
