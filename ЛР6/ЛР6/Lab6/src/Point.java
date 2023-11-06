class Point {
    private int x;
    private int y;
    private int a;
    private int b;
    private int p;

    // Конструктор
    public Point(int x, int y, int a, int b, int p) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;
        this.p = p;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Проверка, лежит ли точка на кривой
    public boolean isOnCurve() {
        int left = (y * y) % p;
        int right = (x * x * x + a * x + b) % p;
        return left == right;

//        if(((x * x * x + 7 - y * y) % p) == 0){
//            return true;
//        }
//        return false;
    }

    // Операция сложения двух точек
    public Point summ(Point other) {
        if (x == other.getX() && y == other.getY()) {
            // Удвоение точки
            return doublePoint();
        }

        int numerator = other.getY() - y; //числитель
        int denominator = other.getX() - x; //знаменатель

        int s = numerator * multiplicativeInverse(denominator, p)%p;

        int x3 = (s * s - x - other.getX()) % p;
        int y3 = (s * (x - x3) - y) % p;

        if (x3 < 0) x3 += p;
        if (y3 < 0) y3 += p;

        return new Point(x3, y3, a, b, p);
    }

    // Операция удвоения точки
    public Point doublePoint() {
        int numerator = 3 * x * x + a;
        int denominator = 2 * y;

        int s = numerator * multiplicativeInverse(denominator, p) % p;
        int x3 = s * s - 2 * x;
        int y3 = s * (x - x3) - y;

        x3 = (x3 % p + p) % p;
        y3 = (y3 % p + p) % p;

        return new Point(x3, y3, a, b, p);
    }

    // Вспомогательная функция для вычисления обратного элемента в поле p
    private int multiplicativeInverse(int a, int p) {
        a = a % p;
        for (int x = 1; x < p; x++) {
            if ((a * x) % p == 1) {
                return x;
            }
        }
        return -1;
    }

    // Вывод координат точки
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
