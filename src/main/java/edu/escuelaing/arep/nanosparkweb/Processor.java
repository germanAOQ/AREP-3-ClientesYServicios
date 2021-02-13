package edu.escuelaing.arep.nanosparkweb;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface Processor {
	public String handle(String path, HttpRequest req, HttpResponse res);

}
