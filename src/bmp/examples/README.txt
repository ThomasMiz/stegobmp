lado.bmp is used as the carrier in all cases.
Everyone has a hidden .png image.

ladoLSB1.bmp -> using LSB1 method (without encryption)
ladoLSB4.bmp -> using LSB4 method (without encryption)
ladoLSBI.bmp -> using LSBI method (without encryption)

ladoLSBIaesofbsalt0.bmp ->using LSBI method, previously encrypted with aes256, mode ofb, password "margarita"
Derive key and IV with PKDF2 algorithm, using sha256, salt: 0x0000000000000000 (8 bytes in 0, expressed in hex), 10000 iterations
KEY: 03DB0A157ACFE8DE523760AA731D8122B25F8D99F3173EC0B52849F459A4C20D
IV: 212420EDC583A686A94D19A3497363A2