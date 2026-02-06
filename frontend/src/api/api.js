import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
});

// attach jwt automatically (except signin/signup)
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");

    // endpoints where JWT should NOT be attached
    const noAuthRoutes = ["/auth/signin", "/auth/signup"];

    // check if current request url matches public routes
    const isNoAuthRoute = noAuthRoutes.some((route) =>
        config.url?.includes(route)
    );

    if (token && !isNoAuthRoute) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default api;


