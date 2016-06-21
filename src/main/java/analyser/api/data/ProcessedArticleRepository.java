package analyser.api.data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public final class ProcessedArticleRepository {
	private static final String INDEX_NAME = "articles";
	private static final String TYPE_NAME = "processed_articles";

	private Client client;

	@Autowired
	Environment environment;

	private void initClient() {
		if (client == null) {
			client = TransportClient
					.builder()
					.build()
					.addTransportAddress(
							new InetSocketTransportAddress(
									new InetSocketAddress(
											environment
													.getProperty("elasticsearch.host"),
											Integer.parseInt(environment
													.getProperty("elasticsearch.port")))));
		}
	}

	public void store(ProcessedArticle processedArticle) {
		this.initClient();

		IndexResponse response;
		try {
			response = client
					.prepareIndex(INDEX_NAME, TYPE_NAME)
					.setSource(
							XContentFactory
									.jsonBuilder()
									.startObject()
									.field("author",
											processedArticle.getAuthor())
									.field("title", processedArticle.getTitle())
									.field("keywords",
											processedArticle.getKeywords())
									.field("article",
											processedArticle.getArticle())
									.endObject()).get();
			if (!response.isCreated()) {
				System.out.println("Warning, object not created: "
						+ processedArticle);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void storeAll(List<ProcessedArticle> processedArticles) {
		this.initClient();

		for (ProcessedArticle article : processedArticles) {
			this.store(article);
		}
	}

}
