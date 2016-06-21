package analyser.api.analysis;

import java.util.List;

public class Document {
	private List<String> keyPhrases;
	private String id;

	public List<String> getKeyPhrases() {
		return keyPhrases;
	}

	public void setKeyPhrases(List<String> keyPhrases) {
		this.keyPhrases = keyPhrases;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
