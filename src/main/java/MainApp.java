
public class MainApp {
	
	public static void main(String[] args) {
		SyncMetadata metadata = SyncMetadata.load("sync-settings.txt");
		System.out.println(metadata.getDestination("/home/nirmal/Documents").get());
	}

}
