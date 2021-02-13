package edu.escuelaing.arep.httpserver;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;

public class HttpServer {
	public static void main(String[] args) throws IOException, FileNotFoundException {
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
				
				if (inputLine.contains("index")) {
					while (!urlInputLine.endsWith(".html") && i < inputLine.length()) {
						urlInputLine += (inputLine.charAt(i++));
					}
					String path = "src/main/resources/public/"+urlInputLine;
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
					String path = "src/main/resources/public/"+urlInputLine;
					
					BufferedImage bufferedImage = ImageIO.read(new File(path));
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					DataOutputStream dataImage = new DataOutputStream(outputStream);
					ImageIO.write(bufferedImage,"jpg",bytes);
					dataImage.writeBytes("HTTP/1.1 200 OK \r\n"
		                    + "Content-Type: image/" + "jpg" + " \r\n"
		                    + "\r\n");
					outputStream.write(bytes.toByteArray());
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