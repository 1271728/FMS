import axios, { AxiosError } from "axios";
import { ElMessage } from "element-plus";

const TOKEN_KEY = "FMS_TOKEN";

export const http = axios.create({
  baseURL: "/api",
  timeout: 15000,
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers = config.headers ?? {};
    // Sa-Token tokenName=token
    config.headers["token"] = token;
  }
  return config;
});

http.interceptors.response.use(
  (resp) => {
    const body = resp.data;
    if (body && typeof body === "object" && "success" in body) {
      if (body.success) return body.data;
      ElMessage.error(body.message || "请求失败");
      return Promise.reject(new Error(body.message || "请求失败"));
    }
    return resp.data;
  },
  (err: AxiosError<any>) => {
    const status = err.response?.status;
    if (status === 401) ElMessage.error("未登录或登录已过期");
    else ElMessage.error(err.response?.data?.message || err.message || "网络错误");
    return Promise.reject(err);
  }
);

export function setToken(tokenValue: string) {
  localStorage.setItem(TOKEN_KEY, tokenValue);
}
export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}
