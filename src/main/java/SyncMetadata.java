import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SyncMetadata {

	private String syncFilePath;
	private SyncConfigs configs;

	private SyncMetadata(String filePath) {
		this.syncFilePath = filePath;
		load();
	}
	
	private void load() {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(syncFilePath);
		if(stream == null) {
			throw new IllegalArgumentException("Could not locate sync-settings file: " + syncFilePath);
		}
		
		List<SyncConfig> syncConfigs = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		
		try {
			while( (line = reader.readLine()) != null) {
				String[] tokens = line.trim().split("->");
				if(tokens.length != 2) {
					throw new RuntimeException("Incorrect format at line: " + line + "\nCorrect format is 'srcPath -> destnPath'. ");
				}
				syncConfigs.add(new SyncConfig(tokens[0].trim(), tokens[1].trim()));
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not read sync-settings file: " + syncFilePath + ". Error is: ", e);
		}

		this.configs = new SyncConfigs(syncConfigs);
	}

	public static SyncMetadata load(String path) {
		return new SyncMetadata(path);
	}

	public boolean isPresent(String src) {
		return configs.isPresent(src);
	}

	public Optional<String> getDestination(String src) {
		
		Optional<SyncConfig> syncConfig = configs.get(src);
		if(syncConfig.isPresent()) {
			return Optional.of(syncConfig.get().destination());
		} else {
			return Optional.empty();
		}
	}

}
