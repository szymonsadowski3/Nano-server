package sadowski;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MultiThreadServer implements Runnable {
	Socket csocket;

	MultiThreadServer(Socket csocket) {
		this.csocket = csocket;
	}

	void sendResponse(String message, String extension) {
		extension = Util.processExtension(extension);

		String httpResponse = "HTTP/1.1 200 OK\r\n";
		httpResponse += "Accept-Ranges: bytes\r\n";
		httpResponse += "Content-Type: text/" + extension + "\r\n";
		httpResponse += "Connection: Close\r\n\r\n";
		String overallMessage = httpResponse + message;

		try {
			PrintStream pstream = new PrintStream(csocket.getOutputStream());
			pstream.println(overallMessage);
			pstream.close();
			csocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void sendResponse(ArrayList<String> lines, String extension)
			throws UnsupportedEncodingException, IOException {
		String message = Util.mergeArrayListOfStrings(lines);
		sendResponse(message, extension);
	}

	public void sendResponse404() throws UnsupportedEncodingException, IOException {
		OutputStream os = csocket.getOutputStream();
		String defaultMessage = "<!doctype html><html lang=en><head><meta charset=utf-8><title>Not Found</title></head><body><h1>Resource not found!</h1></body></html>";
		String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n" + defaultMessage;
		os.write(httpResponse.getBytes("UTF-8"));
	}

	public ArrayList<String> getClientRequest(Socket clientSocket) throws IOException {
		return getClientRequest(clientSocket, 200);
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
			if (line.equals("")) {
				break;
			}
			toReturn.add(line);
		}

		clientSocket.shutdownInput();

		System.out.println("Input shut down");

		return toReturn;
	}

	public String getClientRequestResource(ArrayList<String> clientRequest) {
		if (clientRequest.isEmpty()) {
			return "";
		}

		String firstLine = clientRequest.get(0);
		String[] divided = firstLine.split("\\s+");

		return Util.removeNFirstCharacters(divided[1], 1);
	}

	synchronized ArrayList<String> getTextResource(String path) throws IOException {
		if (path.equals("")) {
			path = "index.html";
		}
		ArrayList<String> lines = new ArrayList<String>();
		if (FileReader.fileExists(path)) {
			lines = FileReader.readLines(path);
		}
		return lines;
	}

	boolean resourceExists(String path) {
		return FileReader.fileExists(path) || path.equals("");
	}

	synchronized BufferedImage getImgResource(String path) throws IOException {
		return ImageIO.read(new File(path));
	}

	void sendImgResponse(String path, Socket socket, String type) throws IOException {
		String httpResponse = "HTTP/1.1 200 OK\r\n";
		httpResponse += "Accept-Ranges: bytes\r\n";
		httpResponse += "Content-Type: image/" + type + "\r\n";
		httpResponse += "Connection: Close\r\n\r\n";
		socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		BufferedImage image = getImgResource(path);
		ImageIO.write(image, type, socket.getOutputStream());
	}

	public static void main(String args[]) throws Exception {
		int port = Util.getUserInteger("Type in on which port server should listen: ");
		
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("Listening");
		
		while (true) {
			Socket sock = ssock.accept();
			System.out.println("Connected");
			new Thread(new MultiThreadServer(sock)).start();
		}
	}

	public void run() {
		try {
			ArrayList<String> clientRequest = getClientRequest(csocket);
			Util.printArrayList(clientRequest);

			String path = getClientRequestResource(clientRequest);
			ArrayList<String> resource = getTextResource(path);
			if (resourceExists(path)) {
				String extension = Util.getResourceExtension(path);
				if (Util.checkIfExtensionRefersToImg(extension)) {
					sendImgResponse(path, csocket, extension);
				} else {
					sendResponse(resource, extension);
				}
			} else {
				sendResponse404();
			}
			csocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
