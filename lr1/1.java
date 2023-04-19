import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {

        // Команда OpenSSL для получения SHA-256 хеш-кода файла
        String command = "openssl dgst -sha256 leasing.txt";

        // Выполнение команды в командной строке
        Process process = Runtime.getRuntime().exec(command);

        // Получение вывода команды
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String output = br.readLine().trim();

        // Парсинг вывода
        String[] hashArray = output.split("\\s+");
        String hash = hashArray[hashArray.length - 1];

        System.out.println("Хеш-код файла: " + hash);
    }
}
