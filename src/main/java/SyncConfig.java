
public class SyncConfig {
	private final String src;
	private final String destn;
	
	public SyncConfig(String src, String destn) {
		this.src = src;
		this.destn = destn;
	}
	
	public String source() {
		return src;
	}
	
	public String destination() {
		return destn;
	}
}
