package analyser.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import analyser.api.analysis.SpringBrokerAnalysisService;
import analyser.api.data.ArticleRepository;

@RestController
public class ArticleController {

	@Autowired
	// private IndicoAnalysisService analysisService;
	private SpringBrokerAnalysisService analysisService;

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private AppState state;

	@Autowired
	private ArticleImporter articleImporter;

	@RequestMapping(value = "/v1/importArticles", method = RequestMethod.GET)
	public void importArticles() {
		articleImporter.run();
	}

	@RequestMapping(value = "/v1/analyse", method = RequestMethod.POST)
	public ResponseEntity<AnalysisResponse> analyseContent(
			@RequestBody Content content) {
		AnalysisResponse response = new AnalysisResponse();

		try {
			List<String> keywords = analysisService.extractKeywords(content
					.getText());

			response.setKeywords(keywords);
			response.setStatus("ok");

		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage(e.getMessage());
		}

		return new ResponseEntity<AnalysisResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/articles", method = RequestMethod.GET)
	public ResponseEntity<Content> fetchArticles() {
		if (state.isOffline()) {
			return new ResponseEntity<Content>((Content) null,
					HttpStatus.I_AM_A_TEAPOT);
		}

		Content content = new Content();

		return new ResponseEntity<Content>(content, HttpStatus.OK);
	}
}
