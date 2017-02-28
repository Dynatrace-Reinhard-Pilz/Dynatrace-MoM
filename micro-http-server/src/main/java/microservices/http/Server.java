package microservices.http;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import microservices.http.rest.Hello;
import microservices.http.rest.Shutdown;

public final class Server implements ThreadFactory {
	
	private static final int DEFAULT_PORT = 8080;
	private static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";
	
	private static Server SERVER = null;
	
	@SuppressWarnings("restriction")
	private final com.sun.net.httpserver.HttpServer httpServer;
	private final ExecutorService threadPool = Executors.newCachedThreadPool(this);
	
	@SuppressWarnings("restriction")
	private Server(String address, int port) {
		URI baseUri = UriBuilder.fromUri("http://" + address + "/").port(port).build();
		ResourceConfig config = new ResourceConfig(Hello.class, Shutdown.class);
		httpServer = JdkHttpServerFactory.createHttpServer(baseUri, config, false);
		httpServer.setExecutor(threadPool);
		httpServer.start();
	}
	
	@SuppressWarnings("restriction")
	public void shutdown() {
		httpServer.stop(0);
		threadPool.shutdownNow();
	}
	
	public static Server instance() {
		return SERVER;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SERVER = new Server(DEFAULT_BIND_ADDRESS, DEFAULT_PORT);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		return thread;
	}
}