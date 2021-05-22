package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

public class Logger {
	private File logFile;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private PrintWriter printWriter;
	private Timestamp timeStamp;

	public Logger() {
		this.logFile = new File("serverLog/log.txt");
		if (!logFile.exists()) {
			File directory = new File("ServerLog");
			if (!directory.exists()) {
				directory.mkdir();
			}
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileWriter = new FileWriter(logFile);

		} catch (IOException e) {
			e.printStackTrace();
		}
		bufferedWriter = new BufferedWriter(fileWriter);
		printWriter = new PrintWriter(bufferedWriter);
	}

	public void ServerStarted() {
		timeStamp = new Timestamp(new Date().getTime());
		printWriter.println(timeStamp + "\tServer Started");
		printWriter.flush();
	}

	public void ClientMachineConnected() {
		timeStamp = new Timestamp(new Date().getTime());
		printWriter.println(timeStamp + "\tClient Machine Connected");
		printWriter.flush();
	}
}
