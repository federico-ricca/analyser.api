package analyser.api.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SpringBrokerAnalysisService {
	private static final String SPRING_BROKER_TEXT_ANALYSIS_LAMBDA_ENDPOINT = "spring.broker.analysis.lambda";
	private static final String SPRING_BROKER_TEXT_ANALYSIS_API_KEY = "spring.broker.analysis.api.key";

	@Autowired
	private Environment environment;

	public List<String> extractKeywords(String text) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(
				environment
						.getProperty(SPRING_BROKER_TEXT_ANALYSIS_LAMBDA_ENDPOINT));

		String query="{ \"text\": \" " + text + "\" }";
		httpPost.setEntity(new StringEntity(query));
		httpPost.setHeader("x-api-key",
				environment.getProperty(SPRING_BROKER_TEXT_ANALYSIS_API_KEY));

		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		SpringBrokerTextAnalysisResponse analysisResponse = null;

		try {
			System.out.println(response2.getStatusLine());
			HttpEntity httpEntity = response2.getEntity();
			
			ObjectMapper mapper = new ObjectMapper();
			analysisResponse = mapper.readValue(httpEntity.getContent(),
					SpringBrokerTextAnalysisResponse.class);

			EntityUtils.consume(httpEntity);
		} finally {
			response2.close();
		}

		List<String> result = new ArrayList<String>();

		if (analysisResponse != null) {
			for (Document doc : analysisResponse.getDocuments()) {
				for (String keyword : doc.getKeyPhrases()) {
					result.add(keyword);
				}
			}
		}
		return result;
	}

}
