package aie.vpnLibrary.messages.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("Image Stream must be not null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        byte[] b = new byte[1024];
        while ((read = inputStream.read(b)) > 0) {

            baos.write(b, 0, read);
        }
        return baos.toByteArray();
    }

    public static byte[] intToBytes(final int data) {
        return new byte[]{
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data) & 0xff),
        };
    }

    public static int convertByteArrayToInt(byte[] data) {
        if (data == null || data.length != 4) return 0x0;
        // ----------
        return (int) ( // NOTE: type cast not necessary for int
                (0xff & data[0]) << 24 |
                        (0xff & data[1]) << 16 |
                        (0xff & data[2]) << 8 |
                        (0xff & data[3]));
    }
}
