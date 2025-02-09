import java.util.regex.Pattern;

public enum SwitchRegul {//формат - логический контроль (примитивный из-за нехватки времени) //


    IPADRESS(1){// 72.118.143.231
        //TODO IPADRESS - проверяет только на наличие цифр между ".", нужно отпределять корректный диапазон чисел между "." (0-255)
        public boolean isValid(String str){

            Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
            if (str == null) {
                return LogError(false, " пустой IP");
            }
            return LogError(pattern.matcher(str).matches(), " формат IP");
        }
    },
    DATETIME(2){// 25/Sep/2022:06:25:06 +0300
        //TODO DATETIME - не содержит контроля всех типов формата (здесь нужно определять формат даты и передавать паттерн, для форматирования)
        public boolean isValid(String str){
            Pattern pattern = Pattern.compile("\\d{2}\\/.{3}\\/\\d{4}:\\d{2}:\\d{2}:\\d{2}.{6}");
            if (str == null) {
                return LogError(false, " пустой DATETIME");
            }
            return LogError(pattern.matcher(str).matches(), " формат DATETIME");
        }
    },
    RESTMETOD(3){// GET /parliament/november-reports/content/6377/58/?n=13 HTTP/1.0
        //TODO RESTMETOD - не содержит контроля формата строки запроса
        public boolean isValid(String str){
            Pattern pattern = Pattern.compile("^(?i)(get|put|head|post|delete).*");
            if (str == null) {
                return LogError(false, " пустой http METOD");
            }
            return LogError(pattern.matcher(str).matches(), " формат http METOD");
        }
    },
    REFERER(4){// пустая строка или типа: https://www.nova-news.ru//cooking/?rss=1&p=53&lg=1
        public boolean isValid(String str){
            //TODO REFERER - вообще примитив нужно переделывать
            Pattern pattern = Pattern.compile("(http.*)|-");
            if (str.isBlank()) {
                return true;
            }
            return LogError(pattern.matcher(str).matches(), " формат REFERER");
        }
    },
    USERAGENT(5){// пустая строка или типа: Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.5195.125 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)
        public boolean isValid(String str){
            //TODO USERAGENT - идеально, вот здесь и сделать разбор по содержанию строки , но это сложновато
            Pattern pattern = Pattern.compile("(.*)|-");
            if (str.isBlank()) {
                return true;
            }
            return LogError(pattern.matcher(str).matches(), " формат USERAGENT");
        }
    },
    CODE_LEN(6){// 200 8983
        public boolean isValid(String str){
            Pattern pattern = Pattern.compile("\\d{3} \\d+");
            if (str == null) {
                return LogError(false, " пустой CODE_LEN");
            }
            return LogError(pattern.matcher(str).matches(), " формат CODE_LEN");
        }
    };
        int i;
        private SwitchRegul(int i){
            this.i=i;
        }
        public boolean isValid(String str){
            return false;
        }
        private static boolean LogError(boolean matcher, String error){
            if(!matcher){
                System.out.println("ФЛК. ошибка:"+error);
            }
            return matcher;

        }
}
