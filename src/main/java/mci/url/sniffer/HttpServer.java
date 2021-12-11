package mci.url.sniffer;

import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class HttpServer {

    private static final LocalQueue queueHelper = LocalQueue.getInstance();

    private static HttpServer server;

    private static final Object LOCK = new Object();

    private HttpServer() {
    }

    public static HttpServer getInstance() {
        synchronized (LOCK) {
            if (server == null) server = new HttpServer();
        }
        return server;
    }


    public void runServer() {
        port(8086);
        get("/", this::doReversePoxy);
    }

    private String doReversePoxy(Request request, Response response) {
        try {
            System.out.println(request.host());
            queueHelper.addDomainToQueue(request.host());
            //TODO implementation proxy pass the current request
            return ":DD";
        } catch (Exception e) {
            return "server error";
        }
    }
}
