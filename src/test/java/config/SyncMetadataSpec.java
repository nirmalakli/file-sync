package config;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncMetadataSpec {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void syncFileCanBeLoadedFromClasspath() {
		SyncMetadata.load("sync-settings.txt");
	}

	@Test
	public void sourceConfigIsPresent() {
		SyncMetadata metadata = SyncMetadata.load("sync-settings.txt");
		assertTrue(metadata.isPresent("/home/nirmal/Documents"));
	}
	
	@Test
	public void sourceConfigIsAbsent() {
		SyncMetadata metadata = SyncMetadata.load("sync-settings.txt");
		assertFalse(metadata.isPresent("/home/nirmal"));
	}
	
	@Test
	public void getDestinationForADirectSource() {
		SyncMetadata metadata = SyncMetadata.load("sync-settings.txt");
		Optional<String> destination = metadata.getDestination("/home/nirmal/Documents");
		assertTrue("Destination mapping for '/home/nirmal/Documents' should be present", destination.isPresent());
		assertEquals("/media/hdd-ntfs/nirmal/pc/Documents", destination.get());
	}
	
	@Test
	public void getDestinationForAnIndirectSource() {
		SyncMetadata metadata = SyncMetadata.load("sync-settings.txt");
		Optional<String> destination = metadata.getDestination("/home/nirmal/Documents/Official");
		assertTrue("Destination mapping for '/home/nirmal/Documents/Official' should be present", destination.isPresent());
		assertEquals("/media/hdd-ntfs/nirmal", destination.get());
	}
}
