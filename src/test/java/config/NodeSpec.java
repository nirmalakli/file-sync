package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class NodeSpec {
	
	private Node root;
	private Node home;
	private Node nirmal;

	@Before
	public void setup() {
		root = Node.newNode("/", "Root", null, null);
		home = Node.newNode("home", "Home", root, null);
		nirmal = Node.newNode("nirmal", "Nirmal", home, null);
		nirmal.marked=true;
	}

	void add(Node parent, Node child) {
		List<Node> children = parent.children;
		children.add(child);
		child.parent = parent;
	}
	
	@Test
	public void searchForRootNode() {
		Optional<Node> result = Node.search(root, "/");
		assertTrue(result.isPresent());
		assertEquals("/", result.get().key);
		assertEquals("Root", result.get().value);
	}

	@Test
	public void searchForTerminalNode() {
		Optional<Node> result = Node.search(root, "/home/nirmal");
		assertTrue(result.isPresent());
		assertEquals(nirmal.key, result.get().key);
		assertEquals(nirmal.value, result.get().value);
	}
	
	@Test
	public void searchForIntermediateNode() {
		Optional<Node> result = Node.search(root, "/home");
		assertTrue(result.isPresent());
		assertEquals(home.key, result.get().key);
		assertEquals(home.value, result.get().value);
	}	
	
	@Test
	public void searchFromIntermediateNode() {
		Optional<Node> result = Node.search(home, "nirmal");
		assertTrue(result.isPresent());
		assertEquals(nirmal.key, result.get().key);
		assertEquals(nirmal.value, result.get().value);
	}
	
	@Test
	public void path() {
		Node nirmal = Node.search(home, "nirmal").get();
		assertEquals("/home/nirmal", nirmal.path());
	}	
	
	@Test
	public void closestMarkedNodeForATerminalNode() {
		Optional<Node> result = Node.closestMarkedNode(root, "/home/nirmal");
		assertTrue(result.isPresent());
		Node nirmal = result.get();
		assertEquals("/home/nirmal", nirmal.path());
	}
	
	@Test
	public void closestMarkedNodeForANodeOutsideTree() {
		Optional<Node> result = Node.closestMarkedNode(root, "/home/nirmal/file1.jpg");
		assertTrue(result.isPresent());
		Node file1 = result.get();
		assertEquals("/home/nirmal", file1.path());
	}	
	
	@Test
	public void noClosestMarkedNode() {
		Optional<Node> result = Node.closestMarkedNode(root, "/home");
		assertFalse(result.isPresent());
	}
}
