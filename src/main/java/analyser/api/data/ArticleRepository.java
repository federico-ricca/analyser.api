package analyser.api.data;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ArticleRepository {
	private static final String INDEX_NAME_KEY = "elastic.source.index.articles";
	private static final String TYPE_NAME_KEY = "elastic.source.index.articles.types.raw_articles";

	private Client client;

	@Autowired
	Environment environment;

	private String indexName;
	private String typeName;

	@PostConstruct
	public void initClient() {
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

		indexName = environment.getProperty(INDEX_NAME_KEY);
		typeName = environment.getProperty(TYPE_NAME_KEY);

		System.out.println("Using " + INDEX_NAME_KEY + " = " + indexName);
		System.out.println("Using " + TYPE_NAME_KEY + " = " + typeName);
	}

	public List<Article> getAllDocuments() {
		int scrollSize = 5000;

		List<Article> esData = new ArrayList<Article>();
		SearchResponse response = null;
		int i = 0;
		ObjectMapper mapper = new ObjectMapper();

		while (response == null || response.getHits().hits().length != 0) {
			response = client.prepareSearch(indexName).setTypes(typeName)
					.setQuery(QueryBuilders.matchAllQuery())
					.setSize(scrollSize).setFrom(i * scrollSize).execute()
					.actionGet();
			for (SearchHit hit : response.getHits()) {
				try {
					esData.add(mapper.convertValue(hit.getSource(),
							Article.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			i++;
		}

		return esData;
	}

	public void store(Article article) throws Exception {
		IndexResponse response = client
				.prepareIndex(indexName, typeName)
				.setSource(
						XContentFactory.jsonBuilder().startObject()
								.field("author", article.getAuthor())
								.field("title", article.getTitle())
								.field("article", article.getArticle())
								.endObject()).get();
		if (!response.isCreated()) {
			System.out.println("Warning, object not created: " + article);
		}
	}

}
