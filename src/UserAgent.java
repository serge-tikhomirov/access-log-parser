import java.util.Objects;

public class UserAgent {

    private final String operationSystem;
    private String browser;
    private boolean isBot=false;
    private boolean isCrawler=false;
    private final String nameBotOrCrawler;

    public UserAgent(String userAgent) {

        operationSystem = containsOS(userAgent);
        browser         = containsBR(userAgent);
        nameBotOrCrawler= nameBot_Crawler(userAgent);
        collisionDetection();
        if (nameBotOrCrawler.equals("None")){
            this.isBot = false;
            this.isCrawler = false;
        } else {
            if (nameBotOrCrawler.toLowerCase().contains("bot")){
                this.isBot = true;
            } else {
                this.isCrawler = true;
            }
        }

    }
    public String getOperationSystem() {
            return operationSystem;
    }
    public String getBrowser() {
            return browser;
    }
    public boolean isBot() {
        return this.isBot;
    }
    public boolean isCrawler() {
        return this.isCrawler;
    }
    public String getNameBotOrCrawler() {
         return nameBotOrCrawler;
    }

    @Override
    public String toString() {
        if(isBot&&!isCrawler){
            return "UserAgent{" +
                    "name of Bot='" + nameBotOrCrawler + '\'' +
                    ", operationSystem='" + operationSystem + '\'' +
                    ", browser='" + browser + '\'' +
                    '}';
        } else if (isCrawler) {
            return "UserAgent{" +
                    "name of Crawler='" + nameBotOrCrawler + '\'' +
                    ", operationSystem='" + operationSystem + '\'' +
                    ", browser='" + browser + '\'' +
                    '}';
        } else {
            return "UserAgent{" +
                    "name of Browser='" + browser + '\'' +
                    ", operationSystem='" + operationSystem + '\'' +
                    '}';
        }
    }
    private String containsOS(String str){
        String os;
        String[] text_Part_c = str.split("\\(");
        if (text_Part_c.length > 1){
            String[] text_Part_i = text_Part_c[1].split("\\)");
            os =text_Part_i[0];
        }
        else{
            return "None";
        }

        String[] arr = getArrOfOS();
        for(int i=0;i<arr.length;i++){
            if(os.contains(arr[i])){
                return arr[i];
            }
        }
        if (os.contains("compatible")){
            return "None";
        }
        return "Other";//Other означает, что строка есть, но из нее, не выделено данное свойство из за отсутствия в справочнике поиска getArrOfOS()
                       // например Android, CrOS, LINUX for TV
    }

    private String containsBR(String str){
        String[] arr =getArrOfBrowser();
        for(int i=0;i<arr.length;i++){
           if (CheckBrowser.valueOf(arr[i]).isValid(str)){
               return arr[i];
           }
        }
        return "Other";//Other означает, что строка есть, но из нее, не выделено данное свойство из за отсутствия в справочнике getArrOfBrowser()
    }
    private String nameBot_Crawler(String userAgent) {
        String[] text_Part = userAgent.split("compatible;");
        if (text_Part.length > 1){
            if (text_Part[1].contains("http")) {
                return text_Part[1].split("\\+")[0].split(";")[0].split("/")[0].trim();
            } else {
                return "None";
            }
        }
        else {
            return "None";
        }
    }
    private void collisionDetection(){//решение коллизий при определении бота, но неуверенности в наличии браузера
        if (Objects.equals(browser, "Other") && (Objects.equals(operationSystem, "None")||Objects.equals(operationSystem, "Other")) &&!(Objects.equals(nameBotOrCrawler, "None"))){
            browser="None";
        }
    }

    public String[] getArrOfOS(){
        String[] arr ={"Windows","Mac OS","Linux"}; //список операционных систем для поиска
        return arr;
    }
    public String[] getArrOfBrowser(){
        String[] arr ={"Edge","Firefox","Chrome", "Opera", "Safari"}; //список браузеров для поиска синхронизируется с классом CheckBrowser
        return arr;
    }

}
