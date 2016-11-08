package config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
	public String path() {
		List<String> traceAncestors = new ArrayList<>();
		for(Node prev = this; prev != null; prev = prev.parent) {
			traceAncestors.add(prev.key);
		}
		
		Collections.reverse(traceAncestors);
		return String.join("/", traceAncestors).replaceFirst("/", "");
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
	
	
	static Optional<Node> search(Node root, String searchPath) {
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
	
	static Optional<Node> closestMarkedNode(Node root, String searchPath) {
		Node search = root;
		List<String> dirs =  Stream.of(searchPath.split("/")).filter(dir -> !dir.isEmpty()).collect(Collectors.toList());
		Node lastMarkedNode = search.marked? search : null;
		
		for(String dir : dirs) {
			
			Optional<Node> node = search.children.stream().filter(child -> Objects.equals(child.key, dir)).findAny();
			if(node.isPresent()) {
				search = node.get();
				if(search.marked) {
					lastMarkedNode = search;
				}
			} else {
				break;
			}				
		}
		
		if(lastMarkedNode != null) {
			return Optional.of(search);
		}
	
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		return key;
	}
}