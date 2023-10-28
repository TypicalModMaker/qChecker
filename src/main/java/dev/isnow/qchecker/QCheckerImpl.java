package dev.isnow.qchecker;

import dev.isnow.qchecker.checker.connection.CheckerIntegration;
import dev.isnow.qchecker.checker.protocol.ProtocolVersion;
import dev.isnow.qchecker.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QCheckerImpl {

    public static File outputFile;

    private final Queue<String> serverQueue = new ArrayDeque<>();
    private int runningThreads = 0;

    public QCheckerImpl(final String inputFile, final int threadAmountProvided) {
        System.out.println("  /$$$$$$                 /$$$$$$ \n" +
                " /$$__  $$               /$$__  $$\n" +
                "| $$  \\ $$ /$$  /$$  /$$| $$  \\ $$\n" +
                "| $$  | $$| $$ | $$ | $$| $$  | $$\n" +
                "| $$  | $$| $$ | $$ | $$| $$  | $$\n" +
                "| $$/$$ $$| $$ | $$ | $$| $$/$$ $$\n" +
                "|  $$$$$$/|  $$$$$/$$$$/|  $$$$$$/\n" +
                " \\____ $$$ \\_____/\\___/  \\____ $$$\n" +
                "      \\__/                    \\__/\n" +
                "                                  \n" +
                "                                  ");
        System.out.println("QChecker 2.0 | Made by Isnow");

        final File file = new File(inputFile);

        outputFile = FileUtil.create(new File(file.getParentFile(), "qChecker.txt"));

        final ArrayList<String> servers = new ArrayList<>(50000);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.startsWith("(")) {
                    continue;
                }
                servers.add(line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the server file.");
            return;
        }

        serverQueue.addAll(servers);
        System.out.println("Found " + servers.size() + " servers.");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        int threadAmount = Math.min(servers.size(), 100);
        if(threadAmountProvided != -1) {
            threadAmount = Math.min(servers.size(), threadAmountProvided);
        }

        for(int i = 0; i < threadAmount; i ++) {
            Thread t = new Thread(() -> {
                runningThreads++;
                while (!serverQueue.isEmpty()) {
                    String serverLine = serverQueue.poll();
                    if(serverLine == null || serverLine.isEmpty()) {
                        continue;
                    }
                    String ipport = serverLine.split("\\(")[1].split("\\)")[0];
                    String ip = ipport.split(":")[0];
                    int port = Integer.parseInt(ipport.split(":")[1]);
                    CheckerIntegration connection = new CheckerIntegration();

                    String[] parts = serverLine.split("\\)");

                    String response = "FAILED - Failed to connect. [NULL STRING]";

                    try {
                        if (parts.length >= 4) {
                            String version = "1.8.8";
                            if(parts[2] != null) {
                                version = parts[2].replaceAll("[()]", "");
                            }
                            Pattern pattern = Pattern.compile("\\d+(\\.\\d+)*");
                            Matcher matcher = pattern.matcher(version);
                            if (matcher.find()) {
                                String versionString = matcher.group();
                                ProtocolVersion protocol = ProtocolVersion.getProtocolByName(versionString);
                                response = connection.connect(ip, port, protocol.getVersion(), 0, new ArrayList<>());
                            } else {
                                response = connection.connect(ip, port, 47, 0, new ArrayList<>());
                            }
                        } else {
                            connection.connect(ip, port, 47, 0, new ArrayList<>());
                        }
                    } catch (IOException|InterruptedException e) {
                        if (e.getMessage() == null) {
                            continue;
                        }
                        if (e.getMessage().contains("Connection refused")) {
                            response = "FAILED - Server Offline.";
                        } else if (e.getMessage().contains("timed out")) {
                            response = "FAILED - Timeout";
                        } else {
                            response = "FAILED - Failed to connect. " + e.getMessage();
                        }
                    }

                    connection.close();

                    FileUtil.save(serverLine + " - " + response, QCheckerImpl.outputFile);
                    System.out.println(serverLine + " - " + response);
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ignored) {}
                }
                System.out.println("Thread " + Thread.currentThread().getName() + " finished.");
                runningThreads--;
            });
            t.start();
        }
        while (runningThreads > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException igored) {}
        }
        System.out.println("Output saved to " + outputFile.getAbsolutePath());
    }
}
