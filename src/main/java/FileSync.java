import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import config.SyncMapper;
import config.SyncMetadata;

public class FileSync {

	private final SyncMetadata metadata;
	private final FileService fileService;
	private final SyncMapper syncMapper;

	FileSync(SyncMetadata metadata, FileService fileService) {
		this.metadata = metadata;
		this.fileService = fileService;
		this.syncMapper = SyncMapper.create(this.metadata); 
	}

	public static FileSync load(String path) {
		SyncMetadata metadata = SyncMetadata.load(path);
		return new FileSync(metadata, new LocalFileService());
	}

	public List<String> items(String sourceRootPath) {
		return fileService.fetch(sourceRootPath)
				.collect(Collectors.toList());
	}
	
	public List<SyncCommand> sync(String path) {
		
		return items(path).stream()
				.map(this::createSyncOperation)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	private Optional<SyncCommand> createSyncOperation(String syncSourcePath) {
		Optional<String> result = syncMapper.destination(syncSourcePath);
		if(!result.isPresent()) {
			return Optional.empty();
		}
		
		String destination = result.get();
		SyncCommand.Operation operation = fileService.exists(destination) ? SyncCommand.Operation.AMEND : SyncCommand.Operation.CREATE;
		SyncCommand syncOperation = new SyncCommand(operation, syncSourcePath, destination);
		return Optional.of(syncOperation);
	}

	public List<SyncResponse> execute(List<SyncCommand> operations) {
		
		return operations.stream()
			.map(this::fetchResponse)
			.collect(Collectors.toList());
	}

	private SyncResponse fetchResponse(SyncCommand command) {
		
		return fileService.execute(command);
	}
}

class SyncCommand {
	enum Operation {
		CREATE,
		AMEND
	}
	
	private final Operation operation;
	private final String sourcePath;
	private final String destinationPath;
	
	public SyncCommand(Operation operation, String sourcePath, String destinationPath) {
		super();
		this.operation = operation;
		this.sourcePath = sourcePath;
		this.destinationPath = destinationPath;
	}
	
	public Operation operation() {
		return operation;
	}
	
	public String sourcePath() {
		return sourcePath;
	}
	
	public String destinationPath() {
		return destinationPath;
	}

	@Override
	public String toString() {
		return String.format("SyncCommand [operation=%s, sourcePath=%s, destinationPath=%s]", operation, sourcePath,
				destinationPath);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationPath == null) ? 0 : destinationPath.hashCode());
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((sourcePath == null) ? 0 : sourcePath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyncCommand other = (SyncCommand) obj;
		if (destinationPath == null) {
			if (other.destinationPath != null)
				return false;
		} else if (!destinationPath.equals(other.destinationPath))
			return false;
		if (operation != other.operation)
			return false;
		if (sourcePath == null) {
			if (other.sourcePath != null)
				return false;
		} else if (!sourcePath.equals(other.sourcePath))
			return false;
		return true;
	}
}

class SyncResponse {
	private final SyncCommand command;
	private final boolean status;
	private final String error;
	
	public SyncResponse(SyncCommand command, boolean status, String error) {
		this.command = command;
		this.status = status;
		this.error = error;
	}
	
	public boolean success() {
		return status;
	}
	
	public boolean failed() {
		return !success();
	}
	
	public String errorMessage() {
		return error;
	}
	
	public SyncCommand command() {
		return command;
	}
}

