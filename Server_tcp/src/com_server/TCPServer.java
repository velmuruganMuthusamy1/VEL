package com_server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer extends Thread {

	private ServerSocket ss;
	boolean fileReceived = false;
	boolean fileSent = false;

	public TCPServer(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {

				Socket clientSock = ss.accept();
				saveFile(clientSock);

				if (fileReceived) {
					sendFileToClient();
					fileReceived = false;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	int bytesRead;
	int current = 0;

	private void saveFile(Socket clientSock) throws IOException {

		DataInputStream ds = new DataInputStream(clientSock.getInputStream());

		DataInputStream clientData = new DataInputStream(ds);

		String fileName = clientData.readUTF();
		OutputStream output = new FileOutputStream("d:\\data1\\" + fileName);
		long size = clientData.readLong();
		byte[] buffer = new byte[1024];
		while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {

			output.write(buffer, 0, bytesRead);

			size -= bytesRead;
		}

		// Closing the FileOutputStream handle
		ds.close();
		clientData.close();
		output.close();
		fileReceived = true;

	}

	public void sendFileToClient() {
		try {
			final File folder = new File("d:\\data1");

			FileUtil fileUtil = new FileUtil();
			ArrayList<String> filelist = fileUtil.getFiles(folder);

			for (String file : filelist) {
				sendFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendFile(String file) throws IOException {

		Socket sock = ss.accept();

		File myFile = new File(file);
		byte[] mybytearray = new byte[(int) myFile.length()];

		FileInputStream fis = new FileInputStream(myFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		// bis.read(mybytearray, 0, mybytearray.length);

		DataInputStream dis = new DataInputStream(bis);
		dis.readFully(mybytearray, 0, mybytearray.length);

		OutputStream os = sock.getOutputStream();

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
		dos.close();

	}

	public static void main(String[] args) throws Exception {
		TCPServer fs = new TCPServer(3001);
		fs.start();
	}

}