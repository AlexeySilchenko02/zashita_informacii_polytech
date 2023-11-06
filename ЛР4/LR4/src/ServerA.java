import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerA {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        while (true) {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Waiting for client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            // Создаем открытый и закрытый ключ
            Process genpkeyProcess = Runtime.getRuntime().exec("openssl genpkey -algorithm RSA -out privatekey.pem -pkeyopt rsa_keygen_bits:1024");
            genpkeyProcess.waitFor();
            Process rsaProcess = Runtime.getRuntime().exec("openssl rsa -pubout -in privatekey.pem -out publickey.pem");
            rsaProcess.waitFor();

            DataOutputStream out_1 = new DataOutputStream(clientSocket.getOutputStream());
            ObjectOutputStream objectOut_1 = new ObjectOutputStream(out_1);
            //отправить ключ клиенту
            byte[] open_key = Files.readAllBytes(Paths.get("publickey.pem"));
            objectOut_1.writeObject(open_key); //открытый ключ

            //получение сообщения и сохранение в файл
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            byte[] message = (byte[]) in.readObject();

            try {
                FileOutputStream fos = new FileOutputStream("m_ec.txt");
                fos.write(message);
                fos.close();
            } catch (IOException e) {
                System.out.println("Error writing file: " + e);
            }

            //расшифровка сообщения
            Process decryptProcess = Runtime.getRuntime().exec("openssl rsautl -decrypt -inkey privatekey.pem -in message.enc -out message.dec");
            decryptProcess.waitFor();

            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            ObjectOutputStream objectOut = new ObjectOutputStream(out);

            // Подписываем сообщение
            Process dgstProcess = Runtime.getRuntime().exec("openssl dgst -sha256 -sign privatekey.pem -out signature.bin message.dec");
            dgstProcess.waitFor();


            byte[] sign = Files.readAllBytes(Paths.get("signature.bin"));
            objectOut.writeObject(sign); //подписанное сообщение

            //удаляем временные файлы
            Files.deleteIfExists(Paths.get("privatekey.pem"));
            Files.deleteIfExists(Paths.get("publickey.pem"));
            Files.deleteIfExists(Paths.get("message.enc"));
            Files.deleteIfExists(Paths.get("signature.bin"));
            Files.deleteIfExists(Paths.get("m_ec.txt"));
            Files.deleteIfExists(Paths.get("message.dec"));

            clientSocket.close();
            serverSocket.close();
        }
    }
}
