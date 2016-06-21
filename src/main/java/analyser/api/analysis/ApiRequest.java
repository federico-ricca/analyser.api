package analyser.api.analysis;

import java.util.ArrayList;
import java.util.List;

public class ApiRequest {
	public class Document {
		private String language;

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		private String id;
		private String text;

	}

	private List<ApiRequest.Document> documents = new ArrayList<ApiRequest.Document>();

	public void addText(String text) {
		ApiRequest.Document doc = new ApiRequest.Document();
		doc.setText(text);
		doc.setLanguage("en");
		doc.setId("string");
		getDocuments().add(doc);
	}

	public List<ApiRequest.Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ApiRequest.Document> documents) {
		this.documents = documents;
	}

}
