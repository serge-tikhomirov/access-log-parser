import java.util.Objects;

public class UserAgent {

    private final String operationSystem;
    private final String browser;

    public UserAgent(String userAgent) {

        operationSystem = containsOS(userAgent);
        browser         = containsBR(userAgent);


    }
    public String getOperationSystem() {
        if(Objects.equals(operationSystem, "None")){
            return null;
        } else {
            return operationSystem;
        }
    }
    public String getBrowser() {
        if(Objects.equals(browser, "None")){
            return null;
        } else {
            return browser;
        }
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

        String[] arr ={"Windows","Mac OS","Linux"};
        for(int i=0;i<arr.length;i++){
            if(os.contains(arr[i])){
                return arr[i];
            }
        }
        return "Other";//Other означает, что строка есть, но из нее, по каким то причинам не выделено данное свойство
    }

    private String containsBR(String str){
        String[] arr ={"Edge","Firefox","Chrome", "Opera"};
        for(int i=0;i<arr.length;i++){
           if (CheckBrowser.valueOf(arr[i]).isValid(str)){
               return arr[i];
           }
        }
        return "Other";//Other означает, что строка есть, но из нее, по каким то причинам не выделено данное свойство
    }
}
