package analyser.api;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class AppState {
	private AtomicBoolean online = new AtomicBoolean(false);

	public void offline() {
		online.set(false);
	}

	public void online() {
		online.set(true);
	}

	public boolean isOnline() {
		return online.get();
	}

	public boolean isOffline() {
		return !this.isOnline();
	}
}
