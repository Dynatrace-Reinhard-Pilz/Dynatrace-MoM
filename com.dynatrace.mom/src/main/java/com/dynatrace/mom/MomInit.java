package com.dynatrace.mom;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.Http;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.mom.runtime.DefaultServerRepository;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.mom.runtime.ServerUpdater;
import com.dynatrace.mom.runtime.XMLServerRepository;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.xml.XMLUtil;

public final class MomInit {
	
	private static final Logger LOGGER = Logger.getLogger(MomInit.class.getName());
	
	private ServerRepository serverRepository =
			new DefaultServerRepository(new DefaultExecutionContext());
	private ServerUpdater serverUpdater = null;

	public ServerRepository getServerRepository() {
		return serverRepository;
	}
	
	public final MomInit init(InputStream in, ExecutionContext context) throws IOException {
		Objects.requireNonNull(in);
		XMLServerRepository xmlRepo = null;
		try {
			xmlRepo = XMLUtil.deserialize(in, XMLServerRepository.class);
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Unable to resolve servers.xml", t);
		}
		if (xmlRepo != null) {
			String href = xmlRepo.getHref();
			if (xmlRepo.isFollow() && href != null) {
				URL url = null;
				try {
					url = new URL(href);
				} catch (MalformedURLException e) {
					LOGGER.log(Level.WARNING, "The URL '" + href + "' referred withn the embedded servers.xml is not valid - skipping loading the remote server list ", e);
				}
				if (url != null) {
					HttpResponse<XMLServerRepository> response = null;
					try {
						LOGGER.log(Level.INFO, "Resolving remote server list from " + url.toString());
						response = Http.client().request(url, Method.GET, null, XMLServerRepository.class);
					} catch (Throwable t) {
						LOGGER.log(Level.WARNING, "Remote Server List located at '" + href + "' could not get resolved", t);
					}
					XMLServerRepository remoteXmlRepo = response.getData();
					if (remoteXmlRepo == null) {
						LOGGER.log(Level.WARNING, "Remote Server List located at '" + href + "' could not get resolved");
					} else {
						remoteXmlRepo.setHref(null);
						xmlRepo = remoteXmlRepo;
					}
				}
			}
		}
		serverRepository = new DefaultServerRepository(xmlRepo, context);
		serverRepository.decrypt();
		init(context);
		return this;
	}
	
	public final MomInit init(ExecutionContext ctx) {
//		serverRepository.add(createServerRecord("52.5.210.190", 8021));
//		serverRepository.add(createServerRecord("52.7.189.84", 8021));
//		 serverRepository.setContext(ctx);
//		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
//		String dbName="mom";
//		String connectionURL = "jdbc:derby:" + dbName + ";create=true";
//		String createString = "CREATE TABLE WISH_LIST (WISH_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, WISH_ITEM VARCHAR(32) NOT NULL)";
//		boolean wishListExists = false;
//		try {
//			
//			
//			Class.forName(driver);
//			connection = DriverManager.getConnection(connectionURL);
//			Set<String> dbTables = getDBTables(connection);
//			for (String dbTable : dbTables) {
//				if (dbTable == null) {
//					continue;
//				}
//				if (dbTable.toUpperCase().equals("WISH_LIST")) {
//					wishListExists = true;
//					LOGGER.log(Level.INFO, "Database Table WISH_LIST exists");
//				}
//			}
//			if (!wishListExists) {
//				executeStatement(connection, createString);
//			}
//			
//			
//		}  catch (Throwable e)  {
//			e.printStackTrace(System.err);
//		}		
		serverUpdater = new ServerUpdater(serverRepository);
		return this;
	}
	
//	private static void executeStatement(final Connection connection, final String sql) {
//		Statement s = null;
//		try {
//			s = connection.createStatement();
//			s.execute(sql);
//		}  catch (SQLException sqle) {
//			sqle.printStackTrace(System.err);
//		} finally {
//			if (s != null) {
//				try {
//					s.close();
//				} catch (SQLException e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//	}
//	
//	private static Set<String> getDBTables(Connection targetDBConn) throws SQLException {
//		Set<String> set = new HashSet<String>();
//		DatabaseMetaData dbmeta = targetDBConn.getMetaData();
//		readDBTable(set, dbmeta, "TABLE", null);
//		readDBTable(set, dbmeta, "VIEW", null);
//		return set;
//	}
//	
//	private static void readDBTable(Set<String> set, DatabaseMetaData dbmeta, String searchCriteria, String schema) throws SQLException {
//		ResultSet rs = dbmeta.getTables(null, schema, null, new String[] { searchCriteria });
//		while (rs.next()) {
//			set.add(rs.getString("TABLE_NAME").toLowerCase());
//		}
//	}
	
	public final void shutdown() {
//		httpServer.stop();
		LOGGER.log(Level.INFO, "Shutting down " + MomInit.class.getSimpleName());
		if (serverUpdater != null) {
			serverUpdater.shutdown();
		}
//		try {
//			DriverManager.getConnection("jdbc:derby:;shutdown=true");
//		} catch (final SQLException se)  {
//			if (!"XJ015".equals(se.getSQLState())) {
//				se.printStackTrace(System.err);
//			}
//		}		
	}

}
