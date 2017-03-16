package com.aratech.lectoras.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

import com.aratech.lectoras.beans.Reg;

public class FileReader {

	File fileToRead;

	public final static String FILE_NOT_EXISTS = "No existe el archivo especificado";

	public FileReader(String fileNameWithExtensionAndPath)
			throws FileReaderException {
		fileToRead = new File(fileNameWithExtensionAndPath);

	}

	public ArrayList<Reg> proccessFile() throws FileReaderException {
		ArrayList<Reg> result = new ArrayList<Reg>();

		BufferedReader bfReader = null;
		try {
			FileInputStream reader = new FileInputStream(fileToRead);
			InputStreamReader is = new InputStreamReader(reader, "ISO-8859-1");
			bfReader = new BufferedReader(is);

			String linea = null;
			int lineCount = 1;
			try {
				while ((linea = bfReader.readLine()) != null) {
					Reg newObject = Reg.loadRegObjectFromImportLine(linea);
					if (newObject == null) {
						Log.e("ERROR IMPORTACION",
								"No se reconoce el tipo de la linea "
										+ lineCount);
					} else {
						result.add(newObject);
					}
					lineCount++;
				}
				bfReader.close();
			} catch (Exception ex) {
				if (bfReader != null)
					bfReader.close();

				ex.printStackTrace();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}

	public class FileReaderException extends Exception {

		private static final long serialVersionUID = 15L;
		private String message;

		public FileReaderException(String exceptionMessage) {
			message = exceptionMessage;
		}

		public String getExceptionMessage() {
			return this.message;
		}

	}

	
	
}
