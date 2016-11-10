import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class LocalFileService implements FileService {

	@Override
	public Stream<String> fetch(String path) {
		File file = new File(path);
		if(!file.exists()) {
			System.err.println("File : " + path + " does not exists!!");
			return Stream.empty();
		}
		return Stream.of(file.listFiles())
			.map(File::getAbsolutePath);
	}

	@Override
	public boolean exists(String path) {
		return new File(path).exists();
	}

	@Override
	public SyncResponse execute(SyncCommand command) {
		boolean status = false;
		String error = "Not implemented yet";
		
		File srcFile = new File(command.sourcePath());
		File destnFile = new File(command.destinationPath());
		try {
			switch(command.operation()) {
				
				case CREATE:
					if(!destnFile.createNewFile()) {
						status = false;
						error = "Could not create destination file";
					} else {
						Files.copy(new FileInputStream(srcFile), Paths.get(destnFile.toURI()), StandardCopyOption.REPLACE_EXISTING);				
					}
					break;
				
				case AMEND:
					Files.copy(new FileInputStream(srcFile), Paths.get(destnFile.toURI()), StandardCopyOption.REPLACE_EXISTING);									
					break;
					
				default:
					throw new IllegalArgumentException("Only Create and Amend operations are supported");
			}
			
		} catch (IOException e) {
			status = false;
			error = e.getMessage();
			e.printStackTrace();
		}
		
		return new SyncResponse(command, status, error);
	}

}
