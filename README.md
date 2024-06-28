
[![Java CI with Maven](https://github.com/ThomasMiz/stegobmp/actions/workflows/build.yaml/badge.svg)](https://github.com/ThomasMiz/stegobmp/actions/workflows/build.yaml)

# StegoBMP

StegoBMP is a program developed in Java for hiding and extracting files within [BMP](https://learn.microsoft.com/en-us/windows/win32/api/wingdi/ns-wingdi-bitmapfileheader?redirectedfrom=MSDN) images using various 
steganography methods. 

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Usage](#usage)
    - [Embedding a File](#embedding-a-file)
    - [Extracting a File](#extracting-a-file)
- [Steganography algorithms](#steganography-algorithms)
- [Encryption algorithms](#encryption-algorithms)

## [Introduction](#introduction)

Steganography is the science of hiding information within other non-secret text or data. 
Unlike cryptography, which scrambles the information to make it unreadable without the proper key, 
steganography aims to conceal the very existence of the information.

In this project, we hide files within BMP images using various steganography methods. 
BMP images are chosen due to their simple structure and wide usage.

## [Features](#features)

- **Embed a File**: Hide any file inside a BMP image.
- **Extract a File**: Retrieve a hidden file from a BMP image.
- **Support for Multiple Steganography Algorithms**: LSB1, LSB4, and LSB Improved.
- **Optional Encryption**: AES (128, 192, 256) and DES with multiple modes (ECB, CFB, OFB, CBC).

## [Usage](#usage)

Compile the project
```sh 
mvn clean install
```

### [Embedding a File](#embedding-a-file)
> **Note:** The following commands will be executed in the root of the project

To embed a file in a BMP image, use the following arguments or flags:
```sh
# [] are optional parameters
java -jar ./target/stegobmp-1.0-SNAPSHOT.jar -embed -in <file_to_hide> -p <carrier_bmp> -out <output_bmp> -steg <LSB1|LSB4|LSBI> [-a <aes128|aes192|aes256|des>] [-m <ecb|cfb|ofb|cbc>] [-pass <password>]
```

For example:
```sh
# Embed secret.txt in image.bmp using LSB Improved steganography and DES encryption in CBC mode with the password "hidden"
java -jar ./target/stegobmp-1.0-SNAPSHOT.jar -embed -in "secret.txt" -p "image.bmp" -out "output.bmp" -steg LSBI -a des -m cbc -pass "hidden"
```

### [Extracting a File](#extracting-a-file)
To extract a hidden file from a BMP image, use the following arguments or flags in the `Main.java`:
```sh
# [] are optional parameters
java -jar ./target/stegobmp-1.0-SNAPSHOT.jar -extract -p <carrier_bmp> -out <output_file> -steg <LSB1|LSB4|LSBI> [-a <aes128|aes192|aes256|des>] [-m <ecb|cfb|ofb|cbc>] [-pass <password>]
```

For example:
```sh
# Extract secret.txt from output.bmp which was hidden using LSB Improved steganography and DES encryption in CBC mode with the password "hidden"
java -jar ./target/stegobmp-1.0-SNAPSHOT.jar -extract -p "output.bmp" -out "secret.txt" -steg LSBI -a des -m cbc -pass "hidden"
```

## [Steganography algorithms](#steganography-algorithms)

- **LSB1:** Least Significant Bit Insertion (1 bit) hides the information by replacing the least significant bit of each byte in the BMP file.
- **LSB4:** Least Significant Bit Insertion (4 bits) hides the information by replacing the four least significant bits of each byte in the BMP file.
- **LSB Improved:** An improved version of LSB insertion that uses bit-inversion techniques as described by Majeed and Sulaiman in their [paper](src%2Fdocs%2Fimproved_lsb.pdf).

## [Encryption algorithms](#encryption-algorithms)
The tool supports optional encryption for added security using the following algorithms and 
modes via the [Java Cryptography Architecture (JCA)](https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html):

- Algorithms: AES (128, 192, 256), DES
- Modes: ECB, CFB, OFB, CBC
> **Note:** Encryption requires a password.
