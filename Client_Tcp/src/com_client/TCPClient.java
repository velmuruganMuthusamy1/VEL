package com_client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPClient extends Thread {

	boolean fileSent = false;

	private Socket s;
	private String _host;
	private int _port;

	public void run() {
		while (true) {
			try {

				if (s.isClosed()) {
					s = new Socket(_host, _port);
				}
				saveFile(s);
				sendFileToServer(_host, _port);

				//
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendFileToServer(String host, int port) {
		try {
			final File folder = new File("d:\\data");
			FileUtil fileUtil = new FileUtil();
			ArrayList<String> filelist = fileUtil.getFiles(folder);

			if (filelist.size() > 0) {
				s = new Socket(host, port);
				for (String file : filelist) {
					sendFile(file);
					fileUtil.deleteFile(file);
				}
				fileSent = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public TCPClient(String host, int port) {
		_host = host;
		_port = port;
		try {
			s = new Socket(_host, _port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendFile(String file) throws IOException {

		File myFile = new File(file);
		byte[] mybytearray = new byte[(int) myFile.length()];

		FileInputStream fis = new FileInputStream(myFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		// bis.read(mybytearray, 0, mybytearray.length);

		DataInputStream dis = new DataInputStream(bis);
		dis.readFully(mybytearray, 0, mybytearray.length);

		OutputStream os = s.getOutputStream();

		// Sending file name and file size to the server
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeUTF(myFile.getName());
		dos.writeLong(mybytearray.length);
		dos.write(mybytearray, 0, mybytearray.length);
		dos.flush();

		// Sending file data to the server
		os.write(mybytearray, 0, mybytearray.length);
		os.flush();

		// Closing socket
		os.close();
		fis.close();
		dis.close();

		// boolean x = myFile.delete();

		dos.close();

	}

	int bytesRead;
	int current = 0;

	private void saveFile(Socket clientSock) throws IOException {

		InputStream istream = clientSock.getInputStream();
		if (istream != null) {

			DataInputStream ds = new DataInputStream(istream);

			DataInputStream clientData = new DataInputStream(ds);
			if (clientData.available() > 1) {
				String fileName = clientData.readUTF();
				OutputStream output = new FileOutputStream("d:\\data2\\" + fileName);
				long size = clientData.readLong();
				byte[] buffer = new byte[1024];
				while (size > 0
						&& (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {

					output.write(buffer, 0, bytesRead);

					size -= bytesRead;
				}

				// Closing the FileOutputStream handle
				ds.close();
				clientData.close();
				output.close();
			}
		}
	}

	public static void main(String[] args) throws Exception {

		TCPClient tcpClient = new TCPClient("Localhost", 3001);
		tcpClient.start();
		// fileUtil.deleteFile(file);

	}

}
