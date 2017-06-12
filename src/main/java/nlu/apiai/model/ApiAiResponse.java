package nlu.apiai.model;

import nlu.NLUResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
public class ApiAiResponse implements NLUResponse {

    @Override
    public String getIntent() {
        // TODO: implementation
        throw new NotImplementedException();
    }

    @Override
    public Map<String, String> getEntities() {
        // TODO: implementation
        throw new NotImplementedException();
    }
}
