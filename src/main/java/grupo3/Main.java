package grupo3;

import grupo3.bmp.Bitmap;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        try {
            Bitmap bitmap = Bitmap.readFromFile("arshivo.bmp");
            bitmap.writeToFile("arshivo2.bmp");

            Bitmap xd = new Bitmap(2, 2, new byte[]{
                    (byte) 255, (byte) 0, (byte) 0,
                    (byte) 0, (byte) 255, (byte) 0,
                    (byte) 0, (byte) 0, (byte) 255,
                    (byte) 255, (byte) 255, (byte) 255
            });
            xd.writeToFile("crocante.bmp");
        } catch (Exception e) {
            System.out.println("pero la PUCHA");
            System.out.println(e);
        }
    }
}
