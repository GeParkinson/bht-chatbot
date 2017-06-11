package nsp;

import com.sun.mail.iap.ByteArray;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.net.ssl.HttpsURLConnection;
import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * @Author: Christopher Kümmel on 6/1/2017.
 */
public class BingSP {

    //private static Logger logger = LoggerFactory.getLogger(TelegramSendAdapter.class);

    public static void main(String[] args) {
        String accessToken = "";

        try {
            ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = resteasyClient.target("https://api.cognitive.microsoft.com/sts/v1.0/issueToken");

            Response response = target.request()
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Ocp-Apim-Subscription-Key", "526d3a6eb08f4731b14d43f136714ba3")
                    .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            System.out.println("Response: " + response.toString());
            accessToken = response.readEntity(String.class);
            response.close();
            //logger.debug("Response: " + response.toString());
            System.out.println("Access Token: " + accessToken);

        } catch (Exception e1) {
            System.out.println(e1.toString());
            //logger.error(e.toString());
        }


        try {

            HttpClient client = HttpClientBuilder.create().build();
            String url = "https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language=de-DE&locale=global&requestid=9fd09df8-d734-42e8-92a7-8cbfba7f13c3";

            String token = accessToken;
            String locale = "global";
            String instanceid = "9fd09df8-d734-42e8-92a7-8cbfba7f13c3";
            String language = "en-Us";

            // setting up post parameters
            Map<String, String> postParameters = new HashMap<>();
            postParameters.put("locale", locale);

            postParameters.put("requestid", instanceid);

            postParameters.put("language", language);

            // setting up HttpPost
            HttpPost httpPost = new HttpPost(url);

            // PARAMETERS
            List<NameValuePair> qparams = new ArrayList<>();
            for (Map.Entry<String, String> s : postParameters.entrySet()) {
                qparams.add(new BasicNameValuePair(s.getKey(), s.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(qparams));

            // HEADERS

            Map<String, String> postHeaders = new HashMap<>();
            postHeaders.put("Authorization", "Bearer " + token);
            postHeaders.put("Content-Type", "audio/wav; codec=\"audio/pcm\"; samplerate=16000");


            for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }

            // WAV FILE
            String wavFile = "src/main/resources/test.wav";

            File file = new File(wavFile);
            FileBody bin = new FileBody(file, ContentType.DEFAULT_BINARY);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("bin", bin);

            HttpEntity reqEntity = builder.build();

            //TEST
            ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
            reqEntity.writeTo(bArrOS);
            bArrOS.flush();
            ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
            bArrOS.close();

            bArrEntity.setChunked(true);
            bArrEntity.setContentEncoding(reqEntity.getContentEncoding());
            bArrEntity.setContentType(reqEntity.getContentType());

            // Set ByteArrayEntity to HttpPost
            httpPost.setEntity(bArrEntity);

            // RESPONSE
            HttpResponse response = client.execute(httpPost);

            int responseCode = response.getStatusLine().getStatusCode();

            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
        } catch (Exception e){
            System.out.println(e.toString());
        }

//        try {
//            ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
//            ResteasyWebTarget target = resteasyClient.target("https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language=de-DE&locale=global&requestid=9fd09df8-d734-42e8-92a7-8cbfba7f13c3");
//
//            byte[] audio = Files.readAllBytes(Paths.get("src/main/resources/test.wav"));
//
//            Response response = target.request()
//                    .header("Authorization", "Bearer " + accessToken)
//                    .header("Content-Type", "codec=\"audio/pcm\"; samplerate=16000")
//                    .post(Entity.entity(audio, MediaType.WILDCARD_TYPE));
//
//            System.out.println("Response: " + response.getStatus() + " - " + response.readEntity(String.class));
//            System.out.println(response.getEntity().toString());
//
//            response.close();
//        } catch (Exception e2) {
//            System.out.println(e2.toString());
//        }
    }
}
