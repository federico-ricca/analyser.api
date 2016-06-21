package analyser.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import analyser.api.analysis.IndicoAnalysisService;
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
	private IndicoAnalysisService analysisService;

	@Autowired
	private AppState state;

	public void run() {
		System.out.println("Processing articles...");

		state.offline();

		List<Article> articles = articleRepository.getAllDocuments();
		List<ProcessedArticle> processedArticles = new ArrayList<ProcessedArticle>();

		articles.forEach(item -> {
			try {
				List<String> keywords = analysisService.extractKeywords(item
						.getArticle());

				ProcessedArticle processedArticle = new ProcessedArticle();
				processedArticle.setKeywords(keywords);
				processedArticle.setArticle(item.getArticle());
				processedArticle.setAuthor(item.getAuthor());
				processedArticle.setTitle(item.getTitle());

				processedArticles.add(processedArticle);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		processedArticleRepository.storeAll(processedArticles);

		state.online();

		System.out.println("Done.");
	}
}
