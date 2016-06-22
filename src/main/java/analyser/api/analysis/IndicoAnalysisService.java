package analyser.api.analysis;

import io.indico.Indico;
import io.indico.api.text.TextTag;
import io.indico.api.utils.IndicoException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public final class IndicoAnalysisService {
	private static final String INDICO_API_KEY = "indico.api.key";

	@Autowired
	private Environment environment;
	
	private Indico indico;

	@PostConstruct
	public void initClient() throws IndicoException {
		indico = new Indico(environment.getProperty(INDICO_API_KEY));
	}

	public List<Double> relevance(String text, List<String> keywords) throws Exception {
		List<Double> relevanceList = new ArrayList<Double>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		keywords.forEach(item -> map.put(item, item));
		
		return indico.relevance.predict(text, keywords.toArray()).getRelevance();
	}
	
	public List<String> extractKeywords(String text) throws Exception {
		if ((text == null) || (text.isEmpty())) {
			return new ArrayList<String>();
		}

		Map<TextTag, Double> result = indico.textTags.predict(text)
				.getTextTags();

		List<String> resultTags = new ArrayList<String>(result.keySet().size());

		for (TextTag tag : result.keySet()) {
			if (result.get(tag) > 0.2d) {
				resultTags.add(tag.toString());
			}
		}

		return resultTags;
	}
}
