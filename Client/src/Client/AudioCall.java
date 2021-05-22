package Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioCall {
	private InetAddress ip;
	private int port;
	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// the line from which audio data will be read
	TargetDataLine targetLine;

	// the line from which audio data will be written
	SourceDataLine sourceLine = null;

	// Call is ongoing or not
	boolean isOnCall = false;

	public AudioCall(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		System.out.println(ip.getHostAddress() + " " + port);
	}

	// Define Audio Format
	private AudioFormat getAudioFormat() {
		float sampleRate = 2000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}

	// Initialize Lines for Recording
	private void initLines() {
		AudioFormat format = this.getAudioFormat();

		DataLine.Info targetLineInfo = new DataLine.Info(TargetDataLine.class, format);
		DataLine.Info sourceLineInfo = new DataLine.Info(SourceDataLine.class, format);

		// checks if system supports the data lines
		if (!AudioSystem.isLineSupported(targetLineInfo)) {
			System.out.println("Target Data Line not supported");
			System.exit(0);
		}
		if (!AudioSystem.isLineSupported(sourceLineInfo)) {
			System.out.println("Source Data Line not supported");
			System.exit(0);
		}

		try {
			targetLine = (TargetDataLine) AudioSystem.getLine(targetLineInfo);
			targetLine.open(format);
			sourceLine = (SourceDataLine) AudioSystem.getLine(sourceLineInfo);
			sourceLine.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		System.out.println(sourceLine.getLineInfo());

		System.out.println("Audio Data Lines Initialized");
	}

	private void sendCallData(DatagramSocket ds, DatagramPacket packet) {
		targetLine.start(); // start capturing

		byte[] soundData = new byte[2];
		// AudioInputStream ais = new AudioInputStream(targetLine);
		while (isOnCall) {
			if (targetLine.read(soundData, 0, soundData.length) > 0) {
				packet.setData(soundData, 0, soundData.length);
				try {
					ds.send(packet);
					// AudioSystem.write(ais, fileType, wavFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Speaking...");
			}
		}
		System.out.println("Call Finished");
	}

	private void rcvCallData(DatagramSocket ds, DatagramPacket packet) {
		sourceLine.start();

		byte[] soundData = new byte[2];
		packet.setData(soundData, 0, soundData.length);

		System.out.println(isOnCall);
		while (isOnCall) {
			// System.out.println("Inside loop");
			try {
				ds.receive(packet);
				sourceLine.write(soundData, 0, soundData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Listening...");
		}
		System.out.println("Call Finished");
	}

	public void start() {
		initLines();
		this.isOnCall = true;
		Thread sndThread = new Thread(new Runnable() {
			@Override
			public void run() {

				System.out.println("Send Thread Started");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buff = baos.toByteArray();

				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket();
				} catch (SocketException e) {
					e.printStackTrace();
				}
				DatagramPacket packet = new DatagramPacket(buff, buff.length, ip, port);
				sendCallData(ds, packet);
				ds.close();
			}
		});

		Thread rcvThread = new Thread(new Runnable() {
			@Override
			public void run() {

				System.out.println("Receive Thread Started");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buff = baos.toByteArray();

				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket(port);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				rcvCallData(ds, packet);
				ds.close();
			}
		});

		sndThread.start();
		rcvThread.start();
	}

	public void end() {
		this.isOnCall = false;
		stop();
	}

	// Finish Recording and Close lines
	private void stop() {

		// Close TargetDataLine
		targetLine.stop();
		targetLine.close();

		// Close SourceDataLine
		sourceLine.stop();
		sourceLine.close();

		System.out.println("Finished Audio Recording");

	}

}
