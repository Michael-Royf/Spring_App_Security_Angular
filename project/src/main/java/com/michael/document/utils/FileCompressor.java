package com.michael.document.utils;

import com.michael.document.exceptions.payload.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class FileCompressor {
    public static final String COMPRESSION_ERROR = "Error compressing data";
    public static final String DECOMPRESSION_ERROR = "Error decompressing data";


    public static byte[] compressData(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception exception) {
            throw new ApiException(COMPRESSION_ERROR);
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressData(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
            throw new ApiException(DECOMPRESSION_ERROR);
        }
        return outputStream.toByteArray();
    }
}
