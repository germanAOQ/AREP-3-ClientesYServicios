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
		ServerSocket serverSocket = this.getServerSocket();
		Socket clientSocket = null;
		while (true) {
			clientSocket = this.getClientSocket(serverSocket);
			OutputStream outputStream = clientSocket.getOutputStream();
			InputStream inputStream = clientSocket.getInputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String pathI = this.getPath(in);
			String host = this.getHost(in);
			this.setUrlInputLine(in, outputStream, out, pathI);
			for (String key : routesToProcessors.keySet()) {
				this.handlePath(out, key, host, pathI, pathI.contains("?"));
			}
			out.close();
			in.close();
			clientSocket.close();
		}
	}

	private void handlePath(PrintWriter out, String key, String host, String pathI, boolean queryParams) {
		String link = "http://" + host + pathI;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();
		String resp = (queryParams)?(routesToProcessors.get(key).handle(pathI.substring(key.length(), pathI.indexOf("?")), request, null)):routesToProcessors.get(key).handle(pathI.substring(key.length()), request, null); 
		if (resp == null) {
			out.println(validOkHttpResponse());
		} else if (resp != null) {
			out.println(resp);
		}
	}

	private void setUrlInputLine(BufferedReader in, OutputStream outputStream, PrintWriter out, String pathI) throws IOException {
		String urlInputLine = "";
		String inputLine = "";
		boolean isFirstResquest = true;
		while ((inputLine = in.readLine()) != null) {
			int i = inputLine.indexOf('/') + 1;
			if ((inputLine.contains("index") || pathI.equals("/") && isFirstResquest)) {
				isFirstResquest = false;
				while (!urlInputLine.endsWith(".html") && i < inputLine.length()) {
					urlInputLine += (inputLine.charAt(i++));
				}
				String path = "src/main/resources/public/" + "index.html";
				BufferedReader readerFile = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
				out.println("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
				while (readerFile.ready()) {
					out.println(readerFile.readLine());
				}
				readerFile.close();
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

	}

	private String getHost(BufferedReader in) {
		String host = null;
		String inputLine;
		try {
			while ((inputLine = in.readLine()) != null && inputLine.contains("Host")) {
				host = inputLine.split(" ")[1];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return host;
	}

	private String getPath(BufferedReader in) {
		String path = null;
		String inputLine;
		int line = 0;
		try {
			while ((inputLine = in.readLine()) != null && line == 0) {
				path = inputLine.split(" ")[1];
				line++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	private Socket getClientSocket(ServerSocket serverSocket) {
		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}
		return clientSocket;
	}

	private ServerSocket getServerSocket() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + this.getPort());
			System.exit(1);
		}
		return serverSocket;
	}

	public static Integer getEnviorenmentPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 36000;
	}

	public String validOkHttpResponse() {
		return "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>\n" + "<html>\n"
				+ "<head>\n" + "<meta charset=\"UTF-8\">\n" + "<title>Title of the document</title>\n" + "</head>\n"
				+ "<body>\n" + "<h1>Mi propio mensaje</h1>\n" + "</body>\n" + "</html>\n";
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