package ru.vtb.servicemesh.test.client.config;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
public class ClientConfig {

    @Bean("non-ssl-validation")
    public RestTemplate insecureRestTemplate() {
        try {
//            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
//                    .loadTrustMaterial(null, acceptingTrustStrategy)
//                    .build();
//            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//
//            CloseableHttpClient httpClient = HttpClients.custom()
//                    .setSSLSocketFactory(csf)
//                    .build();
//            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//            requestFactory.setHttpClient(httpClient);


            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true).build();

            OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
            return new RestTemplate(okHttp3ClientHttpRequestFactory);
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            throw new RuntimeException(ex);
        }
    }
}
