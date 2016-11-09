import java.io.File;
import java.util.stream.Stream;

public class LocalFileService implements FileService {

	@Override
	public Stream<String> fetch(String path) {
		File file = new File(path);
		if(!file.exists()) {
			System.err.println("File : " + path + " does not exists!!");
			return Stream.empty();
		}
		return Stream.of(file.list());
	}

	@Override
	public boolean exists(String path) {
		return new File(path).exists();
	}

}
