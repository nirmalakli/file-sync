import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

import config.SyncMetadata;

public class FileSyncSpec {

	private static FileService mockFileService;
	private static FileSync fileSync;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mockFileService = new FileService() {
			
			@Override
			public Stream<String> fetch(String path) {
				return Stream.of("/home/nirmal/Documents/1.pdf", "/home/nirmal/Documents/Personal/Wife/2.pdf", "/home/nirmal/Videos/Movies/English/Godfather.avi");
			}
			
			@Override
			public boolean exists(String path) {
				return path.contains("pdf");
			}
		};
		fileSync = new FileSync(SyncMetadata.load("sync-settings.txt"), mockFileService);
	}


	@Test
	public void syncAmendsExistingFiles() {
		
		List<SyncCommand> operations = fileSync.sync("/home/nirmal");
		
		SyncCommand command = pickCommand(operations, "/home/nirmal/Documents/1.pdf");		
		assertEquals(SyncCommand.Operation.AMEND, command.operation());
		assertEquals("/media/hdd-ntfs/nirmal/pc/Documents/1.pdf", command.destinationPath());
		
		command = pickCommand(operations, "/home/nirmal/Documents/Personal/Wife/2.pdf");		
		assertEquals(SyncCommand.Operation.AMEND, command.operation());
		assertEquals("/media/hdd-ntfs/pooja/pc/Documents/2.pdf", command.destinationPath());
	}
	
	@Test
	public void syncCreatesNonExistingFiles() {
		
		List<SyncCommand> operations = fileSync.sync("/home/nirmal");
		
		SyncCommand command = pickCommand(operations, "/home/nirmal/Videos/Movies/English/Godfather.avi");		
		assertEquals(SyncCommand.Operation.CREATE, command.operation());
		assertEquals("/media/hdd-ntfs/nirmal/Videos/Movies/English/Godfather.avi", command.destinationPath());
	}


	private SyncCommand pickCommand(List<SyncCommand> operations, String sourcePath) {
		return operations.stream()
				.filter(cmd -> Objects.equals(sourcePath, cmd.sourcePath()))
				.findAny()
				.orElseThrow(() -> new RuntimeException("Could not find command for sourcePath = '" + sourcePath + "'"));
	}

}
