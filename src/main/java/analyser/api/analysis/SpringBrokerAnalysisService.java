package analyser.api.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SpringBrokerAnalysisService {
	private static final String SPRING_BROKER_TEXT_ANALYSIS_LAMBDA_ENDPOINT = "spring.broker.analysis.lambda";
	private static final String SPRING_BROKER_TEXT_ANALYSIS_API_HEADER = "spring.broker.analysis.api.header";
	private static final String SPRING_BROKER_TEXT_ANALYSIS_API_KEY = "spring.broker.analysis.api.key";

	@Autowired
	private Environment environment;

	@Autowired
	private IndicoAnalysisService indicoAnalysisService;
	
	public List<String> extractKeywords(String text) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(
				environment
						.getProperty(SPRING_BROKER_TEXT_ANALYSIS_LAMBDA_ENDPOINT));
		String plainText = new HtmlToPlainText()
				.getPlainText(Jsoup.parse(text));
		plainText = StringEscapeUtils.escapeJson(plainText);
		plainText = plainText.substring(0,
				plainText.length() < 9000 ? plainText.length() : 9000);

		ApiRequest apiRequest = new ApiRequest();
		apiRequest.addText(plainText);
		ObjectMapper jsonMapper = new ObjectMapper();
		String query = jsonMapper.writeValueAsString(apiRequest);

		System.out.println(query);
		httpPost.setEntity(new StringEntity(query));
		httpPost.setHeader(
				environment.getProperty(SPRING_BROKER_TEXT_ANALYSIS_API_HEADER),
				environment.getProperty(SPRING_BROKER_TEXT_ANALYSIS_API_KEY));
		httpPost.setHeader("Content-Type", "application/json; charset=utf-8");

		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		SpringBrokerTextAnalysisResponse analysisResponse = null;

		boolean error = false;

		try {
			HttpEntity httpEntity = response2.getEntity();

			if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				ObjectMapper mapper = new ObjectMapper();
				analysisResponse = mapper.readValue(httpEntity.getContent(),
						SpringBrokerTextAnalysisResponse.class);
			} else {
				System.out.println(response2.getStatusLine() + "; "
						+ response2.getStatusLine().getReasonPhrase());
				error = true;
			}
			EntityUtils.consume(httpEntity);
		} finally {
			response2.close();
		}

		List<String> result = new ArrayList<String>();

		if (!error) {
			if (analysisResponse != null) {
				for (Document doc : analysisResponse.getDocuments()) {
					for (String keyword : doc.getKeyPhrases()) {
						result.add(keyword);
					}
				}
				
				List<Double> relevanceList = indicoAnalysisService.relevance(plainText, result);
				
				relevanceList.forEach(d -> {
					System.out.println("relevance: " + d);
				});
			}
		}

		return result;
	}

}
