package com.dynatrace.http.permissions;

import java.util.Scanner;

public class Unauthorized {

	public static String getMissingPermission(String serverResponse) {
		if (serverResponse == null) {
			return null;
		}
		try (Scanner scanner = new Scanner(serverResponse)) {
			String line = null;
			if (scanner.hasNextLine()) {
				line = scanner.nextLine();
			}
			while (line != null) {
				String permission = getMissingPermissionWithinLine(line);
				if (permission != null) {
					return permission;
				}
				if (scanner.hasNextLine()) {
					line = scanner.nextLine();
				} else {
					line = null;
				}
			}
		}
		return null;
	}
	
	private static String getMissingPermissionWithinLine(String line) {
		if (line == null) {
			return null;
		}
		String trimmedLine = line.trim();
		if (trimmedLine.contains("Administrative Permission denied;")) {
			return "Administrative Permission";
		}
		int idx = trimmedLine.indexOf("Permission denied:");
		if (idx < 0) {
			return null;
		}
		trimmedLine = trimmedLine.substring(idx + "Permission denied:".length()).trim();
		int idxSemicolon = trimmedLine.indexOf(';');
		int idxComma = trimmedLine.indexOf(',');
		idx = -1;
		if (idxSemicolon >= 0) {
			idx = idxSemicolon;
		}
		if ((idxComma >= 0) && ((idxComma < idx) || (idx < 0))) {
			idx = idxComma;
		}
		if (idx < 0) {
			return null;
		}
		trimmedLine = trimmedLine.substring(0, idx);
		return trimmedLine.trim();
	}
}
