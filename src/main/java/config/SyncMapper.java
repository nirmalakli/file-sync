package config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyncMapper {

	private final Node tree;
	
	public SyncMapper() {
		tree = new Node();
		tree.key = "/";
		tree.value=null;
		tree.parent = null;
	}
	
	
	public void add(SyncConfig config) {
		String source = config.source();
		List<String> dirs =  Stream.of(source.split("/")).filter(dir -> !dir.isEmpty()).collect(Collectors.toList());
		Node node = tree;
		for(String dir : dirs) {
			Optional<Node> result = Node.search(node, dir);
			if(!result.isPresent()) {
				Node parent = node;
				Node child = Node.newNode(dir, null, parent, null);
				
				// If parent is marked
				if(parent.marked || parent.markInheritedFrom != null) {
					child.markInheritedFrom = parent.marked ? parent : parent.markInheritedFrom;
					child.parentMarked = true;
				}
				node = child;
			} else {
				node = result.get();
			}
		}
		node.value = config.destination();
		node.marked = true;
		node.parentMarked = false;
		node.markInheritedFrom = null;
		
		
		inheritMarks(tree);
	}
	
	private void inheritMarks(Node node) {

		boolean isMarked = node.marked || node.parentMarked;
		
		for(Node child : node.children) {
			boolean isChildMarked = child.marked || child.parentMarked;
			
			if(isMarked && !isChildMarked) {
				child.parentMarked = true;
				if(node.marked) {
					child.markInheritedFrom = node;
				} else {
					child.markInheritedFrom = node.markInheritedFrom;
				}
			}
			
			inheritMarks(child);
		}
	}
	
	public Optional<String> destination(String srcPath) {
		Optional<Node> result = Node.closestMarkedNode(tree, srcPath);
		if(result.isPresent()) {
			Node node = result.get();
			if(node.marked || node.parentMarked) {
				Node nearestMarkedNode = node.marked ? node : node.markInheritedFrom;
				
				String destinationOfMarkedNode = nearestMarkedNode.value;
				return Optional.of(srcPath.replace(nearestMarkedNode.path(), destinationOfMarkedNode));
			} 
		} 
		
		return Optional.empty();
	}

	public static SyncMapper create(SyncMetadata metadata) {
		SyncMapper syncMapper = new SyncMapper();
		for(SyncConfig config : metadata.configs() ) {
			syncMapper.add(config);
		}
		return syncMapper;
	}
	
	public Node root() {
		return tree;
	}
}
