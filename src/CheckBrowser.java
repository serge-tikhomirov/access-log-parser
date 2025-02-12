import java.util.regex.Pattern;

public enum CheckBrowser {
    Firefox(1){//
        //Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0
        public boolean isValid(String str){

            Pattern pattern = Pattern.compile("Mozilla\\/5.0 \\(.*Firefox\\/.*");
            if (str.isBlank()) {
                return false;
            }
            return pattern.matcher(str).matches();
        }
    },
    Chrome(2){//
        //Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36
        public boolean isValid(String str){
            Pattern pattern = Pattern.compile("Mozilla\\/5.0 \\(.*\\).*\\(KHTML.*\\) Chrome\\/.*Safari\\/.*");
            if (str.isBlank()) {
                return false;
            }
            return pattern.matcher(str).matches();
        }
    },
    Opera(3){//
        //Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41
        //Opera/9.60 (Windows NT 6.0; U; en) Presto/2.1.1
        public boolean isValid(String str){
            Pattern pattern = Pattern.compile("Mozilla\\/....\\(.*\\).*\\(KHTML.*\\) Chrome\\/.*Safari\\/.*OPR\\/.*");
            Pattern pattern1 = Pattern.compile("Opera\\/.....\\(.*\\) Presto\\/.*");
            if (str.isBlank()) {
                return false;
            }
            return pattern.matcher(str).matches()||pattern1.matcher(str).matches();
        }
    },
    Edge(4){//
        public boolean isValid(String str){
            //Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59
            Pattern pattern = Pattern.compile("Mozilla\\/....\\(.*\\).*\\(KHTML.*\\) Chrome\\/.*Safari\\/.*Edg\\/.*");
            if (str.isBlank()) {
                return false;
            }
            return pattern.matcher(str).matches();
        }
    },
    Safari(5){//
        public boolean isValid(String str){
            //Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Mobile/15E148 Safari/604.1
            Pattern pattern = Pattern.compile("Mozilla\\/....\\(.*\\).*\\(KHTML.*\\) Version\\/.*Mobile\\/.*Safari\\/.*");
            if (str.isBlank()) {
                return false;
            }
            return pattern.matcher(str).matches();
        }
    };
    int i;
    private CheckBrowser(int i){
        this.i=i;
    }
    public boolean isValid(String str){
        return false;
    }


}
