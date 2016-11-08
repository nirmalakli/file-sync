package config;

public class MainApp {
	
	public static void main(String[] args) {
		SyncMetadata metadata = SyncMetadata.load("sync-settings.txt");
		System.out.println("Loaded metadata:: \n" + metadata);
		SyncMapper syncMapper = SyncMapper.create(metadata);
		String[] srcPaths = new String[]{
				"/home/nirmal/file1.pdf",
				"/home/nirmal/Documents/file1.pdf",
				"/home/nirmal/Documents/Personal/file2.pdf",
				"/home/nirmal/Documents/Personal/Wife/file2.pdf",
				"/home/nirmal/Videos/file3.pdf",
				"/home/nirmal/Videos/Movies/file3.pdf"
		};
		
		for(String srcPath : srcPaths) {
			String destnPath = syncMapper.destination(srcPath).orElse("NA");
			System.out.printf(" %s -> %s%n", srcPath, destnPath);
		}
	}

}
