import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int first = 0;
        int second = 0;
        int third = 0;
        String message = "";
        boolean stop = true;
        while (stop){
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
            ObjectInputStream in_key = new ObjectInputStream(socket_a.getInputStream());
            byte[] open_key = (byte[]) in_key.readObject();
            try {
                FileOutputStream fos = new FileOutputStream("open.pem");
                fos.write(open_key);
                fos.close();
            } catch (IOException e) {
                System.out.println("Error writing file: " + e);
            }

            //получение модуля и экспоненты
            ObjectInputStream in_me = new ObjectInputStream(socket_a.getInputStream());
            byte[] modulus_b = (byte[]) in_me.readObject();
            byte[] exp_b = (byte[]) in_me.readObject();
            String exp_1 = new String(exp_b);

            //Перевод модуля и экспоненты в BigInteger
            BigInteger modulus = new BigInteger(1, modulus_b);
            BigInteger exp = BigInteger.valueOf(Integer.parseInt(exp_1));

            //Перевод сообщения в BigInteger
            BigInteger mes = new BigInteger(1, message.getBytes());

            //генерируем маскировачный множитель
            BigInteger r = generate_r(modulus);

            //замаскированное сообщение
            BigInteger blindedMessage = blindMessage(mes, r, exp, modulus);

            //подготовка сообщения для отправки
            byte[] message_send = hexStringToByteArray(blindedMessage.toString(16));

            //отправка замаскированного сообщения на сервер
            DataOutputStream out_mess = new DataOutputStream(socket_a.getOutputStream());
            ObjectOutputStream objectOut_mess = new ObjectOutputStream(out_mess);
            objectOut_mess.writeObject(message_send);

            //Получаем подписанное замаскированное сообщение
            ObjectInputStream in_messSign = new ObjectInputStream(socket_a.getInputStream());
            byte[] sign_messR = (byte[]) in_messSign.readObject();
            BigInteger signR = new BigInteger(1, sign_messR);

            //Убираем маскировачный множитель => получаем подписанное сообщение
            BigInteger msd = unblindSignature(signR, r, modulus);

            DataOutputStream out_p = new DataOutputStream(socket_b.getOutputStream());
            ObjectOutputStream objectOut_p = new ObjectOutputStream(out_p);
            byte[] message_2 = Files.readAllBytes(Paths.get("message.txt")); //исходное сообщение
            objectOut_p.writeObject(message_2); //отправка сообщения на сервер С
            objectOut_p.writeObject(msd.toByteArray()); //отправка подписанного сообщения на сервер С
            objectOut_p.writeObject(open_key); //отправка открытого ключа на сервер С
            objectOut_p.writeObject(modulus.toByteArray()); //отправка модуля
            objectOut_p.writeObject(exp.toByteArray()); //отправка экспоненты(публичная)

            //отправка выбора для счетчика
            int v = Integer.parseInt(vibor);
            DataOutputStream out_v = new DataOutputStream(socket_b.getOutputStream());
            out_v.writeInt(v);

            //получение данных от сервера(счетчик голосов)
            DataInputStream in_v = new DataInputStream(socket_b.getInputStream());
            first = in_v.readInt();
            second = in_v.readInt();
            third = in_v.readInt();
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
    }

    //Метод для генерации маскировочного множителя
    public static BigInteger generate_r(BigInteger m){
        BigInteger r;
        do {
            r = new BigInteger(m.bitLength(), new SecureRandom()); // Генерация случайного числа r
        } while (r.compareTo(m) >= 0 || !r.gcd(m).equals(BigInteger.ONE)); // Проверка на взаимно простоту с модулем
        return  r;
    }

    // Маскировка сообщения умножением его на маскирующий множитель в степени e(публичная экспонента)
    public static BigInteger blindMessage(BigInteger m, BigInteger r, BigInteger e, BigInteger n) {
        return m.multiply(r.modPow(e, n)).mod(n);
    }

    // Перевод строки в массив байтов
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

    // Снятие маскировки умножением подписанного сообщения на число, обратное маскирующему множителю
    public static BigInteger unblindSignature(BigInteger blindedSignature, BigInteger r, BigInteger n) {
        return blindedSignature.multiply(r.modInverse(n)).mod(n);
    }
}
