import jms.MessageQueue;
import message.Message;
import message.Messenger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * A simple REST service which is able to say hello to someone using HelloService Please take a look at the web.xml where JAX-RS
 * is enabled.
 *
 * @author gbrey@redhat.com
 */

@Path("/")
public class HelloWorld {
    @Inject
    HelloService helloService;

    @Inject
    MessageQueue messageQueue;

    @GET
    @Path("/json")
    @Produces({"application/json"})
    public String getHelloWorldJSON() {
        Message emptyMessage = new Message();
        emptyMessage.setId(1L);
        emptyMessage.setText(helloService.createHelloMessage("World"));
        emptyMessage.setMessenger(Messenger.TELEGRAM);
        messageQueue.addInMessage(emptyMessage);
        return "{\"result\":\"" + helloService.createHelloMessage("World") + "\"}";
    }

    @GET
    @Path("/xml")
    @Produces({"application/xml"})
    public String getHelloWorldXML() {
        return "<xml><result>" + helloService.createHelloMessage("World") + "</result></xml>";
    }

}
