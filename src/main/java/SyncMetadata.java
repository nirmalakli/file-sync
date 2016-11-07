import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SyncMetadata {

	private String syncFilePath;
	private Map<String, String> configs;

	private SyncMetadata(String filePath) {
		this.syncFilePath = filePath;
		this.configs = new HashMap<>();
		load();
	}
	
	private void load() {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(syncFilePath);
		if(stream == null) {
			throw new IllegalArgumentException("Could not locate sync-settings file: " + syncFilePath);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		
		try {
			while( (line = reader.readLine()) != null) {
				String[] tokens = line.trim().split("->");
				if(tokens.length != 2) {
					throw new RuntimeException("Incorrect format at line: " + line + "\nCorrect format is 'srcPath -> destnPath'. ");
				}
				configs.put(tokens[0].trim(), tokens[1].trim());
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not read sync-settings file: " + syncFilePath + ". Error is: ", e);
		}
	}

	public static SyncMetadata load(String path) {
		return new SyncMetadata(path);
	}

	public boolean isPresent(String src) {
		return configs.keySet().contains(src);
	}

	public Optional<String> getDestination(String src) {
		return isPresent(src) ? Optional.of(configs.get(src)) : Optional.empty();
	}

}
