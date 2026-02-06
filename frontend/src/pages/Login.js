import { useState } from "react";
import { signinApi } from "../api/authApi";
import { useNavigate, Link } from "react-router-dom";

export default function Login() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        email: "",
        password: "",
    });

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleLogin = async () => {
        try {
            const res = await signinApi(form);

            localStorage.setItem("jwt", res.data.jwt);

            alert("Login Successful ✅");
            navigate("/home");
        } catch (err) {
            console.log("Signin Error:", err.response?.data || err.message);
            alert("Login Failed ❌ Check email/password");
        }
    };

    return (
        <div style={{ width: "300px", margin: "100px auto" }}>
            <h2>Login</h2>

            <input
                type="email"
                name="email"
                placeholder="Email"
                value={form.email}
                onChange={handleChange}
                style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
            />

            <input
                type="password"
                name="password"
                placeholder="Password"
                value={form.password}
                onChange={handleChange}
                style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
            />

            <button
                onClick={handleLogin}
                style={{ width: "100%", padding: "10px" }}
            >
                Login
            </button>

            <p style={{ marginTop: "15px", textAlign: "center" }}>
                Not registered? <Link to="/signup">Signup</Link>
            </p>
        </div>
    );
}
