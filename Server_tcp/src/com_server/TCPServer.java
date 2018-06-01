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
				sendFileToClient();

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
		if (clientData.available() > 1) {

			String fileName = clientData.readUTF();

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
			String line;

			br = new BufferedReader(new InputStreamReader(ds));
			File file = new File("d:\\data1\\" + fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			while ((line = br.readLine()) != null) {
				writer.write(line + "--VERIFIED");
				writer.newLine();
			}
			br.close();
			writer.close();

		}

	}

	public void sendFileToClient() {
		try {
			final File folder = new File("d:\\data1");

			FileUtil fileUtil = new FileUtil();
			ArrayList<String> filelist = fileUtil.getFiles(folder);
			if (filelist.size() > 0) {

				for (String file : filelist) {
					sendFile(file);
				}
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

		bis.close();
		dis.close();
		// Closing socket
		os.close();
		dos.close();

	}

	public static void main(String[] args) throws Exception {
		TCPServer fs = new TCPServer(3001);
		fs.start();
	}

}