import api from "./api";

export const sendMessageApi = (data) => api.post("/api/messages/create", data);
