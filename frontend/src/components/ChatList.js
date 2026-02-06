import { useEffect, useState } from "react";
import {
    createSingleChatApi,
    createGroupChatApi,
    deleteChatApi,
    getUserChatsApi,
} from "../api/chatApi";

export default function ChatList({ setSelectedChatId }) {
    const [userid, setUserid] = useState("");
    const [chats, setChats] = useState([]);

    // group inputs
    const [groupName, setGroupName] = useState("");
    const [groupMembers, setGroupMembers] = useState("");

    // 3 dots menu
    const [menuChatId, setMenuChatId] = useState(null);

    // load chats
    useEffect(() => {
        loadChats();
    }, []);

    // close menu when click outside
    useEffect(() => {
        const closeMenu = () => setMenuChatId(null);
        window.addEventListener("click", closeMenu);

        return () => window.removeEventListener("click", closeMenu);
    }, []);

    const loadChats = async () => {
        try {
            const res = await getUserChatsApi();
            setChats(res.data);
        } catch (err) {
            console.log("Load Chats Error:", err.response?.data || err.message);
            alert("Failed to load chats ❌");
        }
    };

    // create single chat
    const handleCreateChat = async () => {
        try {
            if (!userid.trim()) {
                alert("Enter user UUID ❌");
                return;
            }

            await createSingleChatApi(userid);

            alert("Single Chat Created ✅");
            setUserid("");
            await loadChats();
        } catch (err) {
            console.log("Create Single Chat Error:", err.response?.data || err.message);
            alert("Failed to create single chat ❌");
        }
    };

    // create group chat
    const handleCreateGroup = async () => {
        try {
            if (!groupName.trim()) {
                alert("Enter group name ❌");
                return;
            }

            if (!groupMembers.trim()) {
                alert("Enter group members UUIDs ❌");
                return;
            }

            const userids = groupMembers
                .split(",")
                .map((id) => id.trim())
                .filter((id) => id.length > 0);

            await createGroupChatApi(groupName, userids);

            alert("Group Chat Created ✅");
            setGroupName("");
            setGroupMembers("");
            await loadChats();
        } catch (err) {
            console.log("Create Group Error:", err.response?.data || err.message);
            alert("Failed to create group ❌");
        }
    };

    // delete chat
    const handleDeleteChat = async (chatId) => {
        try {
            const confirmDelete = window.confirm(
                "Are you sure you want to delete this chat?"
            );

            if (!confirmDelete) return;

            await deleteChatApi(chatId);

            alert("Chat Deleted ✅");

            setMenuChatId(null);
            await loadChats();
        } catch (err) {
            console.log("Delete Chat Error:", err.response?.data || err.message);
            alert("Failed to delete chat ❌");
        }
    };

    return (
        <div
            style={{
                width: "30%",
                borderRight: "1px solid lightgray",
                display: "flex",
                flexDirection: "column",
            }}
        >
            {/* Header */}
            <div
                style={{
                    padding: "15px",
                    backgroundColor: "#075E54",
                    color: "white",
                    fontSize: "18px",
                    fontWeight: "bold",
                }}
            >
                WhatsApp Clone
            </div>

            {/* Create Single Chat */}
            <div style={{ padding: "10px", borderBottom: "1px solid #ddd" }}>
                <h4 style={{ margin: "5px 0" }}>Create Single Chat</h4>

                <input
                    value={userid}
                    placeholder="Enter User UUID"
                    onChange={(e) => setUserid(e.target.value)}
                    style={{
                        width: "100%",
                        padding: "10px",
                        borderRadius: "8px",
                        border: "1px solid lightgray",
                        marginBottom: "10px",
                    }}
                />

                <button
                    onClick={handleCreateChat}
                    style={{
                        width: "100%",
                        padding: "10px",
                        backgroundColor: "#128C7E",
                        border: "none",
                        color: "white",
                        fontWeight: "bold",
                        borderRadius: "8px",
                        cursor: "pointer",
                    }}
                >
                    Create Chat
                </button>
            </div>

            {/* Create Group Chat */}
            <div style={{ padding: "10px", borderBottom: "1px solid #ddd" }}>
                <h4 style={{ margin: "5px 0" }}>Create Group Chat</h4>

                <input
                    value={groupName}
                    placeholder="Enter Group Name"
                    onChange={(e) => setGroupName(e.target.value)}
                    style={{
                        width: "100%",
                        padding: "10px",
                        borderRadius: "8px",
                        border: "1px solid lightgray",
                        marginBottom: "10px",
                    }}
                />

                <textarea
                    value={groupMembers}
                    placeholder="Enter User UUIDs separated by comma"
                    onChange={(e) => setGroupMembers(e.target.value)}
                    rows={3}
                    style={{
                        width: "100%",
                        padding: "10px",
                        borderRadius: "8px",
                        border: "1px solid lightgray",
                        marginBottom: "10px",
                    }}
                />

                <button
                    onClick={handleCreateGroup}
                    style={{
                        width: "100%",
                        padding: "10px",
                        backgroundColor: "#075E54",
                        border: "none",
                        color: "white",
                        fontWeight: "bold",
                        borderRadius: "8px",
                        cursor: "pointer",
                    }}
                >
                    Create Group
                </button>
            </div>

            {/* Chat List */}
            <div style={{ flex: 1, overflowY: "auto" }}>
                {chats.length === 0 ? (
                    <p style={{ padding: "10px", color: "gray" }}>No chats found.</p>
                ) : (
                    chats.map((chat) => (
                        <div
                            key={chat.id}
                            style={{
                                padding: "15px",
                                borderBottom: "1px solid #eee",
                                cursor: "pointer",
                                position: "relative",
                            }}
                        >
                            {/* open chat */}
                            <div onClick={() => setSelectedChatId(chat.id)}>
                                <h4 style={{ margin: 0 }}>
                                    {chat.chatName && chat.chatName.trim() !== ""
                                        ? chat.chatName
                                        : chat.id}
                                </h4>

                                <p style={{ margin: 0, color: "gray", fontSize: "13px" }}>
                                    {chat.lastMessage?.message || "No messages yet"}
                                </p>
                            </div>

                            {/* 3 dots */}
                            <div
                                onClick={(e) => {
                                    e.stopPropagation();
                                    setMenuChatId(menuChatId === chat.id ? null : chat.id);
                                }}
                                style={{
                                    position: "absolute",
                                    top: "18px",
                                    right: "12px",
                                    fontSize: "18px",
                                    fontWeight: "bold",
                                    cursor: "pointer",
                                    userSelect: "none",
                                }}
                            >
                                ⋮
                            </div>

                            {/* Dropdown */}
                            {menuChatId === chat.id && (
                                <div
                                    onClick={(e) => e.stopPropagation()}
                                    style={{
                                        position: "absolute",
                                        top: "40px",
                                        right: "10px",
                                        backgroundColor: "white",
                                        border: "1px solid lightgray",
                                        borderRadius: "8px",
                                        boxShadow: "0px 2px 8px rgba(0,0,0,0.2)",
                                        zIndex: 10,
                                        overflow: "hidden",
                                    }}
                                >
                                    <button
                                        onClick={() => handleDeleteChat(chat.id)}
                                        style={{
                                            padding: "10px 15px",
                                            border: "none",
                                            backgroundColor: "white",
                                            color: "red",
                                            fontWeight: "bold",
                                            cursor: "pointer",
                                            width: "100%",
                                        }}
                                    >
                                        Delete
                                    </button>
                                </div>
                            )}
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
