public class Main {
    public static void main(String[] args) throws IOException {
        String hash1;
        String hash2;
        Boolean a = true;

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
        hash1 = hashArray[hashArray.length - 1];

        System.out.println("Хеш-код файла: " + hash1);

        // Имя файла, который будем генерировать
        String newFileName = "newFile.txt";

        // Имя файла-источника
        String sourceFileName = "leasing.txt";

        // Открываем файл-источник на чтение
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
            // Открываем новый файл на запись
            try (FileWriter writer = new FileWriter(newFileName)) {
                String line = reader.readLine();

                // Перебираем все строки файла-источника
                while (line != null) {
                    StringBuilder newLine = new StringBuilder();

                    // Перебираем все символы в строке, заменяем пробел на управляющий символ пробел с вероятностью 50%
                    for (char ch : line.toCharArray()) {
                        if (ch == ' ') {
                            if (Math.random() < 0.5) {
                                newLine.append(' ');
                            } else {
                                newLine.append('\u200B'); // Управляющий символ пробел
                            }
                        } else {
                            newLine.append(ch);
                        }
                    }

                    // Записываем новую строку в новый файл
                    writer.write(newLine.toString());
                    writer.write(System.lineSeparator());

                    // Читаем следующую строку из файла-источника
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Команда OpenSSL для получения SHA-256 хеш-кода файла
        command = "openssl dgst -sha256 newFile.txt";

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

        if(hash1 == hash2){
            System.out.println("Хеш коды одинаковые и равны = " + hash1);
        }
        else {
            int i = 0;
            while (a){
                //удаление файла
                File file = new File("newFile.txt");
                file.delete();

                // Имя файла, который будем генерировать
                newFileName = "newFile.txt";

                // Имя файла-источника
                sourceFileName = "leasing.txt";

                // Открываем файл-источник на чтение
                try (BufferedReader reader = new BufferedReader(new FileReader(sourceFileName))) {
                    // Открываем новый файл на запись
                    try (FileWriter writer = new FileWriter(newFileName)) {
                        String line = reader.readLine();

                        // Перебираем все строки файла-источника
                        while (line != null) {
                            StringBuilder newLine = new StringBuilder();

                            // Перебираем все символы в строке, заменяем пробел на управляющий символ пробел с вероятностью 50%
                            for (char ch : line.toCharArray()) {
                                if (ch == ' ') {
                                    if (Math.random() < 0.5) {
                                        newLine.append(' ');
                                    } else {
                                        newLine.append('\u200B'); // Управляющий символ пробел
                                    }
                                } else {
                                    newLine.append(ch);
                                }
                            }

                            // Записываем новую строку в новый файл
                            writer.write(newLine.toString());
                            writer.write(System.lineSeparator());

                            // Читаем следующую строку из файла-источника
                            line = reader.readLine();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Команда OpenSSL для получения SHA-256 хеш-кода файла
                command = "openssl dgst -sha256 newFile.txt";

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

                if(hash1 == hash2){
                    a = false;
                    System.out.println("Хеш-коды одинаковые! И кол-во попыток = " + i);
                }
                else {
                    i++;
                    if(i > 50){
                        a = false;
                    }
                }
            } //конец файла
            if(i > 50){
                System.out.println("Коллизия не найдена");
            }
        }
    }
}
