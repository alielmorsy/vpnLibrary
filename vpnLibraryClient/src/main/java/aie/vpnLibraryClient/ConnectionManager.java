package aie.vpnLibraryClient;

import aie.vpnLibrary.messages.RequestMessage;
import aie.vpnLibrary.messages.ResponseMessage;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.messages.models.Cookie;
import aie.vpnLibrary.messages.utils.Utils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    private static ConnectionManager instance;
    private boolean withCharles = false;


    public static ConnectionManager getInstance(boolean withCharles) {
        if (instance == null) {
            instance = new ConnectionManager();
            instance.withCharles = withCharles;
        }
        return instance;
    }

    public ResponseMessage requestGET(RequestMessage request) {
        //HttpGet httpGet = new HttpGet("http://127.0.0.1/a.php");
        HttpGet httpGet = new HttpGet(request.getUrl());
        for (Map.Entry<String, String> headers :
                request.getAdditionalHeaders().entrySet()) {
            httpGet.addHeader(headers.getKey(), headers.getValue());
        }
        HttpResponse httpResponse = createRequest(httpGet, request.getCookies());

        ResponseMessage response = new ResponseMessage();
        if (httpResponse == null) {
            response.setSuccess(false);
            response.setMessage("Failed To Connect");
            return response;
        }
        response.setSuccess(true);

        if (request.getCookies().size() != 0) {
            response.setCookies(request.getCookies());
        }
        try {
            response.setData(Utils.readAllBytes(httpResponse.getEntity().getContent()));

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        httpResponse = null;
        httpGet = null;
        request = null;
        return response;
    }

    public ResponseMessage requestPOST(RequestMessage request) {
        HttpPost post = new HttpPost(request.getUrl());
        //  HttpPost post = new HttpPost("http://127.0.0.1/a.php");
        for (Map.Entry<String, String> headers :
                request.getAdditionalHeaders().entrySet()) {
            post.addHeader(headers.getKey(), headers.getValue());
        }
        ContentType contentType;
        if (request.getPostType() == PostType.FORM_DATA) {
            contentType = ContentType.APPLICATION_FORM_URLENCODED;
        } else {
            contentType = ContentType.TEXT_XML;
            post.setHeader("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
        }
        contentType.withCharset(StandardCharsets.UTF_8);
        post.setEntity(new InputStreamEntity(new ByteArrayInputStream(request.getPostContent()), contentType));
        HttpResponse httpResponse = createRequest(post, request.getCookies());
        ResponseMessage response = new ResponseMessage();
        if (httpResponse == null) {
            response.setSuccess(false);
            response.setMessage("Failed to connect");
            return response;
        }
        response.setSuccess(true);
        if (request.getCookies().size() != 0) {
            response.setCookies(request.getCookies());
        }
        try {
            response.setData(Utils.readAllBytes(httpResponse.getEntity().getContent()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        httpResponse = null;
        post = null;
        return response;
    }

    private HttpResponse createRequest(HttpRequestBase request, List<aie.vpnLibrary.messages.models.Cookie> cookies) {
        try {
            //  request.addHeader("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/msword, application/vnd.ms-powerpoint, application/vnd.ms-excel, */*");


            HttpClientContext context = new HttpClientContext();
            CookieStore cs = new BasicCookieStore();
            if (cookies.size() != 0) {


                for (Cookie c : cookies) {
                    BasicClientCookie cookie = new BasicClientCookie(c.getKey(), c.getValue());
                    cookie.setDomain(request.getURI().getHost());
                    cookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000000));
                    cs.addCookie(cookie);
                }
                context.setCookieStore(cs);
            }
            HttpClientBuilder httpClient = HttpClients.custom();

            if (withCharles) {
                httpClient.setProxy(new HttpHost("127.0.0.1", 8888));
            }
            httpClient.setUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)");
            httpClient.setSSLContext(createContext());

            HttpResponse response = httpClient.build().execute(request, context);
            //     if (response.getStatusLine().getStatusCode() == 302) hasDirect = true;
            //   if (context.getCookieStore() != null && context.getCookieStore().getCookies().size() != 0)
            cookies.clear();
            if (context.getCookieStore() != null && context.getCookieStore().getCookies().size() != 0) {
                for (org.apache.http.cookie.Cookie c : context.getCookieStore().getCookies()) {
                    cookies.add(new Cookie(c.getName(), c.getValue()));
                }
            }
            context = null;
            httpClient = null;
            cs = null;
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SSLContext createContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] arrx509Certificate, String string) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arrx509Certificate, String string) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        sslContext.init(null, new TrustManager[]{trustManager}, null);
        sslContext.getDefaultSSLParameters().setProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"});
        return sslContext;
    }

    public String getIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));

        String ip = in.readLine();
        return ip;
    }
}
