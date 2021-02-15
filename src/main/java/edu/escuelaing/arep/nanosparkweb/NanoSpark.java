package edu.escuelaing.arep.nanosparkweb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.BiFunction;

import edu.escuelaing.arep.nanosparkweb.NanoSparkServer;

public class NanoSpark {

	/**
	 * @param ruta ruta a ser mapeada.
	 * @param bifunc función lambda a ser registrada.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void get(String ruta, BiFunction<HttpRequest, HttpResponse, String> bifunc) throws FileNotFoundException, IOException {
		NanoSparkServer nServer = NanoSparkServer.getInstance();
		nServer.mapRouteBody(ruta, bifunc);
	}

	/**
	 * @param port puerto con el que iniciara el servidor.
	 */
	public static void port(int port) {
		NanoSparkServer nServer = NanoSparkServer.getInstance();
		nServer.port(port);
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void start() throws FileNotFoundException, IOException, InterruptedException {
		NanoSparkServer nServer = NanoSparkServer.getInstance();
		nServer.startServer();
	}
}
