import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerC {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int v = 0;
        int first = 0;
        int second = 0;
        int third = 0;
        while (true){
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Waiting for client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            //Прием данных от клиента
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            byte[] message_1 = (byte[]) in.readObject();
            byte[] signature = (byte[]) in.readObject();
            byte[] openkey = (byte[]) in.readObject();
            byte[] modulus = (byte[]) in.readObject();
            byte[] exp = (byte[]) in.readObject();

            //Перевод всех данных в BigInteger
            BigInteger mes = new BigInteger(1, message_1);
            BigInteger s = new BigInteger(1, signature);
            BigInteger mod = new BigInteger(1, modulus);
            BigInteger e = new BigInteger(1, exp);

            //получение выбора
            DataInputStream in_v = new DataInputStream(clientSocket.getInputStream());
            v = in_v.readInt();

            boolean result = verifySignature(s, mes, e, mod); //проверяем подпись
            if(result){
                if(v == 1){
                    first++;
                }
                else if(v == 2){
                    second++;
                }
                else if(v == 3){
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
        }
    }

    //Проверка подлинности голоса возведением подписанного сообщения в степень е(публичная экспонента) и сравнения результата операции с исходным сообщением
    public static boolean verifySignature(BigInteger signature, BigInteger m, BigInteger e, BigInteger n)  {
        // Проверяем подпись
        return signature.modPow(e, n).equals(m);
    }
}
