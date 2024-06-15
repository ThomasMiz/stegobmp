package grupo3.arguments;

import grupo3.encryption.EncryptionMode;
import grupo3.encryption.EncryptionOptions;
import grupo3.encryption.algorithms.*;
import grupo3.exceptions.ProgramArgumentsException;
import grupo3.steganography.LsbiSteganography;
import grupo3.steganography.LsbxSteganography;
import grupo3.steganography.SteganographyMethod;

import java.util.Iterator;

/**
 * Represents the parsed arguments passed to this program.
 *
 * @param request             Specifies whether the user requested to embed or extract a message.
 * @param carrierFile         A path to the file that contains the carrier message.
 * @param messageFile         A path to the file with the hidden message.
 * @param outputFile          A path to the file where the result should be written.
 * @param steganographyMethod The steganography algorithm to use.
 * @param encryptionOptions   The (optional) encryption options.
 */
public record Arguments(
        ArgumentRequest request,
        String carrierFile,
        String messageFile,
        String outputFile,
        SteganographyMethod steganographyMethod,
        EncryptionOptions encryptionOptions
) {
    /**
     * Specifies whether the user requested to embed or extract a message.
     */
    @Override
    public ArgumentRequest request() {
        return request;
    }

    /**
     * A path to the file that contains the carrier message.
     */
    @Override
    public String carrierFile() {
        return carrierFile;
    }

    /**
     * A path to the file with the hidden message.
     */
    @Override
    public String messageFile() {
        return messageFile;
    }

    /**
     * A path to the file where the result should be written.
     */
    @Override
    public String outputFile() {
        return outputFile;
    }

    /**
     * The steganography algorithm to use.
     */
    @Override
    public SteganographyMethod steganographyMethod() {
        return steganographyMethod;
    }

    /**
     * The (optional) encryption options. If null, then no encryption should be used.
     */
    @Override
    public EncryptionOptions encryptionOptions() {
        return encryptionOptions;
    }

    public static Arguments parse(Iterator<String> args) throws ProgramArgumentsException {
        ArgumentRequest request = null;
        String carrierFile = null;
        String messageFile = null;
        String outputFile = null;
        SteganographyMethod steganographyMethod = null;

        EncryptionAlgorithm encryptionAlgorithm = null;
        EncryptionMode encryptionMode = null;
        String encryptionPassword = null;

        while (args.hasNext()) {
            String arg = args.next();

            if (arg.equalsIgnoreCase("-embed")) {
                if (request != null) {
                    throw new ProgramArgumentsException("Please specify -embed or -extract only once");
                }
                request = ArgumentRequest.Embed;
            } else if (arg.equalsIgnoreCase("-extract")) {
                if (request != null) {
                    throw new ProgramArgumentsException("Please specify -embed or -extract only once");
                }
                request = ArgumentRequest.Extract;
            } else if (arg.equalsIgnoreCase("-in")) {
                if (messageFile != null) {
                    throw new ProgramArgumentsException("Please specify -in only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected file after -in");
                }

                messageFile = args.next();
                if (messageFile.isEmpty() || messageFile.isBlank()) {
                    throw new ProgramArgumentsException("Message file may not be empty");
                }
            } else if (arg.equalsIgnoreCase("-p")) {
                if (carrierFile != null) {
                    throw new ProgramArgumentsException("Please specify -p only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected file after -p");
                }

                carrierFile = args.next();
                if (carrierFile.isEmpty() || carrierFile.isBlank()) {
                    throw new ProgramArgumentsException("Carrier file may not be empty");
                }
            } else if (arg.equalsIgnoreCase("-out")) {
                if (outputFile != null) {
                    throw new ProgramArgumentsException("Please specify -out only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected file after -out");
                }

                outputFile = args.next();
                if (outputFile.isEmpty() || outputFile.isBlank()) {
                    throw new ProgramArgumentsException("Output file may not be empty");
                }
            } else if (arg.equalsIgnoreCase("-steg")) {
                if (steganographyMethod != null) {
                    throw new ProgramArgumentsException("Please specify -steg only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected steganography method after -steg");
                }

                String methodRaw = args.next();
                String method = methodRaw.toLowerCase().trim();
                if (method.startsWith("lsb") && method.length() == 4) {
                    char ch = method.charAt(3);
                    if (ch > '0' && ch <= '8') {
                        steganographyMethod = new LsbxSteganography(ch - '0');
                    } else if (ch == 'i') {
                        steganographyMethod = new LsbiSteganography();
                    }
                }

                if (steganographyMethod == null) {
                    throw new ProgramArgumentsException("Unknown steganography method: " + methodRaw);
                }
            } else if (arg.equalsIgnoreCase("-a")) {
                if (encryptionAlgorithm != null) {
                    throw new ProgramArgumentsException("Please specify -a only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected encryption algorithm after -a");
                }

                String algorithm = args.next();
                encryptionAlgorithm = switch (algorithm.toLowerCase().trim()) {
                    case "aes128" -> AES128Encryption.getInstance();
                    case "aes192" -> AES192Encryption.getInstance();
                    case "aes256" -> AES256Encryption.getInstance();
                    case "des" -> DESEncryption.getInstance();
                    default -> throw new ProgramArgumentsException("Unknown encryption algorithm: " + algorithm);
                };
            } else if (arg.equalsIgnoreCase("-m")) {
                if (encryptionMode != null) {
                    throw new ProgramArgumentsException("Please specify -m only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected block cypher operation mode after -m");
                }

                String mode = args.next();
                encryptionMode = switch (mode.toLowerCase().trim()) {
                    case "ecb" -> EncryptionMode.ECB;
                    case "cbc" -> EncryptionMode.CBC;
                    case "cfb" -> EncryptionMode.CFB;
                    case "ofb" -> EncryptionMode.OFB;
                    default -> throw new ProgramArgumentsException("Unknown cypher operation mode: " + mode);
                };
            } else if (arg.equalsIgnoreCase("-pass")) {
                if (encryptionPassword != null) {
                    throw new ProgramArgumentsException("Please specify -pass only once");
                }

                if (!args.hasNext()) {
                    throw new ProgramArgumentsException("Expected encryption password after -pass");
                }

                encryptionPassword = args.next();
                if (encryptionPassword.isEmpty()) {
                    throw new ProgramArgumentsException("Password may not be empty");
                }
            }
        }

        if (request == null) {
            throw new ProgramArgumentsException("You must specify an operation with either -embed or -extract");
        }

        if (carrierFile == null) {
            throw new ProgramArgumentsException("You must specify a carrier file with -p <file>");
        }

        if (request == ArgumentRequest.Embed && messageFile == null) {
            throw new ProgramArgumentsException("You must specify a hidden message file with -in <file>");
        }

        if (request == ArgumentRequest.Extract && messageFile != null) {
            throw new ProgramArgumentsException("Do not specify an -in file when using -extract");
        }

        if (outputFile == null) {
            throw new ProgramArgumentsException("You must specify an output file with -out <file>");
        }

        if (steganographyMethod == null) {
            throw new ProgramArgumentsException("You must specify a steganography method with -steg <LSB1 | LSB2 | ... | LSB7 | LSBI>");
        }

        // TODO: SET DEFAULT VALUES
        EncryptionOptions encryptionOptions = null;
        if (encryptionAlgorithm != null || encryptionMode != null || encryptionPassword != null) {
            String message = "";
            if (encryptionAlgorithm == null) {
                message += "    -> Specify an encryption algorithm: -a <aes128 | aes192 | aes256 | des>\n";
            }

            if (encryptionMode == null) {
                message += "    -> Specify a block cypher operation mode: -m <ecb | cfb | ofb | cbc>";
            }

            if (encryptionPassword == null) {
                message += "    -> Specify an encryption password: -pass <password>";
            }

            if (!message.isEmpty()) {
                throw new ProgramArgumentsException("When using encryption, all three encryption arguments must be specified:" + message);
            }

            encryptionOptions = new EncryptionOptions(encryptionAlgorithm, encryptionMode, encryptionPassword);
        }

        return new Arguments(request, carrierFile, messageFile, outputFile, steganographyMethod, encryptionOptions);
    }
}
