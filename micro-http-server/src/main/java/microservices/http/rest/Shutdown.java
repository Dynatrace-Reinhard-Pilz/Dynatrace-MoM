package microservices.http.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import microservices.http.Server;

@Path("/shutdown")
public class Shutdown {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String shutdown() {
		new Thread() {
			@Override
			public void run() {
				System.out.println("Waiting to shut down HTTP Server");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
				System.out.println("Shutting down HTTP Server");
				Server.instance().shutdown();
				System.out.println("HTTP Server has been shut down");
			}
		}.start();
		return "shutdown";
	}
	
}
