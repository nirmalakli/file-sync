package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

public class SyncMapperSpec {


	@Test
	public void straightLine() {
		SyncMapper syncMapper = new SyncMapper();
		syncMapper.add(new SyncConfig("/home/nirmal/Videos", "/media/pc/nirmal/Videos"));
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "home");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home/nirmal");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "nirmal");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home/nirmal/Videos");
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
		syncMapper.add(new SyncConfig("/home/nirmal/Videos/Movies/English", "/media/pc/shared/Movies/English"));
		syncMapper.add(new SyncConfig("/home/nirmal/Videos", "/media/pc/nirmal/Videos"));
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "home");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home/nirmal");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "nirmal");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home/nirmal/Videos");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "Videos");
			assertEquals(result.get().value, "/media/pc/nirmal/Videos");
			assertTrue(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home/nirmal/Videos/Movies");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "Movies");
			assertNull(result.get().value);
			assertFalse(result.get().marked);
			assertTrue(result.get().parentMarked);
			assertNotNull(result.get().markInheritedFrom);
			assertEquals("Videos", result.get().markInheritedFrom.key);
		}
		
		
		{
			Optional<Node> result = Node.search(syncMapper.tree, "/home/nirmal/Videos/Movies/English");
			assertTrue(result.isPresent());
			assertEquals(result.get().key, "English");
			assertEquals("/media/pc/shared/Movies/English", result.get().value);
			assertTrue(result.get().marked);
			assertFalse(result.get().parentMarked);
			assertNull(result.get().markInheritedFrom);
		}
	}
	
	@Test
	public void destinationSearching() {
		
		SyncMapper syncMapper = new SyncMapper();
		syncMapper.add(new SyncConfig("/home/nirmal/Videos/Movies/English", "/media/pc/shared/Movies/English"));
		syncMapper.add(new SyncConfig("/home/nirmal/Videos", "/media/pc/nirmal/Videos"));
		
		
		{
			Optional<String> result = syncMapper.destination("/home/nirmal");
			assertFalse(result.isPresent());
		}
		
		{
			Optional<String> result = syncMapper.destination("/home/nirmal/Videos");
			assertTrue(result.isPresent());
			String destination = result.get();
			assertEquals("/media/pc/nirmal/Videos", destination);
		}
		
		{
			Optional<String> result = syncMapper.destination("/home/nirmal/Videos/Movies");
			assertTrue(result.isPresent());
			String destination = result.get();
			assertEquals("/media/pc/nirmal/Videos/Movies", destination);
		}
		
		{
			Optional<String> result = syncMapper.destination("/home/nirmal/Videos/Movies/English");
			assertTrue(result.isPresent());
			String destination = result.get();
			assertEquals("/media/pc/shared/Movies/English", destination);
		}
		
		{
			Optional<String> result = syncMapper.destination("/home/nirmal/Videos/Movies/English/Godfather.avi");
			assertTrue(result.isPresent());
			String destination = result.get();
			assertEquals("/media/pc/shared/Movies/English/Godfather.avi", destination);
		}
		
	}

}
