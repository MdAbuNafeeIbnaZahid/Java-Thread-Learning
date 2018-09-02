package executor_framework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SingleThreadServer {

    static String responseFileName = "response.html";
    static String response = getStringFromFile(responseFileName);

    static String getStringFromFile(String fileName)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str + "\n");
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        String content = contentBuilder.toString();
        return content;
    }

    private static void serveRequest(Socket connection)
    {
        System.out.println("New connection request from " + connection);

        try{
            OutputStream os = connection.getOutputStream();
            PrintWriter out = new PrintWriter(os);
            System.out.println("Writing");
            System.out.println(response);
            out.write( response );
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Executor executor;
        executor = Executors.newFixedThreadPool(10);
        while (true)
        {
            Socket connection = serverSocket.accept();
            System.out.println("accepted request");

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    serveRequest(connection);
                }
            });
        }
    }

}
