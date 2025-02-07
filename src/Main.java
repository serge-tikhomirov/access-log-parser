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
                    String text_Fragment="";
                    int colLinesOfFile = 0;
                    int maxLengthOfLine = 0;
                    int minLengthOfLine = Integer.MAX_VALUE;
                    int colGooglebot=0;
                    int colYandexbot=0;
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
                        String[] text_Part_c = text_Line.split("\\(");
                        if (text_Part_c.length > 1){
                            String[] text_Part_i = text_Part_c[1].split(";");
                            if (text_Part_i.length > 1){
                                text_Fragment=text_Part_i[1].split("/")[0].trim();
                            }
                        }
                        if(text_Fragment.equals("Googlebot")){
                            colGooglebot++;
                        }
                        if(text_Fragment.equals("YandexBot")){
                            colYandexbot++;
                        }

                    }
                    System.out.println("Общее количество запросов 100%  ("+colLinesOfFile+")");
                    System.out.println("Доля запросов Googlebot "+(double)colGooglebot/colLinesOfFile*100+ " %  (" +colGooglebot+")");
                    System.out.println("Доля запросов YandexBot "+(double)colYandexbot/colLinesOfFile*100+ " %  (" +colYandexbot+")");
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
