import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncMapperSpec {


	@Test
	public void straightLine() {
		SyncMapper syncMapper = new SyncMapper();
		syncMapper.add(new SyncConfig("/home/nirmal/Videos", "/media/pc/nirmal/Videos"));
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "home");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home/nirmal");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "nirmal");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home/nirmal/Videos");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "Videos");
			assertEquals(result.get().value, "/media/pc/nirmal/Videos");
			assertTrue(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
	}
	
	@Test
	public void twoConfigsOnAStraightLine() {
		SyncMapper syncMapper = new SyncMapper();
		syncMapper.add(new SyncConfig("/home/nirmal/Videos", "/media/pc/nirmal/Videos"));
		syncMapper.add(new SyncConfig("/home/nirmal/Videos/Movies/English", "/media/pc/shared/Movies/English"));
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "home");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home/nirmal");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "nirmal");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home/nirmal/Videos");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "Videos");
			assertEquals(result.get().value, "/media/pc/nirmal/Videos");
			assertTrue(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home/nirmal/Videos/Movies");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "Movies");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertTrue(result.get().parentMarked);
			assertNotNull(result.get().markInheritedFrom);
			assertEquals("Videos", result.get().markInheritedFrom.key);
		}
		
		
		{
			Optional<Node> result = Node.get(syncMapper.tree, "/home/nirmal/Videos/Movies/English");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "English");
			assertEquals("/media/pc/shared/Movies/English", result.get().value);
			assertTrue(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
	}
	
	@Test
	public void get() {
		
		SyncMapper syncMapper = new SyncMapper();
		syncMapper.add(new SyncConfig("/home/nirmal/Videos", "/media/pc/nirmal/Videos"));
		syncMapper.add(new SyncConfig("/home/nirmal/Videos/Movies/English", "/media/pc/shared/Movies/English"));
		
		{
			Optional<String> result = syncMapper.get("/home/nirmal/Videos");
			assertTrue(result.isPresent());
			String destination = result.get();
			assertEquals("/media/pc/nirmal/Videos", destination);
		}
		
		{
			Optional<String> result = syncMapper.get("/home/nirmal/Videos/Movies");
			assertTrue(result.isPresent());
			String destination = result.get();
			assertEquals("/media/pc/nirmal/Videos/Movies", destination);
		}
		
		
		
	}

}
