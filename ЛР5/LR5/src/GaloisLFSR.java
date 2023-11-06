import java.util.Arrays;

public class GaloisLFSR {
    private int[] register;     // Содержит значения битов регистра
    private int[] taps;         // Содержит номера разрядов, используемых для обратной связи

    public GaloisLFSR(int[] register, int[] taps) {
        this.register = register;
        this.taps = taps;
    }

    public void shift() {
        int feedback = 0;

        // Вычисляем обратную связь
        for (int tap : taps) {
            feedback ^= register[tap];
        }

        // Сдвигаем все биты регистра вправо на 1
        for (int i = register.length - 1; i > 0; i--) {
            register[i] = register[i - 1];
        }

        // Вставляем обратную связь в первый бит регистра
        register[0] = feedback;
    }

    public int[] generate(int length) {
        int[] bits = new int[length];

        for (int i = 0; i < length; i++) {
            // Генерируем следующий бит и записываем его в массив
            bits[i] = register[register.length - 1];

            // Сдвигаем регистр на один бит вправо
            shift();
        }

        return bits;
    }

}
