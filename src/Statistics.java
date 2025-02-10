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
    private HashSet<String> siteExistSet;
    private HashMap<String, Integer>  operatingSystemsFrequencyOccurrenceMap;
    private UserAgent lastUserAgent;
    public Statistics() {
        totalTraffic=0;
        siteExistSet = new HashSet<>();
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

            if(minTime.toEpochSecond(zone) > entry.getDate_time().toEpochSecond(zone)){
                minTime = entry.getDate_time();
            }
            if(maxTime.toEpochSecond(zone) < entry.getDate_time().toEpochSecond(zone)){
                maxTime = entry.getDate_time();
            }

            if(entry.getCode()==200){
                siteExistSet.add(entry.getPath());
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
    public double getTrafficRate(){
        // вычисляет разницу между maxTime и minTime в часах и
        // общий объём трафика делится на эту разницу.

        ZoneOffset zone = ZoneOffset.of("Z");

        double value = (maxTime.toEpochSecond(zone)-minTime.toEpochSecond(zone))/3600.0;
        if(value > 0){
            return (totalTraffic / value);
        } else {
            return 0;
        }

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

    @Override
    public String toString() {
        return "Statistics{" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", trafficRate=" + getTrafficRate() +
                ", statistic OperatingSystems= " + getOperatingSystemsStatisticMap().toString() +
                '}';
    }
}
