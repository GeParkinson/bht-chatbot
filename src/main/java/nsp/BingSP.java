package nsp;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.print.attribute.standard.Media;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;


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
                    .header("Ocp-Apim-Subscription-Key", "4624509bd77d42f4b6b00b08900d5da6")
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
            ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = resteasyClient.target("https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language=de-DE&locale=global&requestid=9fd09df8-d734-42e8-92a7-8cbfba7f13c3");

            MultipartFormDataOutput mdo = new MultipartFormDataOutput();
            mdo.addFormData("binary", new ByteArrayInputStream(FileUtils.readFileToByteArray(new File("src/main/resources/test.wav"))), MediaType.MULTIPART_FORM_DATA_TYPE);
            //mdo.addFormData("binary", new FileInputStream("src/main/resources/test.wav"), MediaType.MULTIPART_FORM_DATA_TYPE, "test.wav");
            GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(mdo) {
            };


            Response response = target.request()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-type", "audio/wav; codec=\"audio/pcm\"; samplerate=16000")
                    .post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA));

            response.close();
        } catch (Exception e2) {
            System.out.println(e2.toString());
        }
    }
}
