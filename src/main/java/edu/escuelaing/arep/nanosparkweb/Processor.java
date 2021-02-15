package edu.escuelaing.arep.nanosparkweb;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author German
 *
 */
public interface Processor {
	/**
	 * @param path ruta a ser evaluada.
	 * @param req petición http asociada a la ruta. 
	 * @param res respuesta http asociada a la ruta.
	 * @return retorna un http header correspondiente a la ruta y a la función lambda definida.
	 */
	public String handle(String path, HttpRequest req, HttpResponse res);

}
