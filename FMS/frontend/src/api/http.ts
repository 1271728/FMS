import axios, { AxiosError } from "axios";
import { ElMessage } from "element-plus";

const TOKEN_KEY = "FMS_TOKEN";

interface ApiEnvelope<T = any> {
  code: number;
  success: boolean;
  message: string;
  data: T;
  traceId?: string;
}

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

function buildErrorMessage(body: Partial<ApiEnvelope<any>> | undefined, fallback: string) {
  const message = body?.message || fallback;
  const code = body?.code;
  const traceId = body?.traceId;
  const codePart = code != null && code !== 0 ? ` [code=${code}]` : "";
  const tracePart = traceId ? ` [traceId=${traceId}]` : "";
  return `${message}${codePart}${tracePart}`;
}

http.interceptors.response.use(
  (resp) => {
    const body = resp.data as ApiEnvelope<any>;
    if (body && typeof body === "object" && "success" in body) {
      if (body.success) return body.data;
      const tip = buildErrorMessage(body, "请求失败");
      if (body.code === 401) ElMessage.error("未登录或登录已过期，请重新登录");
      else if (body.code === 403) ElMessage.error("无权限执行该操作");
      else if (body.code === 409) ElMessage.warning(tip);
      else ElMessage.error(tip);
      return Promise.reject(new Error(tip));
    }
    return resp.data;
  },
  (err: AxiosError<any>) => {
    const status = err.response?.status;
    const body = err.response?.data as Partial<ApiEnvelope<any>> | undefined;
    const tip = buildErrorMessage(body, err.message || "网络错误");
    if (status === 401) ElMessage.error("未登录或登录已过期，请重新登录");
    else if (status === 403) ElMessage.error("无权限执行该操作");
    else ElMessage.error(tip);
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
