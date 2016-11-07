import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SyncConfigs {
	
	private List<SyncConfig> configs = new ArrayList<>();
	
	public SyncConfigs(List<SyncConfig> configs) {
		this.configs = configs;
	}

	public SyncConfigs add(SyncConfig syncConfig) {
		List<SyncConfig> newConfigs = new ArrayList<>(configs);
		newConfigs.add(syncConfig);
		return new SyncConfigs(newConfigs);
	}
	
	public boolean isPresent(String src) {
		return configs.stream().map(SyncConfig::source).anyMatch(checkSrc -> checkSrc.equals(src));
	}

	public Optional<SyncConfig> get(String src) {
		
		if(isPresent(src)) {
			return configs.stream().filter(config -> Objects.equals(config.source(), src)).findAny();
		} 
		
		return Optional.empty();
	}
	

}
