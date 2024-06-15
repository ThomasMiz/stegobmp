package grupo3;

import grupo3.arguments.Arguments;
import grupo3.bmp.Bitmap;
import grupo3.exceptions.ProgramArgumentsException;
import grupo3.steganography.LsbxSteganography;
import grupo3.steganography.SteganographyMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private final static Scanner s = new Scanner(System.in);
    public static void main(String[] args) {
        Arguments arguments = null;
        try {
            arguments = Arguments.parse(Arrays.stream(args).iterator());
        } catch (ProgramArgumentsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            switch (arguments.request()) {
                case Embed -> embedMessage(arguments);
                case Extract -> extractMessage(arguments);
            }
        } catch (IOException e) {
            System.err.println("An unexpected error ocurred during execution: ");
            System.err.println(e.getMessage());
        }
    }

    private static void embedMessage(Arguments arguments) throws IOException {
        System.out.format("Reading file \"%s\"...%n", arguments.messageFile());
        byte[] message = Files.readAllBytes(Paths.get(arguments.messageFile()));

        System.out.format("Reading file \"%s\"...%n", arguments.carrierFile());
        Bitmap bitmap = Bitmap.readFromFile(arguments.carrierFile());
        System.out.format("\"%s\" has a width of %d and height of %d%n", arguments.carrierFile(), bitmap.getWidth(), bitmap.getHeight());

        int carrierSize = bitmap.getData().length;
        int maxHiddenSize = arguments.steganographyMethod().calculateHiddenSize(carrierSize);
        System.out.format("This means it can carry a message of up to %d bytes%n", maxHiddenSize);

        if (message.length > maxHiddenSize) {
            System.err.format("The message to hide is too long for this carrier! %d > %d%n", message.length, maxHiddenSize);
            return;
        }

        arguments.steganographyMethod().hideMessage(bitmap.getData(), message);

        System.out.format("Saving result to \"%s\"...", arguments.outputFile());
        bitmap.writeToFile(arguments.outputFile());
        System.out.println(" Done!");
    }

    private static void extractMessage(Arguments arguments) throws IOException {
        System.out.format("Reading file \"%s\"...%n", arguments.carrierFile());
        Bitmap bitmap = Bitmap.readFromFile(arguments.carrierFile());

        System.out.println("Extracting message...");
        byte[] message = arguments.steganographyMethod().extractMessage(bitmap.getData());

        System.out.format("Saving result to \"%s\"...", arguments.outputFile());
        Files.write(Paths.get(arguments.outputFile()), message);
        System.out.println(" Done!");
    }
}
