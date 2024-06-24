package grupo3.steganography;

import grupo3.utils.*;

import java.nio.charset.StandardCharsets;

public class LsbiSteganography implements SteganographyMethod {

    @Override
    public int calculateCarrierSize(int messageSize, String fileExtension) {
        // Four extra bytes for storing the length of the message
        messageSize += 4;

        // If there's a file extension in use, then count those bytes too, plus the '\0'
        if (fileExtension != null) {
            messageSize += fileExtension.getBytes(StandardCharsets.UTF_8).length + 1;
        }

        // Calculate the total amount of bits the message has
        int messageSizeBits = messageSize * 8;

        // 4 bits are necessary to store the inversions, 3 will go in the first RGB bytes and the
        // last one will go in the first B byte of the next RGB bytes.
        // The first byte of the message will go in the next G value, and the next R byte will not store anything.
        return 4 + (messageSizeBits * 3) / 2;
    }

    @Override
    public int calculateHiddenSize(int carrierSize, String fileName) {
        // A carrier of that size will have 2 bits every 3 bytes, since nothing is stored under the R byte.
        int totalSizeBits = carrierSize / 3 * 2;

        // Remove the first four bytes which are used to indicate the length of the message
        int messageSize = totalSizeBits - 4;

        // Remove the last few bytes used for the file extension, if one is present, plus the '\0'
        if (fileName != null) {
            messageSize -= fileName.getBytes(StandardCharsets.UTF_8).length + 1;
        }

        // Make sure we didn't go below 0
        return Math.max(0, messageSize);
    }

    private void hideMessageInCarrier(byte[] carrier, BitIterator bitIterator) {
        // Array with two integers to store the appearances of each pattern '11' '10' '01' '00'
        // and how many of them where flipped
        IntPair[] patterCounter = new IntPair[4];
        for (int i = 0; i < patterCounter.length; i++) {
            patterCounter[i] = new IntPair();
        }

        // The first four bytes are to store the inversions
        int i = 4;
        byte carrierMask = (byte) 0b11111110;

        // Count the appearance of each pattern and the needed inversions
        while (i < carrier.length && bitIterator.hasNextBit()) {
            byte hiddenBit = bitIterator.nextBit();
            int pattern = getPattern(carrier[i]);
            patterCounter[pattern].incrementAppearances();

            byte newValue = (byte) ((carrier[i] & carrierMask) | hiddenBit);
            if (carrier[i] != newValue) {
                carrier[i] = newValue;
                patterCounter[pattern].incrementInversions();
            }
            // We just need to hide in G and B, R is skipped
            i = getNextIdxSkipRed(i);
        }

        // Make the flips when necessary
        for (int j = 0; j < patterCounter.length; j++) {
            if (patterCounter[j].getAppearances() < patterCounter[j].getInversions() * 2) {
                carrier[j] |= 0b00000001;
                // TODO, improve. If all the bytes need to be flipped, four iterations are made
                flipPattern(carrier, j, i);
            } else {
                carrier[j] &= (byte) 0b11111110;
            }
        }
    }

    @Override
    public void hideMessage(byte[] carrier, byte[] message) {
        hideMessageWithExtension(carrier, message, null);
    }

    @Override
    public void hideMessageWithExtension(byte[] carrier, byte[] message, String fileExtension) {
        byte[] extendedMessage = getExtendedMessageWithoutLength(carrier, message, fileExtension);
        final BitIterator bitIterator = new ConcatBitIterator(new IntBitIterator(message.length), new ByteArrayBitIterator(extendedMessage));
        hideMessageInCarrier(carrier, bitIterator);
    }

    @Override
    public byte[] extractMessage(byte[] carrier) {
        SkipByteArrayBitIterator bits = initializeBits(carrier);
        int messageLength = readMessageLength(bits);
        return readMessage(bits, messageLength);
    }

    @Override
    public ExtractResult extractMessageWithExtension(byte[] carrier) {
        SkipByteArrayBitIterator bits = initializeBits(carrier);
        int messageLength = readMessageLength(bits);
        byte[] message = readMessage(bits, messageLength);
        String fileExtension = readFileExtension(bits);
        return new ExtractResult(message, fileExtension);
    }

    private SkipByteArrayBitIterator initializeBits(byte[] carrier) {
        // Read the inversion information from the first four bytes
        byte[] inversions = new byte[4];
        for (int i = 0; i < 4; i++) {
            inversions[i] = (byte) (carrier[i] & 0b00000001);
        }
        return new SkipByteArrayBitIterator(4, carrier.length, carrier, inversions);
    }

    private int readMessageLength(SkipByteArrayBitIterator bits) {
        int messageLength = 0;
        for (int i = 0; i < 4; i++) {
            int b = bits.nextByte();
            messageLength = (messageLength << 8) | b;
        }
        return messageLength;
    }

    private byte[] readMessage(SkipByteArrayBitIterator bits, int messageLength) {
        byte[] message = new byte[messageLength];
        for (int i = 0; i < messageLength; i++) {
            message[i] = (byte) bits.nextByte();
        }
        return message;
    }

    private String readFileExtension(SkipByteArrayBitIterator bits) {
        byte[] extensionBytes = new byte[20];
        extensionBytes[0] = (byte) bits.nextByte();
        if (extensionBytes[0] != '.') {
            throw new IllegalStateException("Extension was expected to start with '.'");
        }

        int i;
        byte next = (byte) bits.nextByte();
        for (i = 1; next != '\0'; i++) {
            extensionBytes[i] = next;
            next = (byte) bits.nextByte();
        }

        return new String(extensionBytes, 0, i);
    }

    private int getNextIdxSkipRed(int idx) {
        return idx % 3 == 1 ? idx + 2 : idx + 1;
    }

    private int getPattern(int elem) {
        return (elem >> 1) & 0b00000011;
    }

    private void flipPattern(byte[] carrier, int pattern, int lastIdx) {
        for (int i = 4; i < lastIdx; i = getNextIdxSkipRed(i)) {
            if (getPattern(carrier[i]) == pattern) {
                carrier[i] = (byte) (carrier[i] ^ 0b00000001);
            }
        }
    }
}
