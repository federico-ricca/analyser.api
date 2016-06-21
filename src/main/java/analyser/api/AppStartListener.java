package analyser.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class AppStartListener implements
		ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ArticleImporter importer;

	public void onApplicationEvent(ContextRefreshedEvent event) {
		// don't run importer on startup
		// importer.run();
	}
}
