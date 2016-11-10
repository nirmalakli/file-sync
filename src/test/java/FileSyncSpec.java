import static org.junit.Assert.*;

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

			@Override
			public SyncResponse execute(SyncCommand command) {
				boolean status = command.sourcePath().contains("pdf");
				String error = status ? "Success" : "Failure";
				return new SyncResponse(command, status, error);
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
	
	@Test
	public void execute() {
		
		List<SyncCommand> operations = fileSync.sync("/home/nirmal");
		List<SyncResponse> result = fileSync.execute(operations);
		
		assertEquals(String.format("There should be %d sync responses ", operations.size()),  operations.size(), result.size());
		
		SyncCommand command = pickCommand(operations, "/home/nirmal/Documents/1.pdf");	
		SyncResponse response1 = pickResponse(result, command);
		assertNotNull(response1);
		assertTrue("Response should be successfull", response1.success());
		assertEquals("Response should be successfull", "Success", response1.errorMessage());
	}


	private SyncCommand pickCommand(List<SyncCommand> operations, String sourcePath) {
		return operations.stream()
				.filter(cmd -> Objects.equals(sourcePath, cmd.sourcePath()))
				.findAny()
				.orElseThrow(() -> new RuntimeException("Could not find command for sourcePath = '" + sourcePath + "'"));
	}

	private SyncResponse pickResponse(List<SyncResponse> result, SyncCommand command) {
		return result.stream()
				.filter(r -> Objects.equals(command, r.command()))
				.findAny()
				.orElseThrow(() -> new RuntimeException("Could not find response for " + command));
	}
}
