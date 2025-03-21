import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

export const username = ref("");
export const password = ref("");

export const handleLogin = async () => {
  const router = useRouter();

  // 测试账户
  if (username.value === "12345" && password.value === "12345") {
    alert("成功");
    router.push("/dashboard");
    return;
  }

  try {
    const response = await axios.post("http://localhost:8080/", {
      username: username.value,
      password: password.value,
    });

    console.log("后端响应:", response.data);

    if (response.data.success) {
      console.log("登录成功，跳转到 Dashboard");
      router.push("/dashboard");
    } else {
      console.log("登录失败，用户名或密码错误");
      alert("用户名或密码错误");
    }
  } catch (error) {
    console.error("登录请求失败:", error);
    alert("登录请求失败，请稍后再试");
  }
};
