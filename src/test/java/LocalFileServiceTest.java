import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class LocalFileServiceTest {

	private static final String ROOT = "/home/nirmal/work/file-sync-temp";
	private LocalFileService fileService;

	@Before
	public void setup() {
		fileService = new LocalFileService() {
			
			private String rebase(String path) {
				return ROOT + path;
			}
			
			private String unbase(String path) {
				return path.replace(ROOT, "");
			}
			
			@Override
			public boolean exists(String path) {
				return super.exists(rebase(path));
			}
			
			@Override
			public Stream<String> fetch(String path) {
				return super.fetch(rebase(path))
						.map(this::unbase);
			}
			
			@Override
			public SyncResponse execute(SyncCommand command) {
				SyncCommand rebasedCmd = new SyncCommand(command.operation(), rebase(command.sourcePath()), rebase(command.destinationPath()));
				return super.execute(rebasedCmd);
			}
		};
	}
	
	@Test	
	public void fileExists() {
		
		assertTrue(fileService.exists("/1.zip"));
	}
	
	@Test
	public void fileDoesnotExists() {
		
		assertFalse(fileService.exists("/2.zip"));
	}
	
	@Test
	public void fetch() {
		
		List<String> files = fileService.fetch("/dir").collect(Collectors.toList());
		System.out.println(files);
		assertTrue(files.contains("/dir/1.txt"));
		assertTrue(files.contains("/dir/sub-dir"));
	}
}
