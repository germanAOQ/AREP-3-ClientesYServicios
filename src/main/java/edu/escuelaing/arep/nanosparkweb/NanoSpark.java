package edu.escuelaing.arep.nanosparkweb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.BiFunction;

import edu.escuelaing.arep.nanosparkweb.NanoSparkServer;

public class NanoSpark {

	public static void get(String ruta, BiFunction<HttpRequest, HttpResponse, String> bifunc) throws FileNotFoundException, IOException {
		NanoSparkServer nServer = NanoSparkServer.getInstance();
		nServer.mapRouteBody(ruta, bifunc);
	}

	public static void port(int port) {
		NanoSparkServer nServer = NanoSparkServer.getInstance();
		nServer.port(port);
	}
	
	public static void start() throws FileNotFoundException, IOException {
		NanoSparkServer nServer = NanoSparkServer.getInstance();
		nServer.startServer();
	}
}
