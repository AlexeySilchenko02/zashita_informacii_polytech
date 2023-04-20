import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {
        String hash1;
        String hash2;
        Boolean a = true;
        // Команда OpenSSL для получения SHA-256 хеш-кода файла
        String command = "openssl dgst -sha1 leasing.txt";

        // Выполнение команды в командной строке
        Process process = Runtime.getRuntime().exec(command);

        // Получение вывода команды
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String output = br.readLine().trim();

        // Парсинг вывода
        String[] hashArray = output.split("\\s+");
        hash1 = hashArray[hashArray.length - 1];

        System.out.println("Хеш-код файла: " + hash1);

        String inputFile = "leasing.txt";
        String outputFile = "output.txt";
        String lineSep = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (Math.random() < 0.5)
                line += "\r\n";
            writer.write(line + lineSep);
        }
        reader.close();
        writer.close();

        // Команда OpenSSL для получения SHA-256 хеш-кода файла
        command = "openssl dgst -sha1 output.txt";

        // Выполнение команды в командной строке
        process = Runtime.getRuntime().exec(command);

        // Получение вывода команды
        is = process.getInputStream();
        br = new BufferedReader(new InputStreamReader(is));

        output = br.readLine().trim();

        // Парсинг вывода
        hashArray = output.split("\\s+");
        hash2 = hashArray[hashArray.length - 1];

        System.out.println("Хеш-код файла 2: " + hash2);
        if(Objects.equals(hash1, hash2)){
            System.out.println("Хеш код одинаковый и составляет: " + hash1);
        }
        else {
            int i = 0;
            while (a){
                File file = new File("output.txt");
                file.delete();
//                if (file.delete()) {
//                    System.out.println("Файл успешно удален");
//                } else {
//                    System.out.println("Не удалось удалить файл");
//                }
                inputFile = "leasing.txt";
                outputFile = "output.txt";
                lineSep = System.getProperty("line.separator");
                reader = new BufferedReader(new FileReader(inputFile));
                writer = new BufferedWriter(new FileWriter(outputFile));
                while ((line = reader.readLine()) != null) {
                    if (Math.random() < 0.5)
                        line += "\r\n";
                    // /0
                    writer.write(line + lineSep);
                }
                reader.close();
                writer.close();

                // Команда OpenSSL для получения SHA-256 хеш-кода файла
                command = "openssl dgst -sha1 output.txt";

                // Выполнение команды в командной строке
                process = Runtime.getRuntime().exec(command);

                // Получение вывода команды
                is = process.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));

                output = br.readLine().trim();

                // Парсинг вывода
                hashArray = output.split("\\s+");
                hash2 = hashArray[hashArray.length - 1];
                if(hash2 == hash1){
                    a = false;
                    System.out.println("Хеш код одинаковый и составляет: " + hash1 + "Кол-во созданных файлов: " + i);
                }
                else {
                    i++;
                    if(i > 500){
                        a = false;
                    }
                }
            }
            if(i>499){
                System.out.println("все плохо");
                System.out.println("Хеш-код файла 2: " + hash2);
            }
        }

    }
}
}
