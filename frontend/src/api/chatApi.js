import api from "./api";

export const createSingleChatApi = (userid) =>
    api.post("/api/chats/single", { userid });

export const createGroupChatApi = (chatName, userids) =>
    api.post("/api/chats/group", { chatName, userids });

export const getChatDetailsApi = (chatId) =>
    api.get(`/api/chats/${chatId}`);

export const getUserChatsApi = () =>
    api.get("/api/chats/user");

export const addUserToGroupApi = (chatId, userId) =>
    api.put(`/api/chats/${chatId}/add/${userId}`);

export const removeUserFromGroupApi = (chatId, userId) =>
    api.put(`/api/chats/${chatId}/remove/${userId}`);

export const deleteChatApi = (chatId) =>
    api.delete(`/api/chats/delete/${chatId}`);