import java.util.stream.Stream;

public interface FileService {
	Stream<String> fetch(String path);
	boolean exists(String path);
}
