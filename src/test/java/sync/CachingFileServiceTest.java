package sync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CachingFileServiceTest {

	private static final String ROOT = "/home/nirmal/work/file-sync-temp";
	private LocalFileService localFileService;
	private CachingFileService cachingFileService;

	@Mock
	private FileService fileService;

	@Mock
	private SyncCommand syncCommand;

	@Mock
	private SyncResponse syncResponse;

	@Before
	public void setup() {
		localFileService = new LocalFileService() {
			
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

		cachingFileService = new CachingFileService(localFileService);
	}
	

	@Test
	public void cachedResponseIsReturned() throws IOException {

		when(fileService.execute(syncCommand)).thenReturn(syncResponse);

		CachingFileService cachingFileService = new CachingFileService(fileService);
		SyncResponse syncResponseFirst = cachingFileService.execute(syncCommand);
		assertEquals(syncResponse, syncResponseFirst);

		verify(fileService).execute(syncCommand);

		SyncResponse syncResponseSecond = cachingFileService.execute(syncCommand);
		assertEquals(syncResponse, syncResponseFirst);
		verifyNoMoreInteractions(fileService);
	}

	// TODO: Fix this test case by adding caching logic
	@Test
	public void responseIsRefetchedOnDestinationFileMissing() throws IOException {

		when(fileService.execute(syncCommand)).thenReturn(syncResponse);

		CachingFileService cachingFileService = new CachingFileService(fileService);
		SyncResponse syncResponseFirst = cachingFileService.execute(syncCommand);
		assertEquals(syncResponse, syncResponseFirst);

		deleteDestination();

		SyncResponse syncResponseSecond = cachingFileService.execute(syncCommand);
		assertEquals(syncResponse, syncResponseFirst);
		verify(fileService, times(2)).execute(syncCommand);
	}

	private void deleteDestination() {
		String sourcePath = "/some/source/path";
		String destinationPath = "/some/destination/path";
		when(syncCommand.sourcePath()).thenReturn(sourcePath);
		when(syncCommand.destinationPath()).thenReturn(destinationPath);
		when(fileService.exists(destinationPath)).thenReturn(false);
	}
}
