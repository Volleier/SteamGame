import { ref } from "vue";
import axios from "axios";

// 不要在普通 JS 文件里直接调用 useRouter()
export const username = ref("");
export const password = ref("");

// 改成传入 router 的方式
export const handleLogin = async (router) => {
  // 测试账户
  if (username.value === "12345" && password.value === "12345") {
    alert("成功");
    router.push("/dashboard"); // 确保 router 已传入
    return;
  }

  try {
    const response = await axios.post("http://localhost:8080/", {
      username: username.value,
      password: password.value,
    });

    if (response.data.success) {
      router.push("/dashboard");
    } else {
      alert("用户名或密码错误");
    }
  } catch (error) {
    alert("登录请求失败，请稍后再试");
  }
};
