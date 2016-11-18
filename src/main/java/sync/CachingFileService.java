package sync;


import java.util.stream.Stream;

public class CachingFileService implements FileService {

    private SyncResponse cachedResponse = null;


    private final FileService fileService;

    public CachingFileService(FileService fileService) {
        this.fileService = fileService;
    }


    @Override
    public Stream<String> fetch(String path) {
        return fileService.fetch(path);
    }

    @Override
    public boolean exists(String path) {
        return fileService.exists(path);
    }

    @Override
    public SyncResponse execute(SyncCommand command) {

        if(cachedResponse != null) {
            return cachedResponse;
        } else {
            cachedResponse = fileService.execute(command);
            return cachedResponse;
        }
    }
}
