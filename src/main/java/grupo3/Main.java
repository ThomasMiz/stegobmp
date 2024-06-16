package grupo3;

import grupo3.arguments.Arguments;
import grupo3.bmp.Bitmap;
import grupo3.encryption.EncryptionOptions;
import grupo3.exceptions.EncryptionException;
import grupo3.exceptions.ProgramArgumentsException;
import grupo3.utils.DataUtils;
import grupo3.utils.FileUtils;

import java.io.IOException;
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

            int carrierSize = bitmap.getData().length;
            int maxHiddenSize = arguments.steganographyMethod().calculateHiddenSize(carrierSize);
            System.out.format("This means it can carry a message of up to %d bytes%n", maxHiddenSize);

            if (message.length > maxHiddenSize) {
                System.err.format("The message to hide is too long for this carrier! %d > %d%n", message.length, maxHiddenSize);
                return;
            }
            if (arguments.encryptionOptions() == null) {
                String fileExtension = FileUtils.getFileExtension(arguments.messageFile());
                arguments.steganographyMethod().hideMessageWithExtension(bitmap.getData(), message, fileExtension);
            } else {
                arguments.steganographyMethod().hideMessage(bitmap.getData(), message);
            }

            System.out.format("Saving result to \"%s\"...", arguments.outputFile());
            bitmap.writeToFile(arguments.outputFile());
            System.out.println(" Done!");
        } catch (IOException | ProgramArgumentsException e) {
            System.err.println("Error embedding message:");
            System.err.println(e.getMessage());
        }
    }

    private static void extractMessage(Arguments arguments) {
        try {
            System.out.format("Reading file \"%s\"...%n", arguments.carrierFile());
            final Bitmap bitmap = Bitmap.readFromFile(arguments.carrierFile());

            System.out.println("Extracting message...");

            if (arguments.encryptionOptions() == null) {
                byte[] message = arguments.steganographyMethod().extractMessageWithExtension(bitmap.getData());
                final String outputFilePath = arguments.outputFile() + arguments.steganographyMethod().getFileExtension();
                FileUtils.writeToFile(outputFilePath, message);
            } else {
                byte[] encryptedMessage = arguments.steganographyMethod().extractMessage(bitmap.getData());
                final EncryptionOptions options = arguments.encryptionOptions();

                byte[] decryptedBytes;
                try {
                    decryptedBytes = options.algorithm().decrypt(encryptedMessage, options.mode(), options.password());
                } catch (Exception e) {
                    throw new EncryptionException("Error decrypting: " + e.getMessage());
                }

                final DataUtils dataUtils = new DataUtils(decryptedBytes);
                byte[] data = dataUtils.getData();
                String fileExtension = dataUtils.getFileExtension();

                String outputFile = arguments.outputFile() + fileExtension;
                FileUtils.writeToFile(outputFile, data);
            }

            System.out.format("Saving result to \"%s%s\"...", arguments.outputFile(), arguments.steganographyMethod().getFileExtension());
            System.out.println(" Done!");
        } catch (IOException | EncryptionException e) {
            System.err.println("Error extracting message:");
            System.err.println(e.getMessage());
        }
    }
}
