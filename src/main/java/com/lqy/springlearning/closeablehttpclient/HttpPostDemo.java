package com.lqy.springlearning.closeablehttpclient;

import com.sun.deploy.net.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName HttpPostDemo
 * @Description
 * @Author lengqy
 * @Date 2024年12月20日 00:11
 * @Version 1.0
 */
public class HttpPostDemo {
    public static void main(String[] args) {

    }

    /**
     * 方式一：自动关闭资源
     * @param url
     * @return
     */
    public static String httpPost(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();

        try(CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpPost httpPost = new HttpPost(url);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            // 设置请求体
            String jsonPayload = "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }";
            StringEntity entity = new StringEntity(jsonPayload);
            httpPost.setEntity(entity);

            // 执行请求
            System.out.println("Executing POST request to: " + url);
            try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // 获取响应状态
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Response Status Code: " + statusCode);

                // 获取响应内容
                if (statusCode == HttpStatus.SC_CREATED) {// HTTP 201 表示资源创建成功
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.printf("Response body: " + responseBody);
                } else {
                    System.out.printf("Unexpected response status: " + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 方式二：手动关闭资源
     * @param url
     * @return
     */
    public static String httpPost2(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build(); // 创建httpClient对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");
        StringEntity requestEntity = new StringEntity("{\"name\":\"zhangsan\",\"age\":18}", "UTF-8");
        httpPost.setEntity(requestEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, "UTF-8");
                return resultStr;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
