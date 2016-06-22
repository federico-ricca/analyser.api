package analyser.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import analyser.api.analysis.SpringBrokerAnalysisService;
import analyser.api.data.Article;
import analyser.api.data.ArticleRepository;
import analyser.api.data.ProcessedArticle;
import analyser.api.data.ProcessedArticleRepository;

@Component
public class ArticleImporter {
	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private ProcessedArticleRepository processedArticleRepository;

	@Autowired
	private SpringBrokerAnalysisService analysisService;

	@Autowired
	private AppState state;

	public void run() {
		System.out.println("Processing articles...");

		state.offline();

		List<Article> articles = articleRepository.getAllDocuments();
		List<ProcessedArticle> processedArticles = new ArrayList<ProcessedArticle>();

		int c = articles.size();
		int s = 0;

		if (c > 0) {
			s = c / 10;
		}

		int i = 0;

		for (Article item : articles) {
			try {
				List<String> keywords = analysisService.extractKeywords(item
						.getArticle());

				ProcessedArticle processedArticle = new ProcessedArticle();
				processedArticle.setKeywords(keywords);
				processedArticle.setArticle(item.getArticle());
				processedArticle.setAuthor(item.getAuthor());
				processedArticle.setTitle(item.getTitle());

				processedArticles.add(processedArticle);

				if (i % s == 0) {
					if (i > 0)
						System.out.println((i * 100.0) / c);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}

		System.out.println("Saving data...");
		processedArticleRepository.storeAll(processedArticles);

		state.online();

		System.out.println("Done.");
	}
}
