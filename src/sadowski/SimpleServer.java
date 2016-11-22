package sadowski;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class SimpleServer {
	String host;
	int port;
	
	boolean notFoundFlag = false;
	
	public SimpleServer() {}
	
	public SimpleServer(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	public ArrayList<String> getClientRequest(Socket clientSocket, int requestLength) throws IOException {
		ArrayList<String> toReturn = new ArrayList<String>();
		
		InputStream is = clientSocket.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		String line;

		for (int i = 0; i < requestLength; ++i) {
			line = rd.readLine();
			if (line == null) {
				break;
			}
			toReturn.add(line);
		}

		clientSocket.shutdownInput();
		
		System.out.println("Input shut down");
		
		return toReturn;
	}
	
	public void sendTestResponse(OutputStream os) throws UnsupportedEncodingException, IOException {
		sendResponse(os, "<!doctype html><html lang=en><head><meta charset=utf-8><title>blah</title></head><body><p>I'm the content</p></body></html>");
	}
	
	public void sendResponse(OutputStream os, String message) throws UnsupportedEncodingException, IOException {
		String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + message;
		os.write(httpResponse.getBytes("UTF-8"));
	}
	
	public void sendResponse(OutputStream os, ArrayList<String> lines) throws UnsupportedEncodingException, IOException {
		String message = Util.mergeArrayListOfStrings(lines);
		String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + message;
		os.write(httpResponse.getBytes("UTF-8"));
	}
	
	public void sendResponse404(OutputStream os) throws UnsupportedEncodingException, IOException {
		String defaultMessage = "<!doctype html><html lang=en><head><meta charset=utf-8><title>Not Found</title></head><body><h1>Resource not found!</h1></body></html>";
		String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n" + defaultMessage;
		os.write(httpResponse.getBytes("UTF-8"));
	}
	
	public ArrayList<String> getClientRequest(Socket clientSocket) throws IOException {
		return getClientRequest(clientSocket, 8);
	}
	
	public String getClientRequestResource(ArrayList<String> clientRequest) {
		String firstLine = clientRequest.get(0);
		String[] divided = firstLine.split("\\s+");
		
		return Util.removeNFirstCharacters(divided[1],1);
	}
	
	ArrayList<String> getTextResource(String path) throws IOException {
		if(path.equals("")) {
			path = "index.html";
		}
		ArrayList<String> lines = new ArrayList<String>();
		if(FileReader.fileExists(path)) {
			lines = FileReader.readLines(path);
			setNotFoundFlag(false);
		} else {
			setNotFoundFlag(true);
		}
		return lines;
	}
	
	BufferedImage getImgResource(String path) throws IOException {
		return ImageIO.read(new File(path));
	}
	
	void sendImgResponse(String path, Socket socket, String type) throws IOException {
		BufferedImage image = getImgResource(path);
		ImageIO.write(image, type, socket.getOutputStream());
	}
	
	public void run() throws IOException {
		ServerSocket server = new ServerSocket(port);
		System.out.println("Listening for connection on port " + port + " ....");

		try (Socket clientSocket = server.accept()) {
			ArrayList<String> clientRequest = getClientRequest(clientSocket);
			Util.printArrayList(clientRequest);
			
			String path = getClientRequestResource(clientRequest);
			ArrayList<String> resource = getTextResource(path);
			if(!getNotFoundFlag()) {
				String extension = Util.getResourceExtension(path);
				if( Util.checkIfExtensionRefersToImg(extension) ) {
					sendImgResponse(path, clientSocket, extension);
				} else {
					sendResponse(clientSocket.getOutputStream(), resource);
				}
			} else {
				sendResponse404(clientSocket.getOutputStream());
			}
		}
		server.close();
	}
	
	public void setNotFoundFlag(boolean toSet) {
		notFoundFlag = toSet;
	}

	public boolean getNotFoundFlag() {
		return notFoundFlag;
	}

	public static void main(String args[]) throws IOException {
		int port = Util.getUserInteger("Type in on which port server should listen: ");
		String host = "localhost";
		
		SimpleServer serv = new SimpleServer(host, port);
		while(true) {
			serv.run();
		}
	}
}
