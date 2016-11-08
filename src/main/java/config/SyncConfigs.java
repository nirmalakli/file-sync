package config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SyncConfigs {
	
	private List<SyncConfig> configs = new ArrayList<>();
	
	public SyncConfigs(List<SyncConfig> configs) {
		if(configs == null) {
			throw new IllegalArgumentException("configs can not be null");
		}
		this.configs = configs;
	}

	public SyncConfigs add(SyncConfig syncConfig) {
		if(syncConfig == null) {
			throw new IllegalArgumentException("syncConfig can not be null");
		}
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
	
	public Collection<SyncConfig> configs() {
		return configs;
	}
	
	@Override
	public String toString() {
		return configs.toString();
	}
	

}
