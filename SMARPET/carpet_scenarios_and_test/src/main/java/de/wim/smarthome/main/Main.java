package de.wim.smarthome.main;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Main {

	public static void main(String[] args) {
		try {

			Socket socket = IO.socket("http://192.168.5.5:8000");
			socket.disconnect();
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				public void call(Object... args) {
					System.out.println("connect");
				}

			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
				public void call(Object... arg0) {
					System.out.println("disconnect");
				}

			}).on("raw", new Emitter.Listener() {

				public void call(Object... arg0) {
					String s = new Date() + arg0[0].toString() + "\n";
					System.out.println(s);
					try {
						FileUtils.writeStringToFile(new File("raw_test.txt"), s, true);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}).on("cluster", new Emitter.Listener() {

				public void call(Object... arg0) {

				}

			});
			// .on("fall", new Emitter.Listener() {
			//
			// public void call(Object... arg0) {
			// String s = new Date() + ", Sturz: " + arg0[0] + "\n";
			// System.out.println( s);
			// try {
			// FileUtils.writeStringToFile(new File("raw_test.txt"), s,
			// true);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// });
			socket.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
