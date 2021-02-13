package edu.escuelaing.arep.nanosparkweb.controller;

import static edu.escuelaing.arep.nanosparkweb.NanoSpark.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import edu.escuelaing.arep.httpserver.HttpServer;

public class NanoSparkWebController {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		port(HttpServer.getEnviorenmentPort());
		get("/helloworld", (req, resp) -> "Hello World! from lambda");
		start();
	}
	
	
}
