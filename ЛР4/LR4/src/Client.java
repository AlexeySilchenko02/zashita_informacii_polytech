import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Integer first = 0;
        Integer second = 0;
        Integer third = 0;
        String message = "";
        boolean stop = true;
        while (stop) {
            //подключение к серверам
            Socket socket_a = new Socket("localhost", 12345);
            Socket socket_b = new Socket("localhost", 1234);

            //процесс голосования
            System.out.println("Выберите кандидата, за которого вы хотите проголосовать: ");
            System.out.println("1 - Кандидат 1");
            System.out.println("2 - Кандидат 2");
            System.out.println("3 - Кандидат 3");
            Scanner scanner = new Scanner(System.in);
            String vibor = scanner.nextLine();
            if (vibor.equals("1")) {
                message = "Кандидат 1";
                FileWriter writer = new FileWriter("message.txt");
                writer.write(message);
                writer.close();
            } else if (vibor.equals("2")) {
                message = "Кандидат 2";
                FileWriter writer = new FileWriter("message.txt");
                writer.write(message);
                writer.close();
            } else if (vibor.equals("3")) {
                message = "Кандидат 3";
                FileWriter writer = new FileWriter("message.txt");
                writer.write(message);
                writer.close();
            }

            //получаем открытый ключ и записываем его в файл
            ObjectInputStream in_a_1 = new ObjectInputStream(socket_a.getInputStream());
            byte[] open_key = (byte[]) in_a_1.readObject();
            try {
                FileOutputStream fos = new FileOutputStream("open.pem");
                fos.write(open_key);
                fos.close();
            } catch (IOException e) {
                System.out.println("Error writing file: " + e);
            }

            // Шифруем файл сообщения
            Process encryptProcess = Runtime.getRuntime().exec("openssl rsautl -encrypt -inkey open.pem -pubin -in message.txt -out message.enc");
            encryptProcess.waitFor();

            //отправка зашифрованного сообщения на сервер
            DataOutputStream out_a = new DataOutputStream(socket_a.getOutputStream());
            ObjectOutputStream objectOut_a = new ObjectOutputStream(out_a);
            byte[] message_1 = Files.readAllBytes(Paths.get("message.enc"));
            objectOut_a.writeObject(message_1);

            ObjectInputStream in_a = new ObjectInputStream(socket_a.getInputStream());
            byte[] sign = (byte[]) in_a.readObject(); //подписанное сообщение


            DataOutputStream out_b = new DataOutputStream(socket_b.getOutputStream());
            ObjectOutputStream objectOut = new ObjectOutputStream(out_b);
            byte[] message_2 = Files.readAllBytes(Paths.get("message.txt"));
            objectOut.writeObject(message_2); //отправка сообщения на сервер б
            objectOut.writeObject(sign); //отправка подписанного сообщения на сервер б
            objectOut.writeObject(open_key); //отправка открытого ключа на сервер б

            //отправка выбора для счетчика
//            Integer v = Integer.parseInt(vibor);
//            DataOutputStream out_1 = new DataOutputStream(socket_b.getOutputStream());
//            out_1.writeInt(v);

            //получение данных от сервера(счетчик голосов)
            DataInputStream in_b = new DataInputStream(socket_b.getInputStream());
            first = in_b.readInt();
            second = in_b.readInt();
            third = in_b.readInt();
            System.out.println("Кол-во голосов на данные момент: ");
            System.out.println("За первого кандидата: " + first);
            System.out.println("За второго кандидата: " + second);
            System.out.println("За третьего кондидата: " + third);

            System.out.println("Продолжить голосование? 1-да, 0-нет");
            scanner = new Scanner(System.in);
            vibor = scanner.nextLine();
            if (vibor.equals("0")) {
                    stop = false;
                }
            socket_a.close();
            socket_b.close();
        }
        System.out.println("Голосование завершено! Результаты:");
        System.out.println("За первого кандидата: " + first);
        System.out.println("За второго кандидата: " + second);
        System.out.println("За третьего кондидата: " + third);
        // Удаляем временные файлы
        Files.deleteIfExists(Paths.get("message.txt"));
        Files.deleteIfExists(Paths.get("open.pem"));
        Files.deleteIfExists(Paths.get("message.enc"));
    }
}
