import { useEffect, useState } from "react";
import ChatList from "../components/ChatList";
import ChatWindow from "../components/ChatWindow";
import { connectSocket } from "../websocket/socket";   // ✅ import socket connect

export default function Home() {
    const [selectedChatId, setSelectedChatId] = useState(null);

    // ✅ connect websocket when home loads
    useEffect(() => {
        connectSocket();
    }, []);

    return (
        <div style={{ display: "flex", height: "100vh", fontFamily: "Arial" }}>
            {/* LEFT SIDE */}
            <ChatList setSelectedChatId={setSelectedChatId} />

            {/* RIGHT SIDE */}
            <ChatWindow selectedChatId={selectedChatId} />
        </div>
    );
}
