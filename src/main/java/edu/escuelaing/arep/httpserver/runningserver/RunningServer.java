package edu.escuelaing.arep.httpserver.runningserver;

import java.io.FileNotFoundException;
import java.io.IOException;

import edu.escuelaing.arep.httpserver.HttpServer;

public class RunningServer {

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		HttpServer httpServerFirstChallenge = new HttpServer();
		httpServerFirstChallenge.startServer(HttpServer.getEnviorenmentPort());

	}

}
