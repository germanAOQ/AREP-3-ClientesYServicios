package edu.escuelaing.arep.httpserver;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import edu.escuelaing.arep.nanosparkweb.NanoSparkServer;
import edu.escuelaing.arep.nanosparkweb.Processor;

import java.awt.image.BufferedImage;
import java.io.*;

public class HttpServer {
	private int port;
	private Map<String, Processor> routesToProcessors = new HashMap();

	public HttpServer() {

	}

	public void startServer(int httpPort) throws IOException, FileNotFoundException, InterruptedException {
		this.setPort(httpPort);
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(getPort());
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + this.getPort());
			System.exit(1);
		}
		Socket clientSocket = null;
		while (true) {
			try {
				System.out.println("Listo para recibir en el puerto " + getPort());
				clientSocket = serverSocket.accept();
			} catch (IOException ioe) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			OutputStream outputStream = clientSocket.getOutputStream();
			InputStream inputStream = clientSocket.getInputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			boolean isFirstLine = true;
			String pathI = null;
			String host = null;
			while ((inputLine = in.readLine()) != null) {
				if (isFirstLine) {
					pathI = inputLine.split(" ")[1];
					isFirstLine = false;
				}
				if(inputLine.contains("Host")) {
					host = inputLine.split(" ")[1];
				}
				int i = inputLine.indexOf('/') + 1;
				String urlInputLine = "";

				if (inputLine.contains("index")) {
					while (!urlInputLine.endsWith(".html") && i < inputLine.length()) {
						urlInputLine += (inputLine.charAt(i++));
					}
					String path = "src/main/resources/public/" + urlInputLine;
					try {
						BufferedReader readerFile = new BufferedReader(
								new InputStreamReader(new FileInputStream(path), "UTF8"));
						String output;
						out.println("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
						while (readerFile.ready()) {
							out.println(readerFile.readLine());
						}
					} catch (FileNotFoundException e) {
						// TODO: handle exception
					}
				} else if (inputLine.contains("jpg")) {
					while (!urlInputLine.endsWith(".jpg") && i < inputLine.length()) {
						urlInputLine += (inputLine.charAt(i++));
					}
					String path = "src/main/resources/public/" + urlInputLine;

					BufferedImage bufferedImage = ImageIO.read(new File(path));
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					DataOutputStream dataImage = new DataOutputStream(outputStream);
					ImageIO.write(bufferedImage, "jpg", bytes);
					dataImage.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: image/" + "jpg" + " \r\n" + "\r\n");
					outputStream.write(bytes.toByteArray());
				}

				if (!in.ready()) {
					break;
				}
			}
			String resp = null;
			for(String key: routesToProcessors.keySet()) {
				if(pathI.contains(key) && !pathI.contains("?")) {
					String link = "http://"+host+pathI;
					HttpClient client = HttpClient.newHttpClient();
					HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();
					resp = routesToProcessors.get(key).handle(pathI.substring(key.length()),request, null);
				}else if(pathI.contains(key) && pathI.contains("?")) {
					String link = "http://"+host+pathI;
					HttpClient client = HttpClient.newHttpClient();
					HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();
					resp = routesToProcessors.get(key).handle(pathI.substring(key.length(),pathI.indexOf("?")),request, null);

				}
			}
			if(resp==null) {
				out.println(validOkHttpResponse());
			}else {
				out.println(resp);
			}
			
			
			
			out.close();
			in.close();
			clientSocket.close();
		}
	}

	public static Integer getEnviorenmentPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 36000;
	}

	public String validOkHttpResponse() {
		return "HTTP/1.1 200 OK\r\n"
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
	
	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void registerProcessor(String path, Processor proc) {
		routesToProcessors.put(path, proc);
	}

}