import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerB {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        //Integer v = 0;
        Integer first = 0;
        Integer second = 0;
        Integer third = 0;
    while (true) {

        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Waiting for client...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected");

        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        byte[] message_1 = (byte[]) in.readObject();
        byte[] signature = (byte[]) in.readObject();
        byte[] openkey = (byte[]) in.readObject();

        //получение выбора
//        DataInputStream in_1 = new DataInputStream(clientSocket.getInputStream());
//        v = in_1.readInt();

        //записываем полученные данные в файлы
        try {
            FileOutputStream fos = new FileOutputStream("m.txt");
            fos.write(message_1);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e);
        }

        try {
            FileOutputStream fos = new FileOutputStream("s.bin");
            fos.write(signature);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e);
        }

        try {
            FileOutputStream fos = new FileOutputStream("k.pem");
            fos.write(openkey);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e);
        }

        //получить содержимое сообщения
        String filePath = "m.txt";
        String fileContents = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        boolean result = verifySignature(); //проверяем подпись
        if(result){
            if(fileContents.equals("Кандидат 1")){
                first++;
            }
            else if(fileContents.equals("Кандидат 2")){
                second++;
            }
            else if(fileContents.equals("Кандидат 3")){
                third++;
            }
        }

        //отправка данных клиенту(счетчик голосов)
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        out.writeInt(first);
        out.writeInt(second);
        out.writeInt(third);
        clientSocket.close();
        serverSocket.close();

        //удаляем временные файлы
        Files.deleteIfExists(Paths.get("k.pem"));
        Files.deleteIfExists(Paths.get("s.bin"));
        Files.deleteIfExists(Paths.get("m.txt"));
    }
}

    public static boolean verifySignature() throws IOException, InterruptedException {
        // Проверяем подпись
        Process verifyProcess = Runtime.getRuntime().exec("openssl dgst -sha256 -verify k.pem -signature s.bin m.txt");
        verifyProcess.waitFor();
        String verifyOutput = new String(verifyProcess.getInputStream().readAllBytes());
        if (verifyOutput.contains("OK")) {
            return true;
        } else {
            return false;
        }
    }
}
