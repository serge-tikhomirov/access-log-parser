import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    public Statistics() {
        totalTraffic=0;
        minTime = LocalDateTime.now(); //заполняем текущей датой
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        maxTime = LocalDateTime.parse("01/01/1970:00:00:00 +0300", formatter); //заполняем заведомо маленькой датой
    }
    public boolean addEntry(LogEntry entry) {
        //в totalTraffic добавляется объём данных, отданных сервером;
        //в minTime и maxTime обновляются данные.

        if (!entry.isError()){
            totalTraffic+=entry.getLeng();

            ZoneOffset zone = ZoneOffset.of("Z");

            if(minTime.toEpochSecond(zone) > entry.getDate_time().toEpochSecond(zone)){
                minTime = entry.getDate_time();
            }
            if(maxTime.toEpochSecond(zone) < entry.getDate_time().toEpochSecond(zone)){
                maxTime = entry.getDate_time();
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

    @Override
    public String toString() {
        return "Statistics{" +
                "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", trafficRate=" + getTrafficRate() +
                '}';
    }
}
