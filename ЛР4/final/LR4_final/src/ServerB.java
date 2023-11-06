import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ServerB {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        while (true){
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Waiting for client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            //Создаем открытый и закрытый ключ
            Process genpkeyProcess = Runtime.getRuntime().exec("openssl genpkey -algorithm RSA -out privatekey.pem -pkeyopt rsa_keygen_bits:1024");
            genpkeyProcess.waitFor();
            Process rsaProcess = Runtime.getRuntime().exec("openssl rsa -pubout -in privatekey.pem -out publickey.pem");
            rsaProcess.waitFor();

            //Отправляем ключ клиенту
            DataOutputStream out_key = new DataOutputStream(clientSocket.getOutputStream());
            ObjectOutputStream objectOut_key = new ObjectOutputStream(out_key);
            byte[] open_key = Files.readAllBytes(Paths.get("publickey.pem"));
            objectOut_key.writeObject(open_key); //открытый ключ

            //Получаем модуль
            Process modul_exp = Runtime.getRuntime().exec("openssl rsa -in privatekey.pem -noout -modulus");
            modul_exp.waitFor();
            InputStream m = modul_exp.getInputStream();
            BufferedReader br_m = new BufferedReader(new InputStreamReader(m));
            String line, modulus = "";
            while ((line = br_m.readLine()) != null){
                modulus += line.trim();
                //System.out.println(line);
            }
            modulus = modulus.split("=")[1];
            byte[] modulus_b = hexStringToByteArray(modulus);

            //Получение публичной экспоненты
//            Process exp_c = Runtime.getRuntime().exec("openssl rsa -in privatekey.pem -noout -text | awk '/publicExponent/' | awk '{print $2}' | xargs printf \"%d\\n\" > public_exponent_int.txt");
//            exp_c.waitFor();
            String[] command = { "openssl", "rsa", "-in", "privatekey.pem", "-noout", "-text" };
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line_1;
            String publicExponent = "";
            while ((line_1 = reader.readLine()) != null) {
                // Поиск строки, содержащей "publicExponent"
                if (line_1.contains("publicExponent")) {
                    // Разбиение строки на отдельные части
                    String[] parts = line_1.trim().split("\\s+");
                    // Поиск значения после "publicExponent"
                    publicExponent = parts[1];
                    break;
                }
            }
            reader.close();
            // Запись результата в файл
            File file = new File("public_exponent_int.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(publicExponent);
            writer.close();

            BufferedReader br = new BufferedReader(new FileReader("public_exponent_int.txt"));
            String exp = br.readLine();
            br.close();
            exp = exp.replaceAll("[^\\d]", "");
            byte[] exp_b = exp.getBytes();

            // Вывод информации о структуре сгенерированного закрытого ключа
            Process process_s = Runtime.getRuntime().exec("openssl rsa -in privatekey.pem -noout -text -modulus");
            Scanner scanner = new Scanner(process_s.getInputStream());
            StringBuilder output = new StringBuilder();
            while (scanner.hasNextLine()) {
                output.append(scanner.nextLine() + "\n");
            }
            scanner.close();

            // Извлечение частной экспоненты
            String privateExponent = output.substring(output.indexOf("privateExponent:"), output.indexOf("prime1:") - 1).split("privateExponent:")[1].replaceAll(" ", "").replaceAll(":", "").replaceAll("\n", "");
            BigInteger prExp = new BigInteger(1, hexStringToByteArray(privateExponent));

            //отправка модуля и экспоненты(публичная) на клиент
            DataOutputStream out_exp_mod = new DataOutputStream(clientSocket.getOutputStream());
            ObjectOutputStream objectOut_em = new ObjectOutputStream(out_exp_mod);
            objectOut_em.writeObject(modulus_b);
            objectOut_em.writeObject(exp_b);

            //получение замаскированного сообщения от клиента
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            byte[] message = (byte[]) in.readObject();

            //Слепая подпись замаскированного сообщения
            BigInteger sign = signBlindedMessage(new BigInteger(1, message), prExp, new BigInteger(1, modulus_b));

            //Отправка подписанного сообщения клиенту
            DataOutputStream out_sign = new DataOutputStream(clientSocket.getOutputStream());
            ObjectOutputStream objectOut_sign = new ObjectOutputStream(out_sign);
            byte[] si = sign.toByteArray();
            objectOut_sign.writeObject(si);

            //удаление временнных файлов
            Files.deleteIfExists(Paths.get("publickey.pem"));
            Files.deleteIfExists(Paths.get("privatekey.pem"));
            Files.deleteIfExists(Paths.get("public_exponent_int.txt"));
            clientSocket.close();
            serverSocket.close();
        }
    }

    //Перевод строки в массив байтов
    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }

    //Подпись замаскированного сообщения(возводим его в степень d, по модулю n). Приватная экспонента.
    public static BigInteger signBlindedMessage(BigInteger blindedMessage, BigInteger d, BigInteger n) {
        return blindedMessage.modPow(d, n);
    }
}
