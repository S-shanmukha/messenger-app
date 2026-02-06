import { useState } from "react";
import { signupApi } from "../api/authApi";
import { useNavigate, Link } from "react-router-dom";

export default function Signup() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        name: "",
        email: "",
        password: "",
    });

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSignup = async () => {
        try {
            await signupApi(form);

            alert("Signup Successful ✅ Now Login!");
            navigate("/login");
        } catch (err) {
            console.log("Signup Error:", err.response?.data || err.message);
            alert("Signup Failed ❌ Check console");
        }
    };

    return (
        <div style={{ width: "300px", margin: "100px auto" }}>
            <h2>Signup</h2>

            <input
                type="text"
                name="name"
                placeholder="Name"
                value={form.name}
                onChange={handleChange}
                style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
            />

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
                onClick={handleSignup}
                style={{ width: "100%", padding: "10px" }}
            >
                Signup
            </button>

            <p style={{ marginTop: "15px", textAlign: "center" }}>
                Already registered? <Link to="/login">Login</Link>
            </p>
        </div>
    );
}
