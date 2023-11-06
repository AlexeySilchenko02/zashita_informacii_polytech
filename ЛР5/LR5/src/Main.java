import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Main {
    private static final int HEADER_SIZE = 110;
    private static final int BLOCK_SIZE = 8;
    public static void main(String[] args) throws IOException {

        int[] register = {1, 1, 0, 0, 1};  // Начальное значение регистра
        int[] taps = {3, 2};           // Образующий многочлен для конфигурации Галуа x^3 + x^2 + 1
        GaloisLFSR lfsr = new GaloisLFSR(register, taps);
        int[] sequence = lfsr.generate(15);
        System.out.println(Arrays.toString(sequence));

        int numBins = 2; // кол-во групп, на которые разбивается последовательность.
        double[] observedFrequencies = new double[numBins];
        for (int i = 0; i < sequence.length; i++) {
            observedFrequencies[sequence[i]]++;
        }
        double expectedFrequency = sequence.length / (double) numBins;
        double chiSquared = 0;
        for (int i = 0; i < numBins; i++) {
            chiSquared += Math.pow(observedFrequencies[i] - expectedFrequency, 2) / expectedFrequency;
        }

        System.out.println("Chi-squared: " + chiSquared);

        // Загрузка изображения в память
        byte[] imageData = loadFile("tux.bmp");

        // Гаммирование каждого блока изображения
        int blocksCount = (imageData.length - HEADER_SIZE) / BLOCK_SIZE;
        for (int i = 0; i < blocksCount; i++) {
            // Получение следующего 8-битного блока для шифрования
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(imageData, HEADER_SIZE + i * BLOCK_SIZE, block, 0, BLOCK_SIZE);

            // Гаммирование блока и сохранение результата
            byte[] cipherBlock = new byte[BLOCK_SIZE];
            byte[] keyStream = toByteArray(lfsr.generate(BLOCK_SIZE * 8));
            for (int j = 0; j < BLOCK_SIZE; j++) {
                cipherBlock[j] = (byte) (block[j] ^ keyStream[j]);
            }
            System.arraycopy(cipherBlock, 0, imageData, HEADER_SIZE + i * BLOCK_SIZE, BLOCK_SIZE);
        }

        // Сохранение зашифрованного изображения на диск
        saveFile("tux_encrypted.bmp", imageData);


        LFSRDiagram diagram = new LFSRDiagram(sequence);

        // Создаем окно и добавляем в него диаграмму
        Frame frame = new Frame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        frame.add(diagram);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }
    private static byte[] loadFile(String filename) {
        try (RandomAccessFile file = new RandomAccessFile(filename, "r")) {
            byte[] data = new byte[(int) file.length()];
            file.readFully(data);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveFile(String filename, byte[] data) {
        try (RandomAccessFile file = new RandomAccessFile(filename, "rw")) {
            file.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] toByteArray(int[] bits) {
        byte[] bytes = new byte[bits.length / 8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
            for (int j = 0; j < 8; j++) {
                bytes[i] |= (bits[i * 8 + j] << j);
            }
        }
        return bytes;
    }

}