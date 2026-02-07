import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;
let isConnected = false;

export const connectSocket = (onConnected, onError) => {
    if (stompClient && isConnected) {
        console.log("⚠️ Socket already connected");
        return;
    }

    const token = localStorage.getItem("token");

    stompClient = new Client({
        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),

        connectHeaders: {
            Authorization: "Bearer " + token,
        },

        debug: (str) => console.log("STOMP:", str),

        reconnectDelay: 5000,

        onConnect: () => {
            console.log("✅ WebSocket Connected");
            isConnected = true;
            if (onConnected) onConnected();
        },

        onDisconnect: () => {
            console.log("❌ WebSocket Disconnected");
            isConnected = false;
        },

        onWebSocketError: (err) => {
            console.log("❌ WebSocket Error:", err);
            isConnected = false;
            if (onError) onError(err);
        },

        onStompError: (frame) => {
            console.log("❌ Broker error:", frame.headers["message"]);
            console.log("Details:", frame.body);
            isConnected = false;
            if (onError) onError(frame);
        },
    });

    stompClient.activate();
};

export const subscribeToChat = (chatId, callback) => {
    if (!stompClient || !isConnected) {
        console.log("❌ Cannot subscribe, socket not connected");
        return;
    }

    console.log("📌 Subscribing to:", `/topic/group/${chatId}`);

    stompClient.subscribe(`/topic/group/${chatId}`, (msg) => {
        const data = JSON.parse(msg.body);
        console.log("📩 WS RECEIVED:", data);

        if (callback) callback(data);
    });
};

export const sendMessageSocket = (payload) => {
    if (!stompClient || !isConnected) {
        console.log("❌ Socket not connected. Message not sent.");
        return;
    }

    console.log("📤 Publishing to /app/message:", payload);

    stompClient.publish({
        destination: "/app/message",
        body: JSON.stringify(payload),
    });
};

export const disconnectSocket = () => {
    if (stompClient) {
        stompClient.deactivate();
        stompClient = null;
        isConnected = false;
        console.log("❌ Socket disconnected manually");
    }
};