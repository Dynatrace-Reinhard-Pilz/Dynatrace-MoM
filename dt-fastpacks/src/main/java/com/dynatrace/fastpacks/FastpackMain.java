package com.dynatrace.fastpacks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import com.dynatrace.cmd.CommandLineException;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.FileSource;
import com.dynatrace.utils.Iterables;

public class FastpackMain {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(FastpackMain.class.getName());

	public static void main(String[] args) {
		FastpackMain main = new FastpackMain();
		try {
			main.execute(args);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	private void execute(String[] args) throws IOException, CommandLineException {
		Config config = new Config(args);
		
		
		FastpackBuilder builder = new FastpackBuilder(
			config.getFastPackName(),
			config.getFastPackName()
		);
		
		builder.setAuthor(config.getAuthor());
		boolean isEmpty = true;
		Collection<String> profiles = config.getProfiles();
		if (!Iterables.isNullOrEmpty(profiles)) {
			for (String profile : profiles) {
				isEmpty = false;
				System.out.println("... adding System Profile '" + profile + "'");
				builder.addProfile(new FileSource(profile));
			}
		}
		Collection<String> dashboards = config.getDashboards();
		if (!Iterables.isNullOrEmpty(dashboards)) {
			for (String dashboard : dashboards) {
				isEmpty = false;
				System.out.println("... adding Dashboard '" + dashboard + "'");
				builder.addDashboard(new FileSource(dashboard));
			}
		}
		if (isEmpty) {
			System.err.println("This Fast pack does not contain any resources - please use -profiles or -dashboard options");
			return;
		}
		File fastPackFile = new File(config.getOutput());
		Closeables.delete(fastPackFile);
		FileOutputStream out = new FileOutputStream(fastPackFile);
		builder.build(out);
		out.close();
		System.out.println("... generating 'plugin.xml'");
		System.out.println("... generating 'META-INF/MANIFEST.MF'");
		System.out.println(fastPackFile.getName() + " has been created and uploaded to " + config.getServers());
	}

}
