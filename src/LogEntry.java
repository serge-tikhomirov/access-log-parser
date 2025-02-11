import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private boolean isError =false;
    private final String ipAdr;
    private final LocalDateTime date_time;
    private final Metod  metod;
    private final String path;
    private final int    code;
    private final int    leng;
    private final String referer;
    private final String userAgentStr;
    private final UserAgent userAgentObj;

    public LogEntry(String str){
        String[] strSplit = splitString(str);
        if (strSplit==null){
            isError =true;
            ipAdr="";
            date_time=LocalDateTime.now();
            metod=Metod.NULL;
            path="";
            code=-1;
            leng=-1;
            referer="";
            userAgentStr ="";
            userAgentObj=new UserAgent("");
        } else {
            isError =false;
            ipAdr=strSplit[0];

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            date_time = LocalDateTime.parse(strSplit[1], formatter);

            metod=Metod.valueOf(strSplit[2].split(" ")[0]);
            path=strSplit[2].split(" ")[1];
            code=Integer.parseInt(strSplit[3].split(" ")[0]);
            leng=Integer.parseInt(strSplit[3].split(" ")[1]);
            referer=strSplit[4];
            userAgentStr =strSplit[5];
            userAgentObj=new UserAgent(userAgentStr);

        }

    }

    public String getIpAdr() {
        return ipAdr;
    }
    public LocalDateTime getDate_time() {
        return date_time;
    }
    public Metod getMetod() {
        return metod;
    }
    public String getPath() {
        return path;
    }
    public int getCode() {
        return code;
    }
    public int getLeng() {
        return leng;
    }
    public String getReferer() {
        return referer;
    }
    public String getUserAgentStr() {
        return userAgentStr;
    }
    public UserAgent getUserAgentObj() {
        return userAgentObj;
    }
    public boolean isError() {
        return isError;
    }

    @Override
    public String toString() {
        if (isError){
            return "LogEntry{ не заполнен - "+"isExist=" + isError +" }";
        }
        else {
            return "LogEntry{" +
                    "isError=" + isError +
                    ", ipAdress='" + ipAdr + '\'' +
                    ", date time='" + date_time + '\'' +
                    ", http metod=" + metod +
                    ", http path='" + path + '\'' +
                    ", http code res=" + code +
                    ", length=" + leng +
                    ", referer='" + referer + '\'' +
                    ", userAgentStr='" + userAgentStr + '\'' +
                    ", userAgentObj='" + userAgentObj.toString()+
                    '}';
        }
    }

    private String[] splitString(String str){
        String tmp = str;
        // переформатирование разделителей
        String splitter = "#spliterLogEntry#";
        tmp=tmp.replace(" - - [", splitter);   //разделение IP адреса и даты
        tmp=tmp.replace("] \""  , splitter);   //разделение запроса и IP адреса
        tmp=tmp.replace(" \""   , splitter);   //разделение referer и кода_длины
        tmp=tmp.replace("\" \"" , splitter);   //разделение userAgent и referer
        tmp=tmp.replace("\" "   , splitter);   //разделение кода_длины и запроса
        tmp=tmp.replace("\""    , ""); // конец строки текстового поля

        // разделение по полям
        String[] strSplit = tmp.split(splitter);

        // ФЛК
        if (strSplit==null || strSplit.length==0){
            System.out.println("Левый файл читаем, формат совсем не бъет, ничего не распарсили" );
            return null;
        }
        if(strSplit.length==6){
            boolean check=true;
            check = check && FormatLogicControl.IPADRESS.isValid(strSplit[0]) ;
            check = check && FormatLogicControl.DATETIME.isValid(strSplit[1]);
            check = check && FormatLogicControl.RESTMETOD.isValid(strSplit[2]);
            check = check && FormatLogicControl.CODE_LEN.isValid(strSplit[3]);
            check = check && FormatLogicControl.REFERER.isValid(strSplit[4]);
            check = check && FormatLogicControl.USERAGENT.isValid(strSplit[5]);
            if (check){
                return strSplit;
            } else{
                return null;
            }

        }
        System.out.println("Что то пошло не так, поля неправильно парсятся, количество базовых полей вместо 6 :" + strSplit.length);
        return null;
    }


}
