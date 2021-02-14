package edu.escuelaing.arep.nanosparkweb.controller;

import static edu.escuelaing.arep.nanosparkweb.NanoSpark.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import edu.escuelaing.arep.httpserver.HttpServer;
import edu.escuelaing.arep.persistence.Persistence;

public class NanoSparkWebController {
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		port(HttpServer.getEnviorenmentPort());
		get("/helloworld", (req, resp) -> {
			String query = req.uri().getQuery();
			String mensaje = query.substring(query.indexOf("=")+1);
			Persistence per = new Persistence();
			return per.getStatement(per.connection(), mensaje);
		});
		start();
	}
	
	
}
