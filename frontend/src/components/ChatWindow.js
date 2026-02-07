import { useEffect, useRef, useState } from "react";
import {
    getChatDetailsApi,
    addUserToGroupApi,
    removeUserFromGroupApi,
} from "../api/chatApi";

import {
    connectSocket,
    subscribeToChat,
    sendMessageSocket,
} from "../websocket/socket";

export default function ChatWindow({ selectedChatId }) {
    const [selectedChat, setSelectedChat] = useState(null);
    const [messageText, setMessageText] = useState("");

    // websocket status
    const [socketConnected, setSocketConnected] = useState(false);

    // group member input
    const [newMemberId, setNewMemberId] = useState("");

    // prevent multiple subscriptions
    const subscribedChatIdRef = useRef(null);

    // connect socket only once
    useEffect(() => {
        connectSocket(
            () => {
                console.log("✅ socket connected");
                setSocketConnected(true);
            },
            () => {
                console.log("❌ socket disconnected");
                setSocketConnected(false);
            }
        );
    }, []);

    // load chat details from REST when chat changes
    useEffect(() => {
        if (!selectedChatId) return;

        const loadChat = async () => {
            try {
                const res = await getChatDetailsApi(selectedChatId);
                setSelectedChat(res.data);
            } catch (err) {
                console.log("Load Chat Error:", err.response?.data || err.message);
                alert("Failed to load chat ❌");
            }
        };

        loadChat();
    }, [selectedChatId]);

    // subscribe websocket ONLY when chatId changes and socket is connected
    useEffect(() => {
        if (!selectedChatId) return;
        if (!socketConnected) return;

        // prevent duplicate subscribe
        if (subscribedChatIdRef.current === selectedChatId) return;

        console.log("📌 Subscribing to chat:", selectedChatId);

        subscribeToChat(selectedChatId, (newMsg) => {
            console.log("📩 WS MESSAGE RECEIVED:", newMsg);

            setSelectedChat((prev) => {
                if (!prev) return prev;

                const alreadyExists = prev.messages.some((m) => m.id === newMsg.id);
                if (alreadyExists) return prev;

                return {
                    ...prev,
                    messages: [...prev.messages, newMsg],
                };
            });
        });

        subscribedChatIdRef.current = selectedChatId;
    }, [selectedChatId, socketConnected]);

    // send message using websocket only
    const handleSendMessage = () => {
        if (!selectedChat) {
            alert("Select chat first ❌");
            return;
        }

        if (!messageText.trim()) return;

        if (!socketConnected) {
            alert("Socket not connected ❌");
            return;
        }

        const email = localStorage.getItem("email");

        if (!email) {
            alert("Email not found in localStorage ❌ Login again");
            return;
        }

        const payload = {
            chatid: selectedChat.id,
            message: messageText,
            email: email,
        };

        console.log("📤 Sending WS message:", payload);

        sendMessageSocket(payload);

        setMessageText("");
    };

    // add user to group
    const handleAddMember = async () => {
        try {
            if (!selectedChat) return;

            if (!newMemberId.trim()) {
                alert("Enter user UUID ❌");
                return;
            }

            await addUserToGroupApi(selectedChat.id, newMemberId);

            alert("User added to group ✅");

            const updated = await getChatDetailsApi(selectedChat.id);
            setSelectedChat(updated.data);

            setNewMemberId("");
        } catch (err) {
            console.log("Add Member Error:", err.response?.data || err.message);
            alert("Failed to add member ❌");
        }
    };

    // remove user from group
    const handleRemoveMember = async (userId) => {
        try {
            if (!selectedChat) return;

            await removeUserFromGroupApi(selectedChat.id, userId);

            alert("User removed from group ✅");

            const updated = await getChatDetailsApi(selectedChat.id);
            setSelectedChat(updated.data);
        } catch (err) {
            console.log("Remove Member Error:", err.response?.data || err.message);
            alert("Failed to remove member ❌");
        }
    };

    return (
        <div style={{ width: "70%", display: "flex", flexDirection: "column" }}>
            {/* Header */}
            <div
                style={{
                    padding: "15px",
                    backgroundColor: "#128C7E",
                    color: "white",
                    fontSize: "18px",
                    fontWeight: "bold",
                }}
            >
                {selectedChat
                    ? selectedChat.chatName && selectedChat.chatName.trim() !== ""
                        ? selectedChat.chatName
                        : selectedChat.id
                    : "Chat Window"}
            </div>

            {/* Group Members Section */}
            {selectedChat && selectedChat.groupChat && (
                <div
                    style={{
                        padding: "10px",
                        backgroundColor: "#f2f2f2",
                        borderBottom: "1px solid lightgray",
                    }}
                >
                    <h4 style={{ margin: "5px 0" }}>Group Members</h4>

                    {/* Add member */}
                    <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
                        <input
                            value={newMemberId}
                            placeholder="Enter User UUID to Add"
                            onChange={(e) => setNewMemberId(e.target.value)}
                            style={{
                                flex: 1,
                                padding: "10px",
                                borderRadius: "8px",
                                border: "1px solid lightgray",
                            }}
                        />

                        <button
                            onClick={handleAddMember}
                            style={{
                                padding: "10px 20px",
                                backgroundColor: "#128C7E",
                                color: "white",
                                border: "none",
                                borderRadius: "8px",
                                cursor: "pointer",
                                fontWeight: "bold",
                            }}
                        >
                            Add
                        </button>
                    </div>

                    {/* Members list */}
                    <div style={{ maxHeight: "150px", overflowY: "auto" }}>
                        {selectedChat.users.map((user) => (
                            <div
                                key={user.id}
                                style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    padding: "8px",
                                    backgroundColor: "white",
                                    borderRadius: "8px",
                                    marginBottom: "6px",
                                    alignItems: "center",
                                }}
                            >
                <span style={{ fontSize: "14px" }}>
                  {user.name} ({user.email})
                </span>

                                <button
                                    onClick={() => handleRemoveMember(user.id)}
                                    style={{
                                        backgroundColor: "red",
                                        color: "white",
                                        border: "none",
                                        padding: "6px 12px",
                                        borderRadius: "6px",
                                        cursor: "pointer",
                                    }}
                                >
                                    Remove
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Messages */}
            <div
                style={{
                    flex: 1,
                    padding: "15px",
                    backgroundColor: "#ECE5DD",
                    overflowY: "auto",
                }}
            >
                {!selectedChat ? (
                    <h3 style={{ color: "gray" }}>Select a chat</h3>
                ) : selectedChat.messages.length === 0 ? (
                    <h3 style={{ color: "gray" }}>No messages yet</h3>
                ) : (
                    selectedChat.messages.map((msg, index) => (
                        <div
                            key={msg.id || index}
                            style={{
                                marginBottom: "10px",
                                padding: "10px",
                                borderRadius: "10px",
                                backgroundColor: "white",
                                width: "fit-content",
                                maxWidth: "70%",
                            }}
                        >
                            <b style={{ display: "block" }}>
                                {msg.senderName ? msg.senderName : msg.email ? msg.email : "Unknown"}
                            </b>

                            <p style={{ margin: "5px 0", color: "black" }}>{msg.message}</p>
                        </div>
                    ))
                )}
            </div>

            {/* Input Box */}
            <div
                style={{
                    padding: "10px",
                    display: "flex",
                    borderTop: "1px solid lightgray",
                    backgroundColor: "white",
                }}
            >
                <input
                    placeholder="Type a message..."
                    value={messageText}
                    onChange={(e) => setMessageText(e.target.value)}
                    style={{
                        flex: 1,
                        padding: "10px",
                        borderRadius: "20px",
                        border: "1px solid lightgray",
                        outline: "none",
                    }}
                />

                <button
                    onClick={handleSendMessage}
                    style={{
                        marginLeft: "10px",
                        padding: "10px 20px",
                        backgroundColor: "#128C7E",
                        border: "none",
                        color: "white",
                        borderRadius: "20px",
                        cursor: "pointer",
                        fontWeight: "bold",
                    }}
                >
                    Send
                </button>
            </div>
        </div>
    );
}
