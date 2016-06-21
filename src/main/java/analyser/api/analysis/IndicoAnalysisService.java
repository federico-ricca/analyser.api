package analyser.api.analysis;

import io.indico.Indico;
import io.indico.api.text.TextTag;
import io.indico.api.utils.IndicoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public final class IndicoAnalysisService {
	private static final String INDICO_API_KEY = "indico.api.key";

	@Autowired
	private Environment environment;
	
	private Indico indico;

	private void initClient() throws IndicoException {
		if (indico == null) {
			indico = new Indico(environment.getProperty(INDICO_API_KEY));
		}
	}

	public List<String> extractKeywords(String text) throws Exception {
		this.initClient();

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
