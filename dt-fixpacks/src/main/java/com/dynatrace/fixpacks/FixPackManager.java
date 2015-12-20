package com.dynatrace.fixpacks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.Http;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Version;
import com.dynatrace.xml.XMLUtil;

public final class FixPackManager {
	
	@SuppressWarnings("unused")
	private final ExecutionContext ctx;
	
	private static final Logger LOGGER =
			Logger.getLogger(FixPackManager.class.getName());

	private final File fixPackFolder;
	
	private final Collection<FixPack> fixPacksX = new ArrayList<FixPack>();
	
	public File getFixPackFolder() {
		return fixPackFolder;
	}
	
	private void loadFixPackList(URL url) {
		HttpResponse<XmlFixPackList> response = null;
		try {
			response = Http.client().request(url, Method.GET, null, XmlFixPackList.class);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Loadiong remote FixPack list from " + url + " failed", e);
			return;
		}
		if (response == null) {
			LOGGER.log(Level.WARNING, "Load remote FixPack list from " + url + " did not provide any response");
			return;
		}
		XmlFixPackList xmlFixPackList = response.getData();
		if (xmlFixPackList == null) {
			LOGGER.log(Level.WARNING, "Load remote FixPack list from " + url + " did not provide an a valid list");
			return;
		}
		Collection<XmlFixPack> fixPacks = xmlFixPackList.getFixPacks();
		if (Iterables.isNullOrEmpty(fixPacks)) {
			return;
		}
		for (XmlFixPack xmlFixPack : fixPacks) {
			if (xmlFixPack == null) {
				continue;
			}
			String href = xmlFixPack.getHref();
			if (href == null) {
				continue;
			}
			URL fixPackUrl = null;
			try {
				fixPackUrl = new URL(href);
			} catch (MalformedURLException e) {
				LOGGER.log(Level.WARNING, "Fix Pack URL " + href + " is malformed - skipping download");
			}
			if (fixPackUrl != null) {
				FixPackDownloader downloader = new FixPackDownloader(fixPackUrl);
				downloader.start();
			}
		}
	}
	
	private class FixPackDownloader extends Thread {
		private final URL url;
		
		public FixPackDownloader(URL url) {
			super(url.getPath());
			Objects.requireNonNull(url);
			this.url = url;
			setDaemon(true);
		}
		
		@Override
		public void run() {
			File fixPackFile = retrieveFixPackFile(url);
			if (fixPackFile != null) {
				addFixPackFile(fixPackFile);
			}
		}
	}
	
	private File retrieveFixPackFile(URL url) {
		String path = url.getPath();
		int idx = path.lastIndexOf('/');
		if (idx < 0) {
			LOGGER.log(Level.INFO, "The remote FixPack URL " + url + " points to an invalid file name " + path + " - skipping that file.");
			return null;
		}
		path = path.substring(idx + 1);
		File fixPackFile = new File(fixPackFolder, path);
		if (fixPackFile.exists()) {
			LOGGER.log(Level.INFO, "The remote FixPack " + path + " already exists - skipping that file.");
			return null;
		}
		LOGGER.log(Level.INFO, "Downloading FixPack from " + url);
		try (FileOutputStream fos = new FileOutputStream(fixPackFile)) {
			Http.client().request(url, Method.GET, null, fos);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to download FixPack from " + url);
			if (fixPackFile.exists()) {
				fixPackFile.delete();
			}
			return null;
		}
		return fixPackFile;
	}
	
	private void addFixPackFile(File fixPackFile) {
		if (!FixPack.isFixPackFile(fixPackFile)) {
			return;
		}
		final Version fixPackVersion = FixPack.getFixPackVersion(fixPackFile);
		final FixPackInfo fpi = FixPack.extractFixPackInfo(fixPackFile);
		if ((fixPackVersion != null) && (fpi != null)) {
			synchronized (fixPacksX) {
				fixPacksX.add(new FixPack(fixPackFile, fpi, fixPackVersion));
			}
		} else {
			LOGGER.log(Level.WARNING, "FixPack File " + fixPackFile.getName() + " seems to be invalid - getting rid of this file");
			fixPackFile.delete();
		}
	}
	
	public FixPackManager(ExecutionContext ctx) {
		this.ctx = ctx;
		fixPackFolder = ctx.getStorageSubFolder(
			ExecutionContext.ATTRIBUTE_FIXPACKS_FOLDER,
			"fixpacks",
			false
		);
		final File[] fixPackFiles = this.fixPackFolder.listFiles();
		for (File fixPackFile : fixPackFiles) {
			addFixPackFile(fixPackFile);
		}
		InputStream in = null;
		try {
			in = Closeables.getResourceAsStream("/fixpacks.xml");
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Unable to load local fixpacks.xml - relying on locally available fixpacks", t);
			return;
		}
		if (in == null) {
			LOGGER.log(Level.WARNING, "Unable to load local fixpacks.xml - relying on locally available fixpacks");
			return;
		}
		XmlFixPackList xmlFixPackList = null;
		try {
			xmlFixPackList = XMLUtil.deserialize(in, XmlFixPackList.class);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to load local fixpacks.xml - relying on locally available fixpacks", e);
		}
		if (xmlFixPackList != null) {
			String href = xmlFixPackList.getHref();
			if (href == null) {
				return;
			}
			URL xmlFixPackUrl = null;
			try {
				xmlFixPackUrl = new URL(href);
			} catch (MalformedURLException e) {
				LOGGER.log(Level.INFO, "The remote FixPack URL " + href + " is a malformed URL - relying on locally available fixpacks");
				return;
			}
			loadFixPackList(xmlFixPackUrl);
		}
	}
	
	public final FixPack getFixPack(final Version version) {
		Objects.requireNonNull(version);
		for (FixPack fixPack : getFixPacks()) {
			if (fixPack.getVersion().equals(version)) {
				return fixPack;
			}
		}
		return null;
	}
	
	public final Collection<FixPack> getFixPacks() {
		synchronized (fixPacksX) {
			return new ArrayList<FixPack>(fixPacksX);
		}
	}
	
	public final Collection<FixPack> getInstalledFixPacks(final Version version) {
		final ArrayList<FixPack> result = new ArrayList<FixPack>();
		synchronized (fixPacksX) {
			for (FixPack fixPack : fixPacksX) {
				final Version fixPackVersion = fixPack.getVersion();
				if (version.includes(fixPackVersion)) {
					if (version.getMinor() == fixPackVersion.getMinor()) {
						result.add(fixPack);
					}
				}
			}
		}
		return result;
	}
	
	public final Collection<FixPack> getNonInstalledFixPacks(final Version version) {
		final ArrayList<FixPack> result = new ArrayList<FixPack>();
		synchronized (fixPacksX) {
			for (FixPack fixPack : fixPacksX) {
				final Version fixPackVersion = fixPack.getVersion();
				if (!version.includes(fixPackVersion) && version.matchesMinor(fixPackVersion)) {
					result.add(fixPack);
				}
			}
		}
		return result;
	}
	
}
