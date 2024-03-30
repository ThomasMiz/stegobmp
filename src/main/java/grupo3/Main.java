package grupo3;

import grupo3.bmp.Bitmap;
import grupo3.steganography.Lsb1Steganography;
import grupo3.steganography.SteganographyMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static Scanner s = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Hello world!");

        try {
            while (true) {
                System.out.print("Do you want to hide a message (1) or reveal one (2)? ");
                String input = s.nextLine().trim();
                if (input.equals("1")) {
                    hideMessage();
                } else if (input.equals("2")) {
                    revealMessage();
                } else {
                    System.out.println("That's not an option 😒👎👎");
                }
            }
        } catch (Exception e) {
            System.out.println("pero la PUCHA");
            System.out.println(e);
        }
    }

    private static void hideMessage() throws IOException {
        System.out.println("Looking for file \"carrier.bmp\"...");
        Bitmap bitmap = Bitmap.readFromFile("carrier.bmp");
        System.out.format("\"carrier.bmp\" has a width of %d and height of %d%n", bitmap.getWidth(), bitmap.getHeight());

        SteganographyMethod steg = new Lsb1Steganography();
        int carrierSize = bitmap.getData().length;
        int maxHiddenSize = steg.calculateHiddenSize(carrierSize);
        System.out.format("This means it can carry a message of up to %d bytes%n", maxHiddenSize);

        System.out.print("Enter the message to hide: ");
        String line = s.nextLine().trim();
        if (line.length() > maxHiddenSize) {
            System.out.println("The message is too large for the carrier! Aborting");
            return;
        }

        byte[] messageBytes = line.getBytes(StandardCharsets.UTF_8);
        steg.hideMessage(bitmap.getData(), messageBytes);

        System.out.print("Saving result to \"hidden.bmp\"...");
        bitmap.writeToFile("hidden.bmp");
        System.out.println(" Done!");
    }

    private static void revealMessage() throws IOException {
        System.out.println("Looking for file \"hidden.bmp\"...");
        Bitmap bitmap = Bitmap.readFromFile("hidden.bmp");
        byte[] messageBytes = new Lsb1Steganography().extractMessage(bitmap.getData());
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        System.out.format("The hidden message is: %s%n", message);
    }
}
