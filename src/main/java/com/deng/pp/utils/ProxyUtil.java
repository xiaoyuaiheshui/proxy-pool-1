package com.deng.pp.utils;

import com.deng.pp.entity.ProxyEntity;
import com.deng.pp.fetcher.GoubanjiaFetcher;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by hcdeng on 17-7-3.
 */
public class ProxyUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);

    private static final String VERIFY_URL = "https://api.rez.gg/graphql?o=getStreams";

    public static boolean verifyProxy(ProxyEntity proxy) {
        logger.info("proxy" + proxy);
        if (proxy == null || proxy.getIp() == null) {
            return false;
        }

        return verifyProxy(proxy.getIp(), proxy.getPort());
    }


    public static boolean verifyProxy(String proxy) {

        if (proxy == null || !proxy.matches("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+"))
            return false;

        String[] ps = proxy.trim().split(":");
        return verifyProxy(ps[0], Integer.valueOf(ps[1]));
    }

    /**
     * 验证代理是否可用
     *
     * @param ip
     * @param port
     * @return
     */
    public static boolean verifyProxy(String ip, int port) {
        boolean useful;
        HttpURLConnection connection = null;
        try {
            HttpClient client=new HttpClient();
            client.getHostConfiguration().setHost(ip, port, "http");
            HttpMethod method =new OptionsMethod(VERIFY_URL);
            int statusCode = client.executeMethod(method);
                useful = statusCode == 200;
        } catch (Exception  e1) {
            logger.warn(String.format("verify proxy %s:%d exception: " + e1.getMessage(), ip, port));
            useful = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        logger.info(String.format("verify proxy %s:%d useful: " + useful, ip, port));
        return useful;
    }


    private static final String[] testProxs = new String[]{
            "113.200.159.155:9999", "119.36.92.41:80",
            "120.132.71.212:80", "61.191.41.130:80", "61.152.81.193:9100", "60.169.19.66:9000",
            "42.58.85.31:80", "61.185.137.126:3128", "118.178.124.33:3128", "5.2.69.141:1080",
            "119.252.172.133:3128", "114.115.218.66:8118", "123.138.89.130:9999", "114.115.217.19:8118",
            "117.143.109.146:80", "119.23.129.24:3128", "175.154.50.162:8118", "175.171.71.7:80",
    };

    public static void main(String[] args) {
        while (true) {
            for (String s : testProxs) {
                verifyProxy(s.split(":")[0], Integer.valueOf(s.split(":")[1]));
            }
        }
    }
}
