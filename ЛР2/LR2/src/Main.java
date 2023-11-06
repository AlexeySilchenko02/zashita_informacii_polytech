import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Main {
    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(new File("28.bmp"));
            File file = new File("28.bmp");
            int fileSize = (int) file.length();

            //Загрузка файла leasing.txt и вычисление SHA-1 хеш-кода.
            FileInputStream fis = new FileInputStream("leasing.txt");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] payload = new byte[fis.available()];
            fis.read(payload);
            byte[] hash = md.digest(payload);

            // Ключ
            //int [] key = {13, 26, 42, 50, 60, 72, 84, 92, 109, 121, 145, 150, 163, 171, 191, 201, 211, 220, 231, 243}; //20

            int[] key = generatKey(20, fileSize);

            // Замена LSB (наименее значимого бита) в выбранных байтах контейнера.
            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < key.length; i++) {
                int b = key[i] % data.length; //определяется индекс байта входных данных
                for (int j = 0; j < 8; j++) {
                    //Определяем текущий бит хеш-кода, который мы будем записывать в байты входных данных.
                    int bit = (hash[i] >> j) & 1;
                    if (bit == 0) {
                        //Устанавливаем младший бит входных данных равным нулю, используя оператор побитового И с инвертированным числом 1.
                        data[b] &= ~1;
                    } else {
                        //устанавливаем младший бит входных данных равным единице, используя оператор побитового ИЛИ с числом 1.
                        data[b] |= 1;
                    }
                    b++;
                    if (b >= data.length) {
                        break;
                    }
                }
            }

            //Сохранение измененного изображения.
            File output = new File("28_modified.bmp");
            ImageIO.write(image, "bmp", output);

            //Извлечение хеш-кода из измененного файла.
            BufferedImage modifiedImage = ImageIO.read(output); //считываем изображение
            byte[] modifiedData = ((DataBufferByte) modifiedImage.getRaster().getDataBuffer()).getData();
            byte[] extractedHash = new byte[key.length];
            for (int i = 0; i < key.length; i++) {
                //Определяется индекс байта модифицированного изображения, который будет использоваться для чтения битов спрятанного хеш-кода.
                int b = key[i] % modifiedData.length;
                for (int j = 0; j < 8; j++) {
                    //Определяется текущий бит из байта спрятанного хеш-кода.
                    extractedHash[i] |= ((modifiedData[b] & 1) << j);
                    b++;
                    if (b >= modifiedData.length) {
                        break;
                    }
                }
            }

            // Сравнение хешей
            if (MessageDigest.isEqual(hash, extractedHash)) {
                System.out.println("Хеш одинаковый и равен = " + bytesToHex(extractedHash));
            } else {
                System.out.println("Хеш разный");
                System.out.println("Исходный хеш: " + bytesToHex(hash));
                System.out.println("Полученный хеш: " + bytesToHex(extractedHash));
            }

        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }
    // Метод для преобразования массива байтов в шестнадцатеричную строку
    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private static int[] generatKey(int count, int max){
        int min = 0; // с заголовком
        //int min = 122; // без заголовка
        int[] key = new int[count];
        Set<Integer> gen = new HashSet<>();

        Random random = new Random();
        int i = 0;
        while (i < count){
            int next  =  random.nextInt(max - min + 1) + min;
            if(!gen.contains(next)){
                gen.add(next);
                key[i] = next;
                i++;
            }
        }
        return key;
    }
}