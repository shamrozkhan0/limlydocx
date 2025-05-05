package com.limlydocx;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

@SpringBootApplication
@PropertySource("classpath:security.properties")
public class LimlydocxApplication {



	public static void main(String[] args) {
		SpringApplication.run(LimlydocxApplication.class, args);

		Scanner scanner = new Scanner(System.in);
		System.out.println("Type 'run' to convert DOCX to HTML:");
		String command = scanner.nextLine();
		if (command.equalsIgnoreCase("run")) {
			try {
				new LimlydocxApplication().docxConverter();
			} catch (Docx4JException | IOException e) {
				System.err.println("Error converting document: " + e.getMessage());
				e.printStackTrace();
			}
		}
		scanner.close();
	}



	public void docxConverter() throws Docx4JException, IOException {
		String inputPath = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\document_20250505_142712_d890a6d9-775b-4749-9c1c-b721a6d56f98.docx";
		String outputHtmlPath = "E:\\limlydocx\\limlydocx\\src\\main\\resources\\testPdf\\output.html";

		// Load the DOCX file
		File docxFile = new File(inputPath);
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxFile);

		// Create settings for HTML conversion
		HTMLSettings htmlSettings = new HTMLSettings();
		htmlSettings.setWmlPackage(wordMLPackage);

		// Perform the conversion and write the output to a file
		try (OutputStream outputStream = new FileOutputStream(outputHtmlPath)) {
			Docx4J.toHTML(htmlSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_NONXSL);
		}

		System.out.println("DOCX to HTML conversion is done!");
	}



}