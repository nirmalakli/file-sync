import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
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
			
			private String append(String path) {
				return ROOT + path;
			}
			
			private String remove(String path) {
				return path.replace(ROOT, "");
			}
			
			@Override
			public boolean exists(String path) {
				return super.exists(append(path));
			}
			
			@Override
			public Stream<String> fetch(String path) {
				String appendedPath = append(path);
				Stream<String> files = super.fetch(appendedPath);
				return files.map(this::remove);
			}
			
			@Override
			public SyncResponse execute(SyncCommand command) {
				SyncCommand rebasedCmd = new SyncCommand(command.operation(), append(command.sourcePath()), append(command.destinationPath()));
				SyncResponse response = super.execute(rebasedCmd);
				return new SyncResponse(command, response.success(), response.errorMessage());
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
	
	
	@Test
	public void createCopiesANewFileIfItDoesnotExist() {
		String sourcePath = "/dir/1.txt";
		String destinationPath = "/copy/dir/1.txt";
		File destnFile = new File(ROOT + destinationPath);
		if(destnFile.exists()) {
			destnFile.delete();
		}
		SyncCommand command = new SyncCommand(SyncCommand.Operation.CREATE, sourcePath, destinationPath);
		SyncResponse response = fileService.execute(command );
		assertTrue(response.success());
		assertTrue(response.errorMessage().isEmpty());
		assertEquals(command, response.command());
	}
	
	@Test
	public void createsEdistFileIfItExists() throws IOException {
		String sourcePath = "/dir/1.txt";
		String destinationPath = "/copy/dir/1.txt";
		File destnFile = new File(ROOT + destinationPath);
		if(!destnFile.exists()) {
			destnFile.createNewFile();
		}
		SyncCommand command = new SyncCommand(SyncCommand.Operation.CREATE, sourcePath, destinationPath);
		SyncResponse response = fileService.execute(command );
		assertTrue(response.success());
		assertTrue(response.errorMessage().isEmpty());
		assertEquals(command, response.command());
	}
	
	@Test
	public void amendsAnExistingFile() {
		String sourcePath = "/dir/1.txt";
		String destinationPath = "/copy/dir/1.txt";
		SyncCommand command = new SyncCommand(SyncCommand.Operation.AMEND, sourcePath, destinationPath);
		SyncResponse response = fileService.execute(command );
		assertTrue(response.success());
		assertTrue(response.errorMessage().isEmpty());
		assertEquals(command, response.command());
	}	
}
