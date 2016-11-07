import static org.junit.Assert.assertEquals;
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
	}

	void add(Node parent, Node child) {
		List<Node> children = parent.children;
		children.add(child);
		child.parent = parent;
	}
	
	@Test
	public void getWorksForRootNode() {
		Optional<Node> result = Node.get(root, "/");
		assertTrue(result.isPresent());
		assertEquals("/", result.get().key);
		assertEquals("Root", result.get().value);
	}

	@Test
	public void getWorksForTerminalNode() {
		Optional<Node> result = Node.get(root, "/home/nirmal");
		assertTrue(result.isPresent());
		assertEquals(nirmal.key, result.get().key);
		assertEquals(nirmal.value, result.get().value);
	}
	
	@Test
	public void getWorksForIntermediateNode() {
		Optional<Node> result = Node.get(root, "/home");
		assertTrue(result.isPresent());
		assertEquals(home.key, result.get().key);
		assertEquals(home.value, result.get().value);
	}	
	
	@Test
	public void getWorksFromIntermediateNode() {
		Optional<Node> result = Node.get(home, "nirmal");
		assertTrue(result.isPresent());
		assertEquals(nirmal.key, result.get().key);
		assertEquals(nirmal.value, result.get().value);
	}


}
