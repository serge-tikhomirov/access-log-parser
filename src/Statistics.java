import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class Statistics {
    private long            totalTraffic;
    private LocalDateTime   minTime;
    private LocalDateTime   maxTime;
    private long            usersSessionCounter; //TODO лишний счетчик количества посещений сайта только пользователями (не ботами), можно посчитать через userSessionCounterMap
    private long            wrongResponse; //количество запросов, по которым был ошибочный код (4хх и 5хх)

    private HashMap<String, Integer>    userSessionCounterMap;// Карта отображения посещаемости сайта конкретным пользователей (по IP)
    private HashSet<String>             pageSiteExistSet; // Множество, в котором находятся только существующие страницы сайта
    private HashSet<String>             pageSiteNonExistentSet; // Множество, в котором находятся не существующие страницы сайта
    private HashMap<Long, Integer>      numberOfVisitsForEachSecondMap;// Карта отображения посещаемости сайта в конкретную секунду (Integer для секунд, как написано в задании использовать нельзя)
    private HashSet<String>             sitesWithLinksToCurrentSiteSet;// Множество адресов доменов сайтов, со страниц которых есть ссылки на данный сайт
    private HashMap<String, Integer>    operatingSystemsFrequencyOccurrenceMap;// Карта отображения частоты встречаемости каждой операционной системы
    private HashMap<String, Integer>    browsersFrequencyOccurrenceMap; //Карта отображения частоты встречаемости каждого браузера
    private HashMap<String, Integer>    bot_crawlerFrequencyOccurrenceMap; //Карта отображения частоты встречаемости каждого бота или поисковика
    public Statistics() {
        totalTraffic=0;
        usersSessionCounter =0;
        wrongResponse=0;
        pageSiteExistSet = new HashSet<>();
        pageSiteNonExistentSet = new HashSet<>();
        userSessionCounterMap = new HashMap<>();
        browsersFrequencyOccurrenceMap = new HashMap<>();
        bot_crawlerFrequencyOccurrenceMap = new HashMap<>();
        operatingSystemsFrequencyOccurrenceMap = new HashMap<>();
        numberOfVisitsForEachSecondMap = new HashMap<>();
        sitesWithLinksToCurrentSiteSet = new HashSet<>();
        minTime = LocalDateTime.now(); //заполняем текущей датой
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        maxTime = LocalDateTime.parse("01/01/1970:00:00:00 +0300", formatter); //заполняем заведомо маленькой датой
    }
    public boolean addEntry(LogEntry entry) {
        //в totalTraffic добавляется объём данных, отданных сервером;
        //в minTime и maxTime обновляются данные.
        //в siteExistSet добавляются адреса существующих страниц (с кодом ответа 200) сайта
        //в operatingSystemsFrequencyOccurrenceMap - HashMap, ключи которого это названия операционных систем, а значения — их количествами в лог-файле


        if (!entry.isError()){
            totalTraffic+=entry.getLeng();

            ZoneOffset zone = ZoneOffset.of("Z");

            if (!(entry.getUserAgentObj().isBot()||entry.getUserAgentObj().isCrawler())){// подсчет только пользовательских запросов
                usersSessionCounter++;
            }//TODO лишнее вычисление , у нас есть userSessionCounterMap (оставлю для сравнения)

            if (((entry.getCode()/100)==4) || ((entry.getCode()/100)==5)){//  подсчет ошибочных ответов с кодами 4хх или 5хх
                wrongResponse++;
            }

            if(minTime.toEpochSecond(zone) > entry.getDate_time().toEpochSecond(zone)){
                minTime = entry.getDate_time();
            }
            if(maxTime.toEpochSecond(zone) < entry.getDate_time().toEpochSecond(zone)){
                maxTime = entry.getDate_time();
            }

            if(entry.getCode()==200){
                pageSiteExistSet.add(entry.getPath());
            }

            if(entry.getCode()==404){
                pageSiteNonExistentSet.add(entry.getPath());
            }

            if (!entry.getUserAgentObj().isBot() && !entry.getUserAgentObj().isCrawler()){
                userSessionCounterMap.put(
                        entry.getIpAdr(),
                        userSessionCounterMap.getOrDefault(entry.getIpAdr(),0)+1);
            }
/*
             альтернатива добавления в Map, более явная, но больше кода
            operatingSystemsFrequencyOccurrenceMap.putIfAbsent(entry.getUserAgentObj().getOperationSystem(),0);
            operatingSystemsFrequencyOccurrenceMap.computeIfPresent(entry.getUserAgentObj().getOperationSystem(),(k,v)->v+1);
*/
            if (!Objects.equals(entry.getUserAgentObj().getOperationSystem(), "None")){
                operatingSystemsFrequencyOccurrenceMap.put(
                        entry.getUserAgentObj().getOperationSystem(),
                        operatingSystemsFrequencyOccurrenceMap.getOrDefault(entry.getUserAgentObj().getOperationSystem(),0)+1);
            }

            if (!Objects.equals(entry.getUserAgentObj().getBrowser(), "None")){
                browsersFrequencyOccurrenceMap.put(
                        entry.getUserAgentObj().getBrowser(),
                        browsersFrequencyOccurrenceMap.getOrDefault(entry.getUserAgentObj().getBrowser(),0)+1);
            }

            if (!Objects.equals(entry.getUserAgentObj().getNameBotOrCrawler(), "None")){
                bot_crawlerFrequencyOccurrenceMap.put(
                        entry.getUserAgentObj().getNameBotOrCrawler(),
                        bot_crawlerFrequencyOccurrenceMap.getOrDefault(entry.getUserAgentObj().getNameBotOrCrawler(),0)+1);
            }

            if (!entry.getUserAgentObj().isBot() && !entry.getUserAgentObj().isCrawler()){
                numberOfVisitsForEachSecondMap.put(
                        entry.getDate_time().toEpochSecond(zone),
                        numberOfVisitsForEachSecondMap.getOrDefault(entry.getDate_time().toEpochSecond(zone),0)+1);
            }

            if ((!entry.getReferer().isBlank())&&entry.getReferer().contains("://")){
                sitesWithLinksToCurrentSiteSet.add(entry.getReferer().split("://")[1].split("/")[0]);
            }


            return true;
        } else {
            return false;
        }


    }
    public long getTotalTraffic() {
        return totalTraffic;
    }
    public double getTrafficRate(){
        return calculatingAveragePerHour(totalTraffic, 0);
    }
    public HashSet<String> getPageSiteExistSet(){
        return (HashSet<String>) pageSiteExistSet.clone();
    }
    public HashSet<String> getPageSiteNonExistentSet(){
        return (HashSet<String>) pageSiteNonExistentSet.clone();
    }

    public HashMap<String,Double> getOperatingSystemsStatisticMap(){
        return calculatingHashMapStatist(operatingSystemsFrequencyOccurrenceMap);
     }
    public HashMap<String,Double> getBrowsersStatisticMap(){
        return calculatingHashMapStatist(browsersFrequencyOccurrenceMap);
     }
    public HashMap<String,Double> getBotcrawlerStatisticMap(){
        return calculatingHashMapStatist(bot_crawlerFrequencyOccurrenceMap);
    }
    public double calculatingAverageNumberSiteVisits(){
        return calculatingAveragePerHour(userSessionCounterMap.values().stream().reduce(0, Integer::sum), 2);
        //return userSessionCounterMap.values().stream().reduce(0, (a, b) -> a + b); //альтернатива c лямбдой, но менее понятная
    }
    public double calculatingAverageNumberErrRequests(){
        return calculatingAveragePerHour(wrongResponse, 2);
    }
    public double calculatingAverageAttendance1User(){//usersSessionCounter
        if(!userSessionCounterMap.isEmpty()){
            return  (double) userSessionCounterMap.values().stream().reduce(0, Integer::sum)
                    /  userSessionCounterMap.size();
        } else {
            return 0;
        }
    }
    public int calculatingMaxAttendance1User(){
        return  userSessionCounterMap.values().stream().reduce(0, Integer::max);
    }
    public int peakNumOfVisits(){
        return numberOfVisitsForEachSecondMap.values().stream().reduce(0, Integer::max);
    }
    public HashSet<String> getSitesWithLinksToCurrentSiteSet(){
        return (HashSet<String>) sitesWithLinksToCurrentSiteSet.clone();
    }



    @Override
    public String toString() {
        return "Statistics  {" +"\n"+
                "  totalTraffic=" + totalTraffic +
                ", minTime     =" + minTime +
                ", maxTime     =" + maxTime +
                ", trafficRate =" + getTrafficRate()/(1024) +" KB/h\n"+
                ", statistic OperatingSystems= " + getOperatingSystemsStatisticMap().toString() +"\n"+
                ", statistic Browsers        = " + getBrowsersStatisticMap().toString() +"\n"+
                ", statistic Bots & Crawlers = " + getBotcrawlerStatisticMap().toString() +"\n"+
                ", calculating Average Number Site Visits =" + calculatingAverageNumberSiteVisits() +" per h.\n"+
                ", calculating Average Number Err Requests=" + calculatingAverageNumberErrRequests() +" per h.\n"+
                ", calculating Average Attendance 1User   =" + calculatingAverageAttendance1User() +" \n"+
                ", calculating Maximum Attendance 1User   =" + calculatingMaxAttendance1User() +" \n"+
                ", peak Number Of Visits (Per Second)     =" + peakNumOfVisits() +" per s.\n"+
                ", Sites With Links To the Current Site   =" + getSitesWithLinksToCurrentSiteSet().toString() +"\n"+
                '}';
    }

    private double calculatingAveragePerHour(long numerator, long accurancy){
        // вычисляет разницу между maxTime и minTime в часах и
        // numerator делится на эту разницу, c округлением до нужной точности, по правилам математики
        ZoneOffset zone = ZoneOffset.of("Z");
        if (accurancy>0){
            accurancy*=10;
        } else {
            accurancy=1;
        }

        double value = (maxTime.toEpochSecond(zone)-minTime.toEpochSecond(zone))/3600.0;
        if(value > 0){
            value = numerator / value;
            return Math.round(value*accurancy*10)/(accurancy*10.0);
        } else {
            return 0;
        }
    }


    private HashMap<String,Double> calculatingHashMapStatist(HashMap<String, Integer> map){
        int sum = 0;
        HashMap<String,Double> statist = new HashMap<>();
        for(int i=0; i<map.keySet().toArray().length; i++){
            sum+=map.getOrDefault((String) map.keySet().toArray()[i],0);
        }
        for(int i=0; i<map.keySet().toArray().length; i++){
            Double tmp = (double) map.getOrDefault((String) map.keySet().toArray()[i],0) / sum;
            statist.put((String) map.keySet().toArray()[i], (tmp) );
        }
        return statist;
    }

}
