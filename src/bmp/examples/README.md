## lado.bmp

**lado.bmp** is used as the carrier in all cases. Each case contains a hidden `.png` image.

- **ladoLSB1.bmp**: Using the LSB1 method (without encryption).
- **ladoLSB4.bmp**: Using the LSB4 method (without encryption).
- **ladoLSBI.bmp**: Using the LSBI method (without encryption).
- **ladoLSBIaesofbsalt0.bmp**: Using the LSBI method, previously encrypted with AES-256 in OFB mode, password "margarita".

For encryption, the key and IV are derived using the PBKDF2 algorithm with SHA-256, salt: 0x0000000000000000 (8 bytes of zero, expressed in hex), and 10,000 iterations.
- KEY: 03DB0A157ACFE8DE523760AA731D8122B25F8D99F3173EC0B52849F459A4C20D
- IV: 212420EDC583A686A94D19A3497363A2

## river.bmp

**river.bmp** is used as the carrier in all cases. Each case contains a hidden `.png` image.

- **riverLSB1.bmp**: Using the LSB1 method (without encryption).
- **riverLSB4.bmp**: Using the LSB4 method (without encryption).
- **riverLSBI.bmp**: Using the LSBI method (without encryption).