package grupo3.steganography;

import grupo3.exceptions.CarrierNotLargeEnoughException;
import grupo3.utils.*;

import java.util.AbstractMap;
import java.util.HashMap;

public class LsbiSteganography implements SteganographyMethod {

    private String fileExtension;

    public LsbiSteganography() {

    }

    @Override
    public int calculateCarrierSize(int messageSize) {
        // Four extra bytes for storing the length of the message
        messageSize += 4;

        // Calculate the total amount of bits the message has
        int messageSizeBits = messageSize * 8;

        // 4 bits are necessary to store the inversions, 3 will go in the first RGB bytes and the
        // last one will go in the first B byte of the next RGB bytes.
        // The first byte of the message will go in the next G value, and the next R byte will not store anything.
        return 4 + (messageSizeBits * 3) / 2;
    }

    @Override
    public int calculateHiddenSize(int carrierSize) {
        // A carrier of that size will have 2 bits every 3 bytes, since nothing is stored under the R byte.
        int totalSizeBits = carrierSize / 3 * 2;

        // Remove the first four bytes which are used to indicate the length of the message
        int messageSize = totalSizeBits - 4;

        // Make sure we didn't go below 0
        return Math.max(0, messageSize);
    }

    private void hideMessage(byte[] carrier, BitIterator bits) {
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
        while (i < carrier.length && bits.hasNextBit()) {
            byte hiddenBit = bits.nextBit();
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
            if (patterCounter[j].getAppearances() < patterCounter[j].getInversions()*2) {
                carrier[j] |= 0b00000001;
                // TODO, improve. If all the bytes need to be flipped, four iterations are made
                flipPattern(carrier, j, i);
            }
            else {
                carrier[j] &= (byte) 0b11111110;
            }
        }
    }

    @Override
    public void hideMessage(byte[] carrier, byte[] message) {
        if (carrier.length < calculateCarrierSize(message.length)) {
            throw new CarrierNotLargeEnoughException();
        }

        hideMessage(carrier, new ConcatBitIterator(new BigEndianIntBitIterator(message.length), new ByteArrayBigEndianBitIterator(message)));
    }

    @Override
    public void hideMessageWithExtension(byte[] carrier, byte[] message, String fileExtension) {
        byte[] extensionBytes = fileExtension.getBytes();

        // Create a new byte array for the combined message, extension, and null terminator
        byte[] extendedMessage = new byte[message.length + extensionBytes.length + 1];
        System.arraycopy(message, 0, extendedMessage, 0, message.length);
        System.arraycopy(extensionBytes, 0, extendedMessage, message.length, extensionBytes.length);

        // Add the null terminator at the end
        extendedMessage[extendedMessage.length - 1] = 0;

        if (carrier.length < calculateCarrierSize(extendedMessage.length)) {
            throw new CarrierNotLargeEnoughException();
        }

        hideMessage(carrier, new ConcatBitIterator(new BigEndianIntBitIterator(message.length), new ByteArrayBigEndianBitIterator(extendedMessage)));
    }

    @Override
    public byte[] extractMessage(byte[] carrier) {
        SkipByteArrayBitIterator bits = initializeBits(carrier);
        int messageLength = readMessageLength(bits);
        return readMessage(bits, messageLength);
    }

    @Override
    public byte[] extractMessageWithExtension(byte[] carrier) {
        SkipByteArrayBitIterator bits = initializeBits(carrier);
        int messageLength = readMessageLength(bits);
        byte[] message = readMessage(bits, messageLength);
        this.fileExtension = readFileExtension(bits);
        return message;
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

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    private int getNextIdxSkipRed(int idx) {
        return idx % 3 == 1 ? idx + 2 : idx + 1;
    }

    private int getPattern(int elem) {
        return (elem >> 1) & 0b00000011;
    }

    private void flipPattern (byte[] carrier, int pattern, int lastIdx) {
        for (int i=4 ; i < lastIdx ;) {
            if (getPattern(carrier[i]) == pattern) {
                carrier[i] = (byte) (carrier[i] ^ 0b00000001);
            }
            i=getNextIdxSkipRed(i);
        }
    }

}
