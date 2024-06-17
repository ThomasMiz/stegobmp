package grupo3;

import grupo3.arguments.Arguments;
import grupo3.bmp.Bitmap;
import grupo3.exceptions.EncryptionException;
import grupo3.exceptions.ProgramArgumentsException;
import grupo3.steganography.SteganographyMethod;
import grupo3.utils.FileUtils;
import grupo3.utils.SteganographyDataProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        final Arguments arguments = parseArguments(args);

        try {
            switch (arguments.request()) {
                case Embed:
                    embedMessage(arguments);
                    break;
                case Extract:
                    extractMessage(arguments);
                    break;
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during execution: ");
            System.err.println(e.getMessage());
        }
    }

    private static Arguments parseArguments(String[] args) {
        Arguments arguments = null;
        try {
            arguments = Arguments.parse(Arrays.stream(args).iterator());
        } catch (ProgramArgumentsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return arguments;
    }

    private static void embedMessage(Arguments arguments) {
        try {
            System.out.format("Reading file \"%s\"...%n", arguments.messageFile());
            byte[] message = Files.readAllBytes(Paths.get(arguments.messageFile()));

            System.out.format("Reading file \"%s\"...%n", arguments.carrierFile());
            Bitmap bitmap = Bitmap.readFromFile(arguments.carrierFile());
            System.out.format("\"%s\" has a width of %d and height of %d%n", arguments.carrierFile(), bitmap.getWidth(), bitmap.getHeight());

            String fileExtension = FileUtils.getFileExtension(arguments.messageFile());

            int carrierSize = bitmap.getData().length;
            int maxHiddenSize = arguments.steganographyMethod().calculateHiddenSize(carrierSize, fileExtension);
            System.out.format("This means it can carry a message of up to %d bytes%n", maxHiddenSize);

            if (message.length > maxHiddenSize) {
                System.err.format("The message to hide is too long for this carrier! %d > %d%n", message.length, maxHiddenSize);
                return;
            }
            if (arguments.encryptionOptions() == null) {
                arguments.steganographyMethod().hideMessageWithExtension(bitmap.getData(), message, fileExtension);
            } else {

                // TODO: Modularize code with LsbiSteganography

                byte[] extensionBytes = fileExtension.getBytes(StandardCharsets.UTF_8);

                // Create a new byte array for the combined message, extension, and null terminator
                byte[] extendedMessage = new byte[message.length + extensionBytes.length + 1 + 4];
                ByteBuffer b = ByteBuffer.allocate(4);
                b.putInt(message.length);
                System.arraycopy(b.array(), 0, extendedMessage, 0, 4);
                System.arraycopy(message, 0, extendedMessage, 4, message.length);
                System.arraycopy(extensionBytes, 0, extendedMessage, 4 + message.length, extensionBytes.length);

                // Add the null terminator at the end
                extendedMessage[extendedMessage.length - 1] = 0;

                byte[] encryptedMessage = arguments.encryptionOptions().encrypt(extendedMessage);

                arguments.steganographyMethod().hideMessage(bitmap.getData(), encryptedMessage);
            }

            System.out.format("Saving result to \"%s\"...", arguments.outputFile());
            bitmap.writeToFile(arguments.outputFile());
            System.out.println(" Done!");
        } catch (IOException | ProgramArgumentsException | EncryptionException e) {
            System.err.println("Error embedding message:");
            System.err.println(e.getMessage());
        }
    }

    private static void extractMessage(Arguments arguments) {
        try {
            System.out.format("Reading file \"%s\"...%n", arguments.carrierFile());
            final Bitmap bitmap = Bitmap.readFromFile(arguments.carrierFile());

            System.out.println("Extracting message...");

            byte[] messageData;
            String fileExtension;

            if (arguments.encryptionOptions() == null) {
                SteganographyMethod.ExtractResult result = arguments.steganographyMethod().extractMessageWithExtension(bitmap.getData());
                messageData = result.message;
                fileExtension = result.fileExtension;
            } else {
                final byte[] encryptedMessage = arguments.steganographyMethod().extractMessage(bitmap.getData());
                final byte[] decryptedBytes = arguments.encryptionOptions().decrypt(encryptedMessage);

                final SteganographyDataProcessor steganographyDataProcessor = new SteganographyDataProcessor(decryptedBytes);
                messageData = steganographyDataProcessor.getData();
                fileExtension = steganographyDataProcessor.getFileExtension();
            }

            final String outputFilePath = arguments.outputFile() + fileExtension;
            FileUtils.writeToFile(outputFilePath, messageData);

            System.out.format("Saving result to \"%s%s\"... Done!%n", arguments.outputFile(), fileExtension);

        } catch (IOException | EncryptionException e) {
            System.err.println("Error extracting message: " + e.getMessage());
        }
    }
}
