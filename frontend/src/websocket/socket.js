import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;

export const connectSocket = (onConnected, onError) => {
    stompClient = new Client({
        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
        reconnectDelay: 5000,

        onConnect: () => {
            console.log("WebSocket Connected ✅");
            if (onConnected) onConnected();
        },

        onStompError: (frame) => {
            console.log("WebSocket Error ❌", frame);
            if (onError) onError(frame);
        },
    });

    stompClient.activate();
};

export const subscribeToChat = (chatId, callback) => {
    if (stompClient && stompClient.connected) {
        stompClient.subscribe(`/topic/group/${chatId}`, (msg) => {
            callback(msg.body); // because backend sends string
        });
    }
};

export const sendMessageSocket = (payload) => {
    if (stompClient && stompClient.connected) {
        stompClient.publish({
            destination: "/app/message",
            body: JSON.stringify(payload),
        });
    }
};
