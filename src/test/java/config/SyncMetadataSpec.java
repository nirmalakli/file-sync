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
}
