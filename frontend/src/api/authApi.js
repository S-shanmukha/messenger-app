import api from "./api";

// signup
export const signupApi = (data) => {
    return api.post("/auth/signup", data);
};

// signin
export const signinApi = (data) => {
    return api.post("/auth/signin", data);
};
