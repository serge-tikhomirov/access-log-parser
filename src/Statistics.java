import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private long usersSessionCounter;
    private long wrongResponse;
    private HashMap<String, Integer>  userSessionCounterMap;
    private HashSet<String> siteExistSet;
    private HashMap<String, Integer>  operatingSystemsFrequencyOccurrenceMap;
    private UserAgent lastUserAgent;
    public Statistics() {
        totalTraffic=0;
        usersSessionCounter =0;
        wrongResponse=0;
        siteExistSet = new HashSet<>();
        userSessionCounterMap = new HashMap<>();
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

            if (!entry.getUserAgentObj().isBot() && !entry.getUserAgentObj().isCrawler()){
                userSessionCounterMap.putIfAbsent(entry.getIpAdr(),0);
                userSessionCounterMap.computeIfPresent(entry.getIpAdr(),(k,v)->v+1);
            }

            operatingSystemsFrequencyOccurrenceMap.putIfAbsent(entry.getUserAgentObj().getOperationSystem(),0);
            operatingSystemsFrequencyOccurrenceMap.computeIfPresent(entry.getUserAgentObj().getOperationSystem(),(k,v)->v+1);

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

    public HashMap<String,Double> getOperatingSystemsStatisticMap(){
        int[] freqVal = new int[lastUserAgent.getArrOfOS().length];
        int sum = 0;
        HashMap<String,Double> statist = new HashMap<>();

        for(int i=0; i<lastUserAgent.getArrOfOS().length; i++){
            if (operatingSystemsFrequencyOccurrenceMap.get(lastUserAgent.getArrOfOS()[i])==null){
                freqVal[i] = 0;
            }
            else {
                freqVal[i] = operatingSystemsFrequencyOccurrenceMap.get(lastUserAgent.getArrOfOS()[i]);
            }
            sum+=freqVal[i];
        }
        for(int i=0; i<lastUserAgent.getArrOfOS().length; i++){
            Double tmp = (double) freqVal[i] / sum;
            statist.putIfAbsent(lastUserAgent.getArrOfOS()[i], (tmp) );
        }
        return statist;
    }
    public String toStringOSFrequencyOccurrenceMap(){
        return operatingSystemsFrequencyOccurrenceMap.toString();
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
                ", trafficRate=" + getTrafficRate() +"\n"+
                ", statistic OperatingSystems= " + getOperatingSystemsStatisticMap().toString() +"\n"+
                ", calculating Average Number Site Visits=" + calculatingAverageNumberSiteVisits() +"\n"+
                ", calculating Average Number Err Requests=" + calculatingAverageNumberErrRequests() +"\n"+
                ", calculating Average Attendance 1User=" + calculatingAverageAttendance1User() +"\n"+
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

}
