import java.util.Objects;

public class UserAgent {

    private final String operationSystem;
    private final String browser;

    public UserAgent(String userAgent) {

        operationSystem = containsOS(userAgent);
        browser         = containsBR(userAgent);


    }
    public String getOperationSystem() {
            return operationSystem;
    }
    public String getBrowser() {
            return browser;
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "operationSystem='" + operationSystem + '\'' +
                ", browser='" + browser + '\'' +
                '}';
    }
    private String containsOS(String str){
        String os;
        String[] text_Part_c = str.split("\\(");
        if (text_Part_c.length > 1){
            String[] text_Part_i = text_Part_c[1].split("\\)");
            if (text_Part_i.length > 1){
                os =text_Part_i[0].trim();
            } else{
                return "None";
            }
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
        return "Other";//Other означает, что строка есть, но из нее, не выделено данное свойство из за отсутствия в справочнике поиска getArrOfOS()
                       // например Android, совместимые ОС (compatible), CrOS, LINUX for TV
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

    public String[] getArrOfOS(){
        String[] arr ={"Windows","Mac OS","Linux"}; //список операционных систем для поиска
        return arr;
    }
    public String[] getArrOfBrowser(){
        String[] arr ={"Edge","Firefox","Chrome", "Opera"}; //список браузеров для поиска синхронизируется с классом CheckBrowser
        return arr;
    }
}
