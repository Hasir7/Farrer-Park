package com.example.farrerpark;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ReadPDF {
    private static final String SOURCE_FOLDER = "src/main/resources/source";
    private static final String DESTINATION_FOLDER = "src/main/resources/destination";
    private static final Set<String> keyLines = new HashSet<>(Arrays.asList(
            "Patient Name", "Study Date", "Admission ID", "MRN", "Page", "Age", "Gender", "Race",
            "Date of Birth", "Account Number", "Study Number"
    ));

    public static void main(String[] args) throws IOException, InterruptedException {
        Path sourceDir = Paths.get(SOURCE_FOLDER);
        Files.createDirectories(sourceDir); // Ensure the source folder exists
        Files.createDirectories(Paths.get(DESTINATION_FOLDER)); // Ensure the destination folder exists

        // Process any existing PDF files in the source folder
        processExistingFiles(sourceDir);

        // Start monitoring for new files
        WatchService watchService = FileSystems.getDefault().newWatchService();
        sourceDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        System.out.println("Monitoring folder for new PDF files...");

        while (true) {
            WatchKey key = watchService.take(); // Wait for a new file
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path newFilePath = sourceDir.resolve((Path) event.context());
                    File pdfFile = newFilePath.toFile();

                    if (pdfFile.getName().endsWith(".pdf")) {
                        System.out.println("New PDF detected: " + pdfFile.getName());
                        readAndProcessPDF(pdfFile);
                        moveFile(pdfFile, Paths.get(DESTINATION_FOLDER, pdfFile.getName()));
                    }
                }
            }
            key.reset();
        }
    }

    private static void processExistingFiles(Path sourceDir) {
        System.out.println("Processing existing PDF files in the source folder...");

        File folder = sourceDir.toFile();
        File[] pdfFiles = folder.listFiles((dir, name) -> name.endsWith(".pdf"));

        if (pdfFiles != null) {
            for (File pdfFile : pdfFiles) {
                System.out.println("Processing existing file: " + pdfFile.getName());
                readAndProcessPDF(pdfFile);
                moveFile(pdfFile, Paths.get(DESTINATION_FOLDER, pdfFile.getName()));
            }
        }
    }

    private static void readAndProcessPDF(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String pdfText = pdfStripper.getText(document);
            String[] lines = pdfText.split("\n");

            List<String> filteredLines = filterLinesMatching(lines, keyLines);
            PatientDet patientDet = parseLine(filteredLines);

            // Set the number of pages in the document
            patientDet.setNoOfPg(document.getNumberOfPages());

            // Print formatted output
            System.out.println("\n--- Processing Report: " + pdfFile.getName() + " ---"); // Heading
            System.out.println(patientDet); // This will call the toString() method from PatientDet
            System.out.println("\n--- End of Report ---\n"); // End of report with gap
        } catch (IOException e) {
            System.err.println("Error reading PDF file: " + e.getMessage());
        }
    }

    private static List<String> filterLinesMatching(String[] lines, Set<String> keyLines) {
        return Arrays.stream(lines)
                .filter(line -> keyLines.stream().anyMatch(line::contains))
                .collect(Collectors.toList());
    }

    private static PatientDet parseLine(List<String> lines) {
        // Updated regex patterns for additional fields
        Pattern patientNamePattern = Pattern.compile("Patient Name[:]?\\s*(.*?)(?=\\s+Study Date|\\s*$)", Pattern.CASE_INSENSITIVE);
        Pattern studyDatePattern = Pattern.compile("Study Date[:]?\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Pattern mrnPattern = Pattern.compile("MRN[:]?\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Pattern admissionIdPattern = Pattern.compile("Admission ID[:]?\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Pattern studyNumberPattern = Pattern.compile("Study Number[:]?\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Pattern dobPattern = Pattern.compile("Date of Birth[:]?\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Pattern agePattern = Pattern.compile("Age[:]?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Pattern genderPattern = Pattern.compile("Gender[:]?\\s*(\\w+)", Pattern.CASE_INSENSITIVE);

        PatientDet pDet = new PatientDet();
        for (String line : lines) {
            Matcher matcher = patientNamePattern.matcher(line);
            if (matcher.find()) pDet.setPatientName(matcher.group(1).trim());

            matcher = studyDatePattern.matcher(line);
            if (matcher.find()) pDet.setStudyDateStr(matcher.group(1).trim());

            matcher = mrnPattern.matcher(line);
            if (matcher.find()) pDet.setMrn(matcher.group(1).trim());

            matcher = admissionIdPattern.matcher(line);
            if (matcher.find()) pDet.setAdmissionId(matcher.group(1).trim());

            matcher = studyNumberPattern.matcher(line);
            if (matcher.find()) pDet.setStudyNumber(matcher.group(1).trim());

            matcher = dobPattern.matcher(line);
            if (matcher.find()) pDet.setDateOfBirth(matcher.group(1).trim());

            matcher = agePattern.matcher(line);
            if (matcher.find()) pDet.setAge(Integer.parseInt(matcher.group(1).trim()));

            matcher = genderPattern.matcher(line);
            if (matcher.find()) pDet.setGender(matcher.group(1).trim());
        }
        return pDet;
    }

    private static void moveFile(File file, Path destination) {
        try {
            Files.move(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved " + file.getName() + " to " + destination);
        } catch (IOException e) {
            System.err.println("Error moving file: " + e.getMessage());
        }
    }
}


