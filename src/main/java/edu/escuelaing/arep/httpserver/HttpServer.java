package edu.escuelaing.arep.httpserver;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;

public class HttpServer {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(getPort());
		} catch (IOException e) {
			System.err.println("Could not listen on port: 35000.");
			System.exit(1);
		}
		while (true) {
			Socket clientSocket = null;
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
			while ((inputLine = in.readLine()) != null) {
				int i = inputLine.indexOf('/') + 1;
				String urlInputLine = "";
				System.out.println("Received: " + inputLine);
				if (inputLine.contains("index")) {
					System.out.println("Impresion de prueba: "+inputLine);
					while (!urlInputLine.endsWith(".html") && i < inputLine.length()) {
						urlInputLine += (inputLine.charAt(i++));
					}
					String path = "src/main/resources/public/"+urlInputLine;
					try {
						BufferedReader readerFile = new BufferedReader(
								new InputStreamReader(new FileInputStream(path), "UTF8"));
						out.println("HTTP/2.0 200 OK");
						out.println("Content-Type: text/html");
						out.println("\r\n");
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
					String path = "src/main/resources/public/"+urlInputLine;
					BufferedImage bImage = ImageIO.read(new File(path));
					out.println("HTTP/2.0 200 OK");
					out.write("Content-Type: image/webp,*/*");
					out.println("\r\n");
					ImageIO.write(bImage, "jpg", outputStream);
				}
				if (!in.ready()) {
                    break;
                }
			}
			out.close();
			in.close();
			clientSocket.close();
		}
	}

	private static int getPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 36000;
	}
}