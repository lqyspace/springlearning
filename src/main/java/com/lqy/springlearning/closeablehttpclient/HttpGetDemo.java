package com.lqy.springlearning.closeablehttpclient;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @ClassName HttpGetDemo
 * @Description
 * @Author lengqy
 * @Date 2024年12月19日 21:33
 * @Version 1.0
 */
public class HttpGetDemo {
    public static void main(String[] args) {
        httpGet("http://localhost:8080/qw/v1/qwAgent/test");
        System.out.println("***********************************************");
        System.out.println(httpGet2("http://localhost:8080/qw/v1/qwAgent/test"));
    }

    /**
     * 方式一：使用 try-with-resources
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        // 单位毫秒
        RequestConfig requestConfig = RequestConfig.custom() // 创建一个自定义的RequestConfig.Builder对象，用于灵活设置HTTP请求的设置。
                .setConnectionRequestTimeout(3000) // 设置从连接池中获取连接的最大等待时间，单位：毫秒
                .setConnectTimeout(3000) // 设置客户端与目标服务器建立连接的最大等待时间，单位：毫秒
                .setSocketTimeout(3000).build(); // 设置从服务器读取数据的超时时间，即数据包最大的间隔时间，单位：毫秒
        /**
         * 使用场景：
         * 1、setConnectionRequestTimeout(3000): 当Http客户端使用连接池管理多个连接时，如果所有连接都已被占用且新的请求需要等待连接释放，
         * 该参数定义了客户端等待连接的最大时间段，超过这个时间则抛出异常 ConnectionPoolTimeoutException。
         * 2、setConnectionTimeout(3000): 适用于TCP连接的握手阶段，如果服务器没有在规定时间内响应，将抛出 ConnectionTimeoutException
         * 3、setSocketTimeout(3000): 适用于读取响应数据时，如果服务器在规定时间内没有发送数据，将抛出 SocketTimeoutException。
         * 4、build(): 构建并返回一个RequestConfig对象，该对象包含了上述设置的参数。
         */

        // 使用 try-with-resources 自动关闭 CloseableHttpClient 和 CloseableHttpResponse，避免资源泄露。
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build())
        {
            /**
             * 使用场景：
             * 1、setDefaultRequestConfig(requestConfig): 设置默认的请求配置，即在创建HttpClient实例时，指定了默认的请求配置参数。
             * 2、build(): httpClient发出的每一个请求都会遵循上述的超时配置。
             */

            HttpGet httpGet = new HttpGet(url);
            // 设置请求头（可选）
            httpGet.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            // 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // 获取响应状态
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Response Status Code: " + statusCode);

                // 获取响应内容
                if (statusCode == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.printf("Response body: " + responseBody);
                } else {
                    System.out.printf("Unexpected response status: " + statusCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    public static String httpGet2(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            } else {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return null;
    }
}
