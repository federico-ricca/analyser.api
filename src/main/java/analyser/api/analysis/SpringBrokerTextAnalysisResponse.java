package analyser.api.analysis;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpringBrokerTextAnalysisResponse {
	private List<Document> documents;
	private List<AnalysisError> errors;

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public List<AnalysisError> getErrors() {
		return errors;
	}

	public void setErrors(List<AnalysisError> errors) {
		this.errors = errors;
	}
}
