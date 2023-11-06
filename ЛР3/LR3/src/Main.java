import java.io.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws Exception {

        // генерация ключа
        String keyPath = "key.txt";

        String command = "openssl rand -hex 16";

        Process process = Runtime.getRuntime().exec(command);

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String key = reader.readLine();

        File file = new File(keyPath);
        FileWriter fw = new FileWriter(file);
        fw.write(key);
        fw.close();

        // зашифровка и расшифровка изображения в режиме ECB
        String fileECB1 = new File("temp\\ECB1.txt").getPath();
        String fileECB2 = new File("temp\\ECB2.txt").getPath();
        String de_ECB = new File("de_ECB.bmp").getPath();
        encryptImage("ECB", "tux.bmp", keyPath, "tux_ecb.bmp",fileECB1, fileECB2 );
        decryptImage("ECB", "tux_ecb.bmp", keyPath, de_ECB,fileECB1, fileECB2 );

        // зашифровка и расшифровка изображения в режиме CBC
        String fileCBC1 = new File("temp\\CBC1.txt").getPath();
        String fileCBC2 = new File("temp\\CBC2.txt").getPath();
        String de_CBC = new File("de_CBC.bmp").getPath();
        encryptImage("CBC", "tux.bmp", keyPath, "tux_cbc.bmp", fileCBC1, fileCBC2);
        decryptImage("CBC", "tux_cbc.bmp", keyPath, de_CBC, fileCBC1, fileCBC2);

        // зашифровка и расшифровка изображения в режиме CFB
        String fileCFB1 = new File("temp\\CFB1.txt").getPath();
        String fileCFB2 = new File("temp\\CFB2.txt").getPath();
        String de_CFB = new File("de_CFB.bmp").getPath();
        encryptImage("CFB", "tux.bmp", keyPath, "tux_cfb.bmp", fileCFB1, fileCFB2);
        decryptImage("CFB", "tux_cfb.bmp", keyPath, de_CFB, fileCFB1, fileCFB2);

        // зашифровка изображения в режиме OFB
        String fileOFB1 = new File("temp\\OFB1.txt").getPath();
        String fileOFB2 = new File("temp\\OFB2.txt").getPath();
        String de_OFB = new File("de_OFB.bmp").getPath();
        encryptImage("OFB", "tux.bmp", keyPath, "tux_ofb.bmp", fileOFB1, fileOFB2);
        decryptImage("OFB", "tux_ofb.bmp", keyPath, de_OFB, fileOFB1, fileOFB2);

        // вывод информации о файлах
        File tuxFile = new File("tux.bmp");
        File tuxEcbFile = new File("tux_ecb.bmp");
        File tuxCbcFile = new File("tux_cbc.bmp");
        File tuxCfbFile = new File("tux_cfb.bmp");
        File tuxOfbFile = new File("tux_ofb.bmp");

        System.out.println("Оригинальное изображение: " + tuxFile.length() + " bytes");
        System.out.println("ECB image: " + tuxEcbFile.length() + " bytes");
        System.out.println("CBC image: " + tuxCbcFile.length() + " bytes");
        System.out.println("CFB image: " + tuxCfbFile.length() + " bytes");
        System.out.println("OFB image: " + tuxOfbFile.length() + " bytes");

        System.out.println("Шифрование и расшифрование завершено успешно.");
    }

    private static void encryptImage(String mode, String inputFilename, String keyFilename, String outputFilename, String file1, String file2) throws Exception {

        //читаем исходный файл
        FileInputStream inputStream = new FileInputStream(inputFilename);
        byte[] header = new byte[110];
        inputStream.read(header); //получаем заголовок
        byte[] imageBytes = new byte[inputStream.available()];
        inputStream.read(imageBytes); //получаем тело
        inputStream.close();

        //записываем в отдельный файл тело изображения
        FileOutputStream outputStream = new FileOutputStream(file1);
        outputStream.write(imageBytes);
        outputStream.close();

        // зашифровка тела файла
        String encryptCommand = ("openssl enc -aes-256-" + mode + " -in " + file1 + " -out " + file2 + " -pass file:" + keyFilename);
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(encryptCommand);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Command execution failed with exit code " + exitCode);
        }

        //получения массива байтов зашифрованного тела
        FileInputStream inputStream1 = new FileInputStream(file2);
        byte[] shBytes = new byte[inputStream1.available()];
        inputStream1.read(shBytes);
        inputStream1.close();

        //собираем изображение вместе
        FileOutputStream outputStream1 = new FileOutputStream(outputFilename);
        outputStream1.write(header);
        outputStream1.write(shBytes);
        outputStream1.close();
    }
    private static void decryptImage(String mode, String inputFilename, String keyFilename, String outputFilename, String file1, String file2) throws Exception {

        FileInputStream inputStream = new FileInputStream(inputFilename);
        byte[] header = new byte[110];
        inputStream.read(header); //получаем заголовок
        byte[] imageBytes = new byte[inputStream.available()];
        inputStream.read(imageBytes); //получаем тело
        inputStream.close();

        //записываем в отдельный файл тело цикла
        FileOutputStream outputStream = new FileOutputStream(file1);
        outputStream.write(imageBytes);
        outputStream.close();

        // зашифровка тела файла
        String encryptCommand = ("openssl enc -d -aes-256-" + mode + " -in " + file1 + " -out " + file2 + " -pass file:" + keyFilename);
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(encryptCommand);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Command execution failed with exit code " + exitCode);
        }

        //получения массива байтов зашифрованного тела
        FileInputStream inputStream1 = new FileInputStream(file2);
        byte[] shBytes = new byte[inputStream1.available()];
        inputStream1.read(shBytes);
        inputStream1.close();

        //собираем изображение вместе
        FileOutputStream outputStream1 = new FileOutputStream(outputFilename);
        outputStream1.write(header);
        outputStream1.write(shBytes);
        outputStream1.close();
    }
}