import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyncMapper {

	Node tree;
	
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
			Optional<Node> result = Node.get(node, dir);
			if(!result.isPresent()) {
				Node parent = node;
				Node child = Node.newNode(dir, null, parent, null);
				
				// If parent is marked
				if(parent.marked || parent.markInheritedFrom != null) {
					child.marked = false;
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
	}
	
	public Optional<String> get(String srcPath) {
		Optional<Node> result = Node.get(tree, srcPath);
		if(result.isPresent()) {
			Node node = result.get();
			if(node.marked) {
				return Optional.of(node.value);
			} else if(node.parentMarked) {
				Node ancestor = node.markInheritedFrom;
				Stack<Node> ancestors = new Stack<>();
				for(Node prev = node; prev != ancestor; prev = prev.parent) {
					ancestors.push(prev);
				}
				
				List<String> names = new ArrayList<>();
				while(!ancestors.isEmpty()) {
					Node pitru = ancestors.pop();
					names.add(pitru.key);
				}
				
				String name = String.join("/", names);
				return Optional.of(ancestor.value + "/" + name);
			} 
		} 
		
		return Optional.empty();
	}


}


class Node {
	String key;
	String value;
	List<Node> children;
	Node parent;
	
	// Mark related stuff
	boolean marked;
	boolean parentMarked;
	Node markInheritedFrom;
	
	public Node() {
		children = new ArrayList<>();
	}
	
	static Node newNode(String key, String value, Node parent, List<Node> children) {
		
		Node node = new Node();
		node.key = key;
		node.value = value;
		node.parent = parent;
		if(children == null) {
			children = new ArrayList<>();
		} else {
			node.children = children;
		}
		
		if(parent != null) {
			parent.children.add(node);
		}
		return node;
	}
	
	
	static Optional<Node> get(Node root, String searchPath) {
		Node search = root;
		List<String> dirs =  Stream.of(searchPath.split("/")).filter(dir -> !dir.isEmpty()).collect(Collectors.toList());
		boolean found = true;
		for(String dir : dirs) {
			Optional<Node> node = search.children.stream().filter(child -> Objects.equals(child.key, dir)).findAny();
			if(node.isPresent()) {
				search = node.get();
			} else {
				found = false;
				break;
			}				
		}
		
		if(found) {
			return Optional.of(search);
		}
	
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		return key;
	}
}
