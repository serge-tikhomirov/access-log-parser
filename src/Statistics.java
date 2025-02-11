import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private long usersSessionCounter;
    private long wrongResponse;
    private HashMap<String, Integer>  userSessionCounterMap;
    private HashSet<String> siteExistSet;
    private HashSet<String> siteNonExistentSet;
    private HashMap<String, Integer>  operatingSystemsFrequencyOccurrenceMap;
    private HashMap<String, Integer>  browsersFrequencyOccurrenceMap;
    private HashMap<String, Integer>  bot_crawlerFrequencyOccurrenceMap;
    private UserAgent lastUserAgent;
    public Statistics() {
        totalTraffic=0;
        usersSessionCounter =0;
        wrongResponse=0;
        siteExistSet = new HashSet<>();
        siteNonExistentSet = new HashSet<>();
        userSessionCounterMap = new HashMap<>();
        browsersFrequencyOccurrenceMap = new HashMap<>();
        bot_crawlerFrequencyOccurrenceMap = new HashMap<>();
        operatingSystemsFrequencyOccurrenceMap = new HashMap<>();
        minTime = LocalDateTime.now(); //заполняем текущей датой
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        maxTime = LocalDateTime.parse("01/01/1970:00:00:00 +0300", formatter); //заполняем заведомо маленькой датой
    }
    public boolean addEntry(LogEntry entry) {
        //в totalTraffic добавляется объём данных, отданных сервером;
        //в minTime и maxTime обновляются данные.
        //в siteExistSet добавляются адреса существующих страниц (с кодом ответа 200) сайта
        //в operatingSystemsFrequencyOccurrenceMap - HashMap, ключи которого это названия операционных систем, а значения — их количествами в лог-файле

        lastUserAgent = entry.getUserAgentObj();
        if (!entry.isError()){
            totalTraffic+=entry.getLeng();

            ZoneOffset zone = ZoneOffset.of("Z");

            if (!(entry.getUserAgentObj().isBot()||entry.getUserAgentObj().isCrawler())){// подсчет только пользовательских запросов
                usersSessionCounter++;
            }
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
                siteExistSet.add(entry.getPath());
            }

            if(entry.getCode()==404){
                siteNonExistentSet.add(entry.getPath());
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

            return true;
        } else {
            return false;
        }


    }
    public long getTotalTraffic() {
        return totalTraffic;
    }
    public long getTrafficRate(){
        // вычисляет разницу между maxTime и minTime в часах и
        // общий объём трафика делится на эту разницу.

        return calculatingAveragePerHour(totalTraffic);
    }
    public HashSet<String> getSiteExistSet(){
        return (HashSet<String>) siteExistSet.clone();
    }
    public HashSet<String> getSiteNonExistentSet(){
        return (HashSet<String>) siteNonExistentSet.clone();
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

    public long calculatingAverageNumberSiteVisits(){
        return calculatingAveragePerHour(usersSessionCounter);
    }

    public long calculatingAverageNumberErrRequests(){
        return calculatingAveragePerHour(wrongResponse);
    }
    public long calculatingAverageAttendance1User(){
        if(!userSessionCounterMap.isEmpty()){
            return usersSessionCounter/userSessionCounterMap.size();
        } else {
            return 0;
        }

    }

    @Override
    public String toString() {
        return "Statistics  {" +"\n"+
                "  totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", trafficRate=" + getTrafficRate() +" B/h\n"+
                ", statistic OperatingSystems= " + getOperatingSystemsStatisticMap().toString() +"\n"+
                ", statistic Browsers        = " + getBrowsersStatisticMap().toString() +"\n"+
                ", statistic Bots & Crawlers        = " + getBotcrawlerStatisticMap().toString() +"\n"+
                ", calculating Average Number Site Visits=" + calculatingAverageNumberSiteVisits() +" per h.\n"+
                ", calculating Average Number Err Requests=" + calculatingAverageNumberErrRequests() +" per h.\n"+
                ", calculating Average Attendance 1User=" + calculatingAverageAttendance1User() +" \n"+
                '}';
    }

    private long calculatingAveragePerHour(long numerator){
        ZoneOffset zone = ZoneOffset.of("Z");

        long value = (maxTime.toEpochSecond(zone)-minTime.toEpochSecond(zone))/3600;
        if(value > 0){
            return (numerator / value);
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
