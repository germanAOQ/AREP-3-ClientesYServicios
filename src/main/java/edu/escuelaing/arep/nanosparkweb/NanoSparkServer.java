package edu.escuelaing.arep.nanosparkweb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import edu.escuelaing.arep.httpserver.HttpServer;

public class NanoSparkServer implements Processor{

	private Map<String, BiFunction<HttpRequest, HttpResponse, String>> funciones = new HashMap<>();
	private HttpServer httpServer;
	private int httpPort = 36000;
	
	private static NanoSparkServer _instance = new NanoSparkServer();
	
	private NanoSparkServer() {
		httpServer = new HttpServer();
		httpServer.registerProcessor("/Apps",this);
	}

	public static NanoSparkServer getInstance() {
		return _instance;
	}
	
	public void mapRouteBody(String ruta, BiFunction<HttpRequest, HttpResponse, String> bifunc) {
		funciones.put(ruta, bifunc);
	}
	
	public void startServer() throws FileNotFoundException, IOException, InterruptedException {
		//httpServer = new HttpServer();
		httpServer.startServer(httpPort);
		
	}

	public void port(int servetPort) {
		this.httpPort = servetPort;
		
	}

	@Override
	public String handle(String path, HttpRequest req, HttpResponse resp) {
		String value;
		if(funciones.containsKey(path)) {
			value = validOkHttpHeader() + funciones.get(path).apply(req, resp);
		}else {
			value = validErrorHttpHeader() + " Error";
		}
		return value;
	}
	


	private String validErrorHttpHeader() {
		// TODO Auto-generated method stub
		return "HTTP/1.1 404 Not Found OK\r\n"
        + "Content-Type: text/html\r\n"
        + "\r\n"
        + "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta charset=\"UTF-8\">\n"
        + "<title>Title of the document</title>\n"
        + "</head>\n"
        + "<body>\n"
        + "<h1>Mi propio mensaje</h1>\n"
        + "</body>\n"
        + "</html>\n";
	}

	private String validOkHttpHeader() {
		return "HTTP/1.1 200 OK\r\n"
				+ "Content-Type: text/html\r\n"
				+ "\r\n";
	}

}
