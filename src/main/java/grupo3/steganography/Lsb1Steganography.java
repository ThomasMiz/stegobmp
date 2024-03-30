package grupo3.steganography;

import grupo3.exceptions.CarrierNotLargeEnoughException;
import grupo3.utils.BitIterator;

import java.util.Arrays;

public class Lsb1Steganography implements SteganographyMethod {
    @Override
    public int calculateCarrierSize(int messageSize) {
        return messageSize * 8;
    }

    @Override
    public int calculateHiddenSize(int carrierSize) {
        return carrierSize / 8;
    }

    @Override
    public void hideMessage(byte[] carrier, byte[] message) {
        if (carrier.length < calculateCarrierSize(message.length)) {
            throw new CarrierNotLargeEnoughException();
        }

        BitIterator bits = new BitIterator(message);

        int i;
        for (i = 0; i < carrier.length && bits.hasNextBit(); i++) {
            carrier[i] = (byte) ((carrier[i] & (byte) 0b11111110) | bits.nextBit());
        }

        // Pad the remaining space with zeroes
        for (; i < carrier.length; i++) {
            carrier[i] &= (byte) 0b11111110;
        }
    }

    @Override
    public byte[] extractMessage(byte[] carrier) {
        int maxLength = carrier.length / 8 * 8;
        byte[] message = new byte[maxLength];

        int messageLength = 0;
        for (int i = 0; i < carrier.length; i += 8) {
            // Only in fucking java
            int b0 = Byte.toUnsignedInt(carrier[i]) & 0x01;
            int b1 = Byte.toUnsignedInt(carrier[i + 1]) & 0x01;
            int b2 = Byte.toUnsignedInt(carrier[i + 2]) & 0x01;
            int b3 = Byte.toUnsignedInt(carrier[i + 3]) & 0x01;
            int b4 = Byte.toUnsignedInt(carrier[i + 4]) & 0x01;
            int b5 = Byte.toUnsignedInt(carrier[i + 5]) & 0x01;
            int b6 = Byte.toUnsignedInt(carrier[i + 6]) & 0x01;
            int b7 = Byte.toUnsignedInt(carrier[i + 7]) & 0x01;

            int value = b0 + (b1 << 1) + (b2 << 2) + (b3 << 3) + (b4 << 4) + (b5 << 5) + (b6 << 6) + (b7 << 7);
            if (value == 0) { // TODO: This is not the end condition of the message!
                break;
            }

            message[messageLength++] = (byte) value;
        }

        if (messageLength != message.length) {
            // Again, fucking java. Even C# has proper slices.
            message = Arrays.copyOf(message, messageLength);
        }

        return message;
    }
}
