package Server;

import java.net.*;
import java.io.*;

public class GoogleTranslate {
	public static String Translate(String targetLanguage, String toTranslate) {
		final String APIKey = "AIzaSyC841T29YRYJ8-l3u3A27-UezMNsPHCIeA";
		String parsedToTranslate = "";
		for (int i = 0; i < toTranslate.length(); i++) {
			if (toTranslate.charAt(i) == ' ') {
				parsedToTranslate += '%';
			} else {
				parsedToTranslate += toTranslate.charAt(i);
			}
		}
		URL url = null;
		try {
			url = new URL("https://translation.googleapis.com/language/translate/v2?target=" + targetLanguage + "&key="
					+ APIKey + "&q=" + parsedToTranslate);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		BufferedReader in;
		String temp = "";
		String buffer = "";
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			while ((temp = in.readLine()) != null) {
				buffer += temp;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String translated = "";
		for (int i = 67; i < buffer.length(); i++) {
			if (buffer.charAt(i) == '"') {
				break;
			}
			translated += buffer.charAt(i);
		}
		return translated;
	}
}
