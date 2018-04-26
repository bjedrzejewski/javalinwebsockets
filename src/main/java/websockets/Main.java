package websockets;

import io.javalin.Javalin;
import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static Map<String, WsSession> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        Javalin.create()
                .port(7070)
                .enableStaticFiles("/public")
                .ws("/demo/:session-id", ws -> {
                    ws.onConnect(session -> {
                        session.send("Hello Session: "+session.param("session-id"));
                    });
                    ws.onMessage((session, message) -> {
                        String sortedMessage = "";
                        while(message.length() > 0){
                            Thread.sleep(50);
                            sortedMessage = sortedMessage + message.substring(0,1);
                            message = message.substring(1);

                            //sorting
                            char[] chars = sortedMessage.toCharArray();
                            Arrays.sort(chars);
                            sortedMessage = new String(chars).trim();

                            String response = "message unsorted: "+message+"\n"+"message sorted: "+sortedMessage;
                            session.send(response);
                        }
                    });
                    ws.onError(((wsSession, throwable) ->
                            System.out.println("Something went wrong")
                    ));
                    ws.onClose((session, status, message) -> {
                        //clean-up
                    });
                })
                .start();

    }
}