import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // начало декларации локальных переменных метода
        String  text_PathOfFile = "";    //путь к файлу
        boolean flag_PathExist  = false; //переменная определяет существует ли такой путь в файловой системе
        boolean flag_IsDirectory= false; //переменная определяет, что существующий путь - это дирректория
        File    file_ForCheck;           //интерфейс к атрибутам файла, для проверки
        int     int_ColFile = 0;         //количество верно указанных путей к файлам
        // конец  декларации локальных переменных метода
/*
●     Выполняйте задание в том же проекте AccessLogParser в ветке master.

●     После проверки существования файла и того, что указанный путь является путём именно к файлу, а не к папке, напишите код, который будет построчно читать указанный файл:

FileReader fileReader = new FileReader(path);
BufferedReader reader =
   new BufferedReader(fileReader);
String line;
while ((line = reader.readLine()) != null) {
   int length = line.length();
}
●     Поскольку в данном коде есть целых два места, требующих обязательной обработки исключений,
поместите этот код внутрь конструкции try…catch, внутри которой пропишите обработку всех исключений:

try {
   // code here
} catch (Exception ex) {
   ex.printStackTrace();
}
 */

        while (true){ //TODO бесконечный цикл без возможности штатного завершения программы (требуется заданием)
            System.out.println("Введите путь к файлу и нажмите <Enter>:  ");
            text_PathOfFile  = new Scanner(System.in).nextLine();
            file_ForCheck    = new File(text_PathOfFile);
            flag_PathExist   = file_ForCheck.exists();
            flag_IsDirectory = file_ForCheck.isDirectory();

           if (!flag_PathExist && !flag_IsDirectory){
                System.out.println("Путь указан не верно. По указанному пути ни файл, ни дирректория не существует.");
                continue; //TODO неявная логика (требуется заданием), в некоторых ФС (NFS) просто проверка flag_PathExist работать не будет
                          // (эта проверка не д.б. оторвана от проверки flag_IsDirectory), но правильно использовать конструкцию else if.
                          // см. https://www.techiedelight.com/ru/check-if-file-exists-java/
            }

            if (flag_IsDirectory){
                System.out.println("Путь к файлу указан не верно. Это дирректория.");
                continue; //TODO неявная логика (требуется заданием), правильно использовать конструкцию else if.
            }


            if (isFileExists(file_ForCheck)){
                int_ColFile++;
                System.out.println("Путь к файлу указан верно. Это файл номер: " + int_ColFile);
                try{
                    FileReader fileReader = new FileReader(text_PathOfFile);
                    BufferedReader reader =
                            new BufferedReader(fileReader);
                    String text_Line;
                    int colLinesOfFile = 0;
                    int maxLengthOfLine = 0;
                    int minLengthOfLine = Integer.MAX_VALUE;
                    while ((text_Line = reader.readLine()) != null) {
                        int length = text_Line.length();
                        colLinesOfFile++;
                        if(length < minLengthOfLine){
                            minLengthOfLine = length;
                        }
                        if(length > maxLengthOfLine){
                            maxLengthOfLine = length;
                        }
                        if(length > 1024){
                            throw new OperationAttemptException("В файле встретилась строка длиннее 1024 символов");
                        }
                    }
                    System.out.println("Общее количество строк в файле = "+colLinesOfFile);
                    System.out.println("Длина самой длинной строки в файле = "+maxLengthOfLine);
                    System.out.println("Длина самой короткой строки в файле = "+minLengthOfLine);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            else{
                System.out.println("Статус получить невозможно. Файл еще не сформирован или не доступен для данного JAVA-процесса ");
            }

        }
    }

    public static boolean isFileExists(File file) {//метод достоверно определяет существует ли файл
        return (file.exists() && !file.isDirectory() && file.isFile());
    }


}
