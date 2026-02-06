import { useEffect, useState } from "react";
import {
    getChatDetailsApi,
    addUserToGroupApi,
    removeUserFromGroupApi,
} from "../api/chatApi";

import { sendMessageApi } from "../api/messageApi";
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

    // connect socket once
    useEffect(() => {
        connectSocket(
            () => setSocketConnected(true),
            () => setSocketConnected(false)
        );
    }, []);

    // load chat details when chat changes
    useEffect(() => {
        if (!selectedChatId) return;

        const loadChat = async () => {
            try {
                const res = await getChatDetailsApi(selectedChatId);
                setSelectedChat(res.data);

                // subscribe for realtime messages
                if (socketConnected) {
                    subscribeToChat(selectedChatId, (newMsg) => {
                        setSelectedChat((prev) => {
                            if (!prev) return prev;

                            const alreadyExists = prev.messages.some(
                                (m) => m.id === newMsg.id
                            );

                            if (alreadyExists) return prev;

                            return {
                                ...prev,
                                messages: [...prev.messages, newMsg],
                            };
                        });
                    });
                }
            } catch (err) {
                console.log("Load Chat Error:", err.response?.data || err.message);
                alert("Failed to load chat ❌");
            }
        };

        loadChat();
    }, [selectedChatId, socketConnected]);

    // send message
    const handleSendMessage = async () => {
        try {
            if (!selectedChat) {
                alert("Select chat first ❌");
                return;
            }

            if (!messageText.trim()) return;

            const payload = {
                chatid: selectedChat.id,
                message: messageText,
            };

            // REST API save in DB
            const res = await sendMessageApi(payload);

            // update UI instantly
            setSelectedChat((prev) => ({
                ...prev,
                messages: [...prev.messages, res.data],
            }));

            // websocket broadcast
            if (socketConnected) {
                sendMessageSocket(payload);
            }

            setMessageText("");
        } catch (err) {
            console.log("Send Message Error:", err.response?.data || err.message);
            alert("Failed to send message ❌");
        }
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

            // reload chat
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

            // reload chat
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
                    selectedChat.messages.map((msg) => (
                        <div
                            key={msg.id}
                            style={{
                                marginBottom: "10px",
                                padding: "10px",
                                borderRadius: "10px",
                                backgroundColor: "white",
                                width: "fit-content",
                                maxWidth: "70%",
                            }}
                        >
                            <b>{msg.senderName}</b>
                            <p style={{ margin: "5px 0" }}>{msg.message}</p>
                            <small style={{ color: "gray" }}>
                                {msg.createdAt ? msg.createdAt : ""}
                            </small>
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
