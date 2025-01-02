import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:  ");
        int firstNumber = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:  ");
        int secondNumber = new Scanner(System.in).nextInt();


        int amount = firstNumber + secondNumber;
        System.out.println("1) Вывод суммы        : " + firstNumber + " + " + secondNumber + " = " + amount);

        int composition = firstNumber * secondNumber;
        System.out.println("2) Вывод произведения : " + firstNumber + " * " + secondNumber + " = " + composition);

        int difference = firstNumber - secondNumber;
        System.out.println("3) Вывод разности     : " + firstNumber + " - " + secondNumber + " = " + difference);

        double division = (double) firstNumber / secondNumber;
        System.out.println("4) Вывод частного     : " + firstNumber + " / " + secondNumber + " = " + division);

    }

}
