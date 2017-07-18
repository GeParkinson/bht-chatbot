package de.bht.beuthbot.model.nlp;

import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 *
 * The base result interface for every natural language processing unit like rasa_nlu and api.ai
 */
public interface NLUResponse {

    public String getIntent();

    public Map<String, String> getEntities();
}
